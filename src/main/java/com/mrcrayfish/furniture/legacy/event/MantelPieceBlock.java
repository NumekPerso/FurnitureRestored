package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class MantelPieceBlock extends FurnitureHorizontalBlock
{
    /* The mantel piece is wider and taller than its block */
    private static final Map<Direction, VoxelShape> SHAPES = LegacyUtil.horizontalShapes(Direction.NORTH, -8, 0, 0, 24, 22.4, 6);

    public MantelPieceBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DIRECTION));
    }
}
