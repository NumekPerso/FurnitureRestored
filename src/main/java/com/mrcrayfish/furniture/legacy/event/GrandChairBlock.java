package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.entity.SeatEntity;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Chair on two spaces. Only the bottom part drops the item, and each half removes itself when the
 * other one disappears, so a chair always gives back one item (same pattern as the vanilla doors).
 */
public class GrandChairBlock extends FurnitureHorizontalBlock
{
    public static final EnumProperty<BlockPart> PART = BlockPart.PROPERTY;
    private static final double SEAT_HEIGHT = 6 * 0.0625;
    private static final VoxelShape OUTLINE_BOTTOM = Block.box(0, 0, 0, 16, 28.8, 16);
    private static final VoxelShape OUTLINE_TOP = Block.box(0, -16, 0, 16, 12.8, 16);
    private static final VoxelShape COLLISION_BOTTOM = Block.box(0, 0, 0, 16, 8, 16);
    private static final Map<Direction, VoxelShape> COLLISION_TOP = LegacyUtil.horizontalShapes(Direction.NORTH, 0, 0, 0, 16, 12.8, 3);

    public GrandChairBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(PART, BlockPart.BOTTOM));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if(pos.getY() >= level.getMaxBuildHeight() - 1 || !level.getBlockState(pos.above()).canBeReplaced(context))
            return null;
        return super.getStateForPlacement(context);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        level.setBlock(pos.above(), state.setValue(PART, BlockPart.TOP), Block.UPDATE_ALL);
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_CHAIR.trigger(player);
            LegacyTriggers.PLACE_CHAIR_OR_TABLE.trigger(player);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        Direction other = state.getValue(PART) == BlockPart.BOTTOM ? Direction.UP : Direction.DOWN;
        if(direction == other && !neighbor.is(this))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    /* The top part has no drop, but removing it would make the bottom part drop in creative mode */
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
    {
        if(!level.isClientSide() && player.isCreative() && state.getValue(PART) == BlockPart.TOP)
        {
            BlockPos below = pos.below();
            BlockState bottom = level.getBlockState(below);
            if(bottom.is(this))
            {
                level.setBlock(below, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, below, Block.getId(bottom));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return state.getValue(PART) == BlockPart.BOTTOM ? super.getDrops(state, builder) : Collections.emptyList();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(player.isShiftKeyDown())
            return InteractionResult.PASS;
        BlockPos seat = state.getValue(PART) == BlockPart.BOTTOM ? pos : pos.below();
        InteractionResult interaction = SeatEntity.create(level, seat, SEAT_HEIGHT, player, state.getValue(DIRECTION));
        if(!level.isClientSide())
        {
            level.updateNeighbourForOutputSignal(seat, this);
        }
        return interaction;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return level.getEntitiesOfClass(SeatEntity.class, new AABB(pos)).isEmpty() ? 0 : 1;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(PART) == BlockPart.BOTTOM ? OUTLINE_BOTTOM : OUTLINE_TOP;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(PART) == BlockPart.BOTTOM ? COLLISION_BOTTOM : COLLISION_TOP.get(state.getValue(DIRECTION));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }
}
