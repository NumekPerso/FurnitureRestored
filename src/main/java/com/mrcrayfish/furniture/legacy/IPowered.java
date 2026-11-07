package com.mrcrayfish.furniture.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block a light switch can control. Blocks of any module can implement it.
 */
public interface IPowered
{
    void setPowered(Level level, BlockPos pos, BlockState state, boolean powered);
}
