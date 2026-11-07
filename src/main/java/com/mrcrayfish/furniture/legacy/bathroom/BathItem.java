package com.mrcrayfish.furniture.legacy.bathroom;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Places a bath only if there is room for its two parts.
 */
public class BathItem extends BlockItem
{
    public BathItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state)
    {
        return super.canPlace(context, state) && state.getBlock() instanceof TwoPartBlock block && block.canPlaceOtherPart(context, state);
    }
}
