package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Modern bed. BedBlock brings the sleep, the spawn point and the explosion outside the overworld.
 * It has no block entity: BedBlock implements EntityBlock, but the game accepts a null block entity.
 * All colors connect together, as long as the facing and the part are the same.
 */
public class ModernBedBlock extends BedBlock
{
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final Map<Direction, VoxelShape> HEAD_SHAPES = createShapes(LegacyUtil.horizontalShapes(Direction.EAST, 14, 0, 0, 16, 16, 16));
    private static final Map<Direction, VoxelShape> FOOT_SHAPES = createShapes(LegacyUtil.horizontalShapes(Direction.EAST, 0, 0, 0, 2, 8, 16));

    public ModernBedBlock(DyeColor color, Properties properties)
    {
        super(color, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, Type.NONE));
    }

    private static Map<Direction, VoxelShape> createShapes(Map<Direction, VoxelShape> end)
    {
        VoxelShape base = Block.box(0, 4, 0, 16, 8, 16);
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        end.forEach((direction, shape) -> shapes.put(direction, Shapes.or(base, shape)));
        return shapes;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return (state.getValue(PART) == BedPart.HEAD ? HEAD_SHAPES : FOOT_SHAPES).get(state.getValue(FACING));
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if(state == null)
        {
            return null;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos headPos = pos.relative(state.getValue(FACING));
        if(!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP) || !level.getBlockState(headPos.below()).isFaceSturdy(level, headPos.below(), Direction.UP))
        {
            return null;
        }
        return state.setValue(TYPE, getType(state, level, pos));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(!level.isClientSide())
        {
            BlockPos headPos = pos.relative(state.getValue(FACING));
            BlockState head = state.setValue(PART, BedPart.HEAD);
            level.setBlock(headPos, head.setValue(TYPE, getType(head, level, headPos)), Block.UPDATE_ALL);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        BlockState updated = super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
        return updated.is(this) ? updated.setValue(TYPE, getType(updated, level, pos)) : updated;
    }

    private static Type getType(BlockState state, BlockGetter level, BlockPos pos)
    {
        Direction facing = state.getValue(FACING);
        boolean right = isSameBedPart(level.getBlockState(pos.relative(facing.getClockWise())), state);
        boolean left = isSameBedPart(level.getBlockState(pos.relative(facing.getCounterClockWise())), state);
        if(left && right)
        {
            return Type.BOTH;
        }
        return left ? Type.RIGHT : right ? Type.LEFT : Type.NONE;
    }

    private static boolean isSameBedPart(BlockState other, BlockState state)
    {
        return other.getBlock() instanceof ModernBedBlock && other.getValue(FACING) == state.getValue(FACING) && other.getValue(PART) == state.getValue(PART);
    }

    /**
     * Only the head has drops: when one half is broken the game also breaks the other one.
     */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return state.getValue(PART) == BedPart.HEAD ? super.getDrops(state, builder) : Collections.emptyList();
    }

    /**
     * BedBlock halves the fall distance: this bed divides it by four.
     */
    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        super.fallOn(level, state, pos, entity, fallDistance * 0.5F);
    }

    /**
     * Bounces higher than a vanilla bed.
     */
    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity)
    {
        if(entity.isSuppressingBounce())
        {
            super.updateEntityAfterFallOn(level, entity);
            return;
        }
        Vec3 motion = entity.getDeltaMovement();
        if(motion.y < 0.0)
        {
            double bounce = entity instanceof LivingEntity ? 0.8 : 0.64;
            entity.setDeltaMovement(motion.x, -motion.y * bounce, motion.z);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    public enum Type implements StringRepresentable
    {
        LEFT, RIGHT, BOTH, NONE;

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
