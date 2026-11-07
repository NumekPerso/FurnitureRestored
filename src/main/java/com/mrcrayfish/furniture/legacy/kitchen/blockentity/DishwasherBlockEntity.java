package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.menu.DishwasherMenu;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DishwasherBlockEntity extends WashingBlockEntity
{
    public DishwasherBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.DISHWASHER_ENTITY.get(), pos, state, 19);
    }

    @Override
    public int getContainerSize()
    {
        return 7;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.dishwasher");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new DishwasherMenu(windowId, playerInventory, this, this.data);
    }

    @Override
    protected boolean isRepairable(ItemStack stack)
    {
        return stack.is(KitchenRecipes.DISHWASHER_REPAIRABLE);
    }

    @Override
    protected SoundEvent getRunningSound()
    {
        return LegacySounds.DISHWASHER.get();
    }

    /** Unlike the washing machine, it runs empty but refuses any item that cannot be repaired. */
    @Override
    protected boolean hasValidContent()
    {
        for(int i = 0; i < 6; i++)
        {
            ItemStack stack = this.getItem(i);
            if(!stack.isEmpty() && !this.isRepairable(stack))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        if(slot == 6)
        {
            return isSoap(stack);
        }
        return DishwasherMenu.toolToSlot(stack) == slot;
    }
}
