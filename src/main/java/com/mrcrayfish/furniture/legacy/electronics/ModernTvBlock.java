package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * Decoration only. Placed on the side of a block, it uses the wall mounted model.
 */
public class ModernTvBlock extends FurnitureHorizontalBlock
{
    public static final BooleanProperty MOUNTED = BooleanProperty.create("mounted");

    private static final Map<Direction, VoxelShape> SELECTION = LegacyUtil.horizontalShapes(Direction.EAST, 6, 0, -5, 11, 18, 21);
    private static final Map<Direction, VoxelShape> SELECTION_MOUNTED = LegacyUtil.horizontalShapes(Direction.EAST, 12, 2, -5, 16, 18, 21);
    private static final Map<Direction, VoxelShape> COLLISION = LegacyUtil.horizontalShapes(Direction.EAST, 7, 0, -4, 10, 17, 20);
    private static final Map<Direction, VoxelShape> COLLISION_MOUNTED = LegacyUtil.horizontalShapes(Direction.EAST, 13, 3, -4, 15, 17, 20);

    public ModernTvBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(MOUNTED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return super.getStateForPlacement(context).setValue(MOUNTED, context.getClickedFace().getAxis().isHorizontal());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return (state.getValue(MOUNTED) ? SELECTION_MOUNTED : SELECTION).get(state.getValue(DIRECTION));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return (state.getValue(MOUNTED) ? COLLISION_MOUNTED : COLLISION).get(state.getValue(DIRECTION));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(MOUNTED);
    }
}
