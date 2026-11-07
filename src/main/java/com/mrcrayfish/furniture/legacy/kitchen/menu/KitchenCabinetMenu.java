package com.mrcrayfish.furniture.legacy.kitchen.menu;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KitchenCabinetMenu extends KitchenMenu
{
    public KitchenCabinetMenu(int windowId, Inventory playerInventory, Container cabinet)
    {
        super(KitchenContent.KITCHEN_CABINET_MENU.get(), windowId, playerInventory, cabinet);
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                this.addSlot(new Slot(cabinet, j + i * 5, j * 18 + 44, i * 18 + 18));
            }
        }
        this.addPlayerSlots(playerInventory, 85, 143);
    }

    @Override
    protected boolean isMachineItem(ItemStack stack)
    {
        return true;
    }

    @Override
    protected boolean moveToMachine(ItemStack stack)
    {
        return this.moveItemStackTo(stack, 0, 15, false);
    }
}
