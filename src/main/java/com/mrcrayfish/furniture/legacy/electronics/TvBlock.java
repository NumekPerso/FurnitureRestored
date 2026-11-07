package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Decoration only.
 */
public class TvBlock extends FurnitureHorizontalBlock
{
    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 14, 15);

    public TvBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
