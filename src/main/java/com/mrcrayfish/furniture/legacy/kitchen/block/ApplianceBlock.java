package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * An appliance gives the advancement trigger when placed.
 */
public abstract class ApplianceBlock extends HorizontalEntityBlock
{
    protected ApplianceBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_APPLIANCE.trigger(player);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
