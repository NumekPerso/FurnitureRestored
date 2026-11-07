package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.core.ModBlocks;
import com.mrcrayfish.furniture.entity.SeatEntity;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Modern couch. The type tells on which side another couch is connected, all colors connect together.
 */
public class ModernCouchBlock extends FurnitureHorizontalBlock
{
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final Map<Direction, Map<Type, VoxelShape>> SHAPES = createShapes();

    public ModernCouchBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(TYPE, Type.BOTH));
    }

    private static Map<Direction, Map<Type, VoxelShape>> createShapes()
    {
        Map<Direction, VoxelShape> base = LegacyUtil.horizontalShapes(Direction.EAST, 1, 3, 0, 15, 9, 16);
        Map<Direction, VoxelShape> backrest = LegacyUtil.horizontalShapes(Direction.EAST, 11, 9, 0, 15, 19, 16);
        Map<Direction, VoxelShape> armrestLeft = LegacyUtil.horizontalShapes(Direction.EAST, 1, 0, 0, 15, 13, 2);
        Map<Direction, VoxelShape> armrestRight = LegacyUtil.horizontalShapes(Direction.EAST, 1, 0, 14, 15, 13, 16);
        Map<Direction, Map<Type, VoxelShape>> shapes = new EnumMap<>(Direction.class);
        for(Direction direction : base.keySet())
        {
            Map<Type, VoxelShape> byType = new EnumMap<>(Type.class);
            for(Type type : Type.values())
            {
                VoxelShape shape = Shapes.or(base.get(direction), backrest.get(direction));
                if(type == Type.LEFT || type == Type.BOTH)
                {
                    shape = Shapes.or(shape, armrestLeft.get(direction));
                }
                if(type == Type.RIGHT || type == Type.BOTH)
                {
                    shape = Shapes.or(shape, armrestRight.get(direction));
                }
                byType.put(type, shape);
            }
            shapes.put(direction, byType);
        }
        return shapes;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION)).get(state.getValue(TYPE));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.getItem() == Items.NAME_TAG && stack.hasCustomHoverName() && stack.getHoverName().getString().equals("jeb_"))
        {
            if(!level.isClientSide())
            {
                BlockState sofa = ModBlocks.SOFA_RAINBOW.get().defaultBlockState().setValue(DIRECTION, state.getValue(DIRECTION));
                level.setBlock(pos, Block.updateFromNeighbourShapes(sofa, level, pos), Block.UPDATE_ALL);
                if(!player.getAbilities().instabuild)
                {
                    stack.shrink(1);
                }
                LegacyTriggers.CREATE_COUCH_JEB.trigger(player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if(stack.getItem() instanceof DyeItem)
        {
            LegacyUtil.tryRecolor(state, level, pos, player, hand, color -> LegacyUtil.get(DecorContent.MODERN_COUCHES, color));
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return SeatEntity.create(level, pos, 5 * 0.0625, player, state.getValue(DIRECTION));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(TYPE, getType(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return state.setValue(TYPE, getType(state, level, pos));
    }

    private static Type getType(BlockState state, BlockGetter level, BlockPos pos)
    {
        Direction facing = state.getValue(DIRECTION);
        boolean left = isCouch(level.getBlockState(pos.relative(facing.getClockWise())), facing);
        boolean right = isCouch(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing);
        if(left && right)
        {
            return Type.NONE;
        }
        return left ? Type.LEFT : right ? Type.RIGHT : Type.BOTH;
    }

    private static boolean isCouch(BlockState other, Direction facing)
    {
        return other.getBlock() instanceof ModernCouchBlock && other.getValue(DIRECTION) == facing;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    public enum Type implements StringRepresentable
    {
        NONE, LEFT, RIGHT, BOTH;

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
