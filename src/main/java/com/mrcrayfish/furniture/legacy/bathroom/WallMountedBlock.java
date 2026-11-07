package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * A block hanging on the wall it was placed against: the direction property points to that wall,
 * and the block breaks and drops when the wall goes away.
 */
public abstract class WallMountedBlock extends FurnitureHorizontalBlock
{
    public WallMountedBlock(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        if(face.getAxis().isVertical())
            return null;
        BlockState state = this.defaultBlockState().setValue(DIRECTION, face.getOpposite());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction direction = state.getValue(DIRECTION);
        BlockPos wall = pos.relative(direction);
        return level.getBlockState(wall).isFaceSturdy(level, wall, direction.getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if(direction == state.getValue(DIRECTION) && !state.canSurvive(level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(placer != null)
        {
            BathroomUtil.triggerPlaced(placer);
        }
    }
}
