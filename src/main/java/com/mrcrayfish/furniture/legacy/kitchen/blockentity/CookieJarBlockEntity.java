package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Holds no data: the number of cookies is in the blockstate. It exists for the renderer.
 */
public class CookieJarBlockEntity extends BlockEntity
{
    public CookieJarBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.COOKIE_JAR_ENTITY.get(), pos, state);
    }
}
