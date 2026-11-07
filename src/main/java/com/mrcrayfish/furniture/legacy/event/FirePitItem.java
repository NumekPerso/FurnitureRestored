package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * The item of a lit fire pit: places the pit already burning.
 */
public class FirePitItem extends BlockItem
{
    public FirePitItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context)
    {
        BlockState state = super.getPlacementState(context);
        return state == null ? null : state.setValue(FirePitBlock.STAGE, FirePitBlock.LAST_STAGE).setValue(FirePitBlock.LIT, true);
    }
}
