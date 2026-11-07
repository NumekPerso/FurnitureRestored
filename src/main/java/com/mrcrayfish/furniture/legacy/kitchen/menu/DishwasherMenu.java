package com.mrcrayfish.furniture.legacy.kitchen.menu;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.DishwasherBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;

public class DishwasherMenu extends WashingMenu
{
    public DishwasherMenu(int windowId, Inventory playerInventory, DishwasherBlockEntity machine, ContainerData data)
    {
        super(KitchenContent.DISHWASHER_MENU.get(), windowId, playerInventory, machine, data);
        this.addSlot(new KitchenSlots.Tool(machine, 0, 56, 43, 0));
        this.addSlot(new KitchenSlots.Tool(machine, 1, 80, 43, 1));
        this.addSlot(new KitchenSlots.Tool(machine, 2, 104, 43, 2));
        this.addSlot(new KitchenSlots.Tool(machine, 3, 56, 74, 3));
        this.addSlot(new KitchenSlots.Tool(machine, 4, 80, 74, 4));
        this.addSlot(new KitchenSlots.Tool(machine, 5, 104, 74, 5));
        this.addSlot(new KitchenSlots.Soap(machine, 6, 125, 7));
        this.addPlayerSlots(playerInventory, 146, 204);
        this.addDataSlots(data);
    }

    public static int toolToSlot(ItemStack stack)
    {
        Item item = stack.getItem();
        if(item instanceof PickaxeItem)
            return 0;
        if(item instanceof ShovelItem)
            return 1;
        if(item instanceof SwordItem)
            return 2;
        if(item instanceof AxeItem)
            return 3;
        if(item instanceof HoeItem)
            return 4;
        return 5;
    }

    @Override
    protected boolean isMachineItem(ItemStack stack)
    {
        return stack.is(KitchenRecipes.DISHWASHER_REPAIRABLE) || WashingBlockEntity.isSoap(stack);
    }

    @Override
    protected boolean moveToMachine(ItemStack stack)
    {
        if(WashingBlockEntity.isSoap(stack))
        {
            return this.moveItemStackTo(stack, 6, 7, false);
        }
        int slot = toolToSlot(stack);
        return this.moveItemStackTo(stack, slot, slot + 1, false);
    }
}
