package com.mrcrayfish.furniture.legacy.kitchen.menu;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingMachineBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public class WashingMachineMenu extends WashingMenu
{
    public static final EquipmentSlot[] ARMOUR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public WashingMachineMenu(int windowId, Inventory playerInventory, WashingMachineBlockEntity machine, ContainerData data)
    {
        super(KitchenContent.WASHING_MACHINE_MENU.get(), windowId, playerInventory, machine, data);
        this.addSlot(new KitchenSlots.Armour(machine, 0, 80, 44, ARMOUR_SLOTS[0]));
        this.addSlot(new KitchenSlots.Armour(machine, 1, 64, 60, ARMOUR_SLOTS[1]));
        this.addSlot(new KitchenSlots.Armour(machine, 2, 96, 60, ARMOUR_SLOTS[2]));
        this.addSlot(new KitchenSlots.Armour(machine, 3, 80, 76, ARMOUR_SLOTS[3]));
        this.addSlot(new KitchenSlots.Soap(machine, 4, 125, 7));
        this.addPlayerSlots(playerInventory, 146, 204);
        this.addDataSlots(data);
    }

    @Override
    protected boolean isMachineItem(ItemStack stack)
    {
        return stack.is(KitchenRecipes.WASHING_MACHINE_REPAIRABLE) || WashingBlockEntity.isSoap(stack);
    }

    @Override
    protected boolean moveToMachine(ItemStack stack)
    {
        if(WashingBlockEntity.isSoap(stack))
        {
            return this.moveItemStackTo(stack, 4, 5, false);
        }
        return this.moveItemStackTo(stack, 0, 4, false);
    }
}
