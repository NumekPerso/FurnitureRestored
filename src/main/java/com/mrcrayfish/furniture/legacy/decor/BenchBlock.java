package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.entity.SeatEntity;
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
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Outdoor bench. Its look only depends on the axis, so benches of both directions of an axis connect.
 */
public class BenchBlock extends FurnitureHorizontalBlock
{
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");

    private static final VoxelShape OUTLINE_Z = Block.box(1, 0, 0, 15, 10, 16);
    private static final VoxelShape OUTLINE_X = Block.box(0, 0, 1, 16, 10, 15);
    private static final VoxelShape COLLISION_Z = Block.box(2, 0, 1, 14, 9, 15);
    private static final VoxelShape COLLISION_X = Block.box(1, 0, 2, 15, 9, 14);

    public BenchBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(LEFT, false).setValue(RIGHT, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(DIRECTION).getAxis() == Direction.Axis.X ? OUTLINE_X : OUTLINE_Z;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(DIRECTION).getAxis() == Direction.Axis.X ? COLLISION_X : COLLISION_Z;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return SeatEntity.create(level, pos, 0.4, player, state.getValue(DIRECTION));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.withConnections(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
    {
        return this.withConnections(state, level, pos);
    }

    private BlockState withConnections(BlockState state, BlockGetter level, BlockPos pos)
    {
        Direction.Axis axis = state.getValue(DIRECTION).getAxis();
        BlockPos right = axis == Direction.Axis.Z ? pos.east() : pos.north();
        BlockPos left = axis == Direction.Axis.Z ? pos.west() : pos.south();
        return state.setValue(RIGHT, this.isBench(level.getBlockState(right), axis)).setValue(LEFT, this.isBench(level.getBlockState(left), axis));
    }

    private boolean isBench(BlockState other, Direction.Axis axis)
    {
        return other.is(this) && other.getValue(DIRECTION).getAxis() == axis;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(LEFT, RIGHT);
    }
}
