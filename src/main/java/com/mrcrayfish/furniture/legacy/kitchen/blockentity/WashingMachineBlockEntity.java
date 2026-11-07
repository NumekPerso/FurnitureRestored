package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.menu.WashingMachineMenu;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class WashingMachineBlockEntity extends WashingBlockEntity
{
    public WashingMachineBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.WASHING_MACHINE_ENTITY.get(), pos, state, 20);
    }

    @Override
    public int getContainerSize()
    {
        return 5;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.washing_machine");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new WashingMachineMenu(windowId, playerInventory, this, this.data);
    }

    @Override
    protected boolean isRepairable(ItemStack stack)
    {
        return stack.is(KitchenRecipes.WASHING_MACHINE_REPAIRABLE);
    }

    @Override
    protected SoundEvent getRunningSound()
    {
        return LegacySounds.WASHING_MACHINE.get();
    }

    /** At least one armour piece that can be repaired. */
    @Override
    protected boolean hasValidContent()
    {
        for(int i = 0; i < 4; i++)
        {
            if(this.isRepairable(this.getItem(i)))
            {
                return true;
            }
        }
        return false;
    }

    /** Slots follow the order of the menu: head, chest, legs and feet, then the soapy water. */
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        if(slot == 4)
        {
            return isSoap(stack);
        }
        return this.isRepairable(stack) && WashingMachineMenu.ARMOUR_SLOTS[slot] == Mob.getEquipmentSlotForItem(stack);
    }
}
