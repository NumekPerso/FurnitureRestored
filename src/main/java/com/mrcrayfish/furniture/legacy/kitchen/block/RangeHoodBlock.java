package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

/**
 * Range hood, on when powered. A hood above another one with the same direction only shows its
 * vent.
 */
public class RangeHoodBlock extends FurnitureHorizontalBlock
{
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    private static final Map<Direction, VoxelShape> VENT = LegacyUtil.horizontalShapes(Direction.EAST, 8, 0, 3.2, 16, 16, 12.8);
    private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 4, 16);
    private static final Map<Direction, VoxelShape> FULL = new EnumMap<>(Direction.class);

    static
    {
        VENT.forEach((direction, shape) -> FULL.put(direction, Shapes.or(shape, BASE)));
    }

    public RangeHoodBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(DOWN, false).setValue(POWERED, false));
    }

    private static boolean isHood(BlockState below, Direction direction)
    {
        return below.getBlock() instanceof RangeHoodBlock && below.getValue(DIRECTION) == direction;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
        return state.setValue(DOWN, isHood(below, state.getValue(DIRECTION)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        if(direction == Direction.DOWN)
        {
            return state.setValue(DOWN, isHood(neighbour, state.getValue(DIRECTION)));
        }
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if(!level.isClientSide())
        {
            level.setBlock(pos, state.cycle(POWERED), Block.UPDATE_ALL);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return (state.getValue(DOWN) ? VENT : FULL).get(state.getValue(DIRECTION));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(DOWN, POWERED);
    }
}
