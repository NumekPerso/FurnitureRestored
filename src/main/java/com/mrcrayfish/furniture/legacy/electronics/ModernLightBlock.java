package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.IPowered;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * A flat light for any face of a block. Only a light switch turns it on, it has no click of its own.
 */
public class ModernLightBlock extends FurnitureBlock implements IPowered
{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final Map<Direction, VoxelShape> SIDES = LegacyUtil.horizontalShapes(Direction.EAST, 13, 3, 3, 16, 13, 13);
    private static final VoxelShape SELECTION_FLOOR = box(3, 0, 3, 13, 3, 13);
    private static final VoxelShape SELECTION_CEILING = box(3, 13, 3, 13, 16, 13);
    private static final VoxelShape COLLISION_FLOOR = box(4, 0, 4, 14, 2, 14);
    private static final VoxelShape COLLISION_CEILING = box(4, 14, 4, 14, 16, 14);

    public ModernLightBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(FACING, Direction.UP));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch(state.getValue(FACING))
        {
            case UP -> SELECTION_FLOOR;
            case DOWN -> SELECTION_CEILING;
            default -> SIDES.get(state.getValue(FACING).getOpposite());
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch(state.getValue(FACING))
        {
            case UP -> COLLISION_FLOOR;
            case DOWN -> COLLISION_CEILING;
            default -> SIDES.get(state.getValue(FACING).getOpposite());
        };
    }

    @Override
    public void setPowered(Level level, BlockPos pos, BlockState state, boolean powered)
    {
        level.setBlock(pos, state.setValue(LIT, powered), Block.UPDATE_ALL);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(LIT, FACING);
    }
}
