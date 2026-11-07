package com.mrcrayfish.furniture.legacy.kitchen.menu;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.OvenBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OvenMenu extends KitchenMenu
{
    private final ContainerData data;
    private final Inventory playerInventory;

    public OvenMenu(int windowId, Inventory playerInventory, OvenBlockEntity oven, ContainerData data)
    {
        super(KitchenContent.OVEN_MENU.get(), windowId, playerInventory, oven);
        this.data = data;
        this.playerInventory = playerInventory;
        for(int i = 0; i < 4; i++)
        {
            this.addSlot(new Slot(oven, i, i * 18 + 53, 31));
        }
        for(int i = 0; i < 4; i++)
        {
            this.addSlot(new KitchenSlots.Output(oven, i + 4, i * 18 + 53, 73));
        }
        this.addPlayerSlots(playerInventory, 146, 204);
        this.addDataSlots(data);
    }

    public boolean isCooking()
    {
        return this.data.get(0) > 0;
    }

    public int getCookingItem()
    {
        return this.data.get(1);
    }

    public int getProgressScaled(int size)
    {
        return this.data.get(0) * size / OvenBlockEntity.COOK_TIME;
    }

    @Override
    protected boolean isMachineItem(ItemStack stack)
    {
        return KitchenRecipes.find(KitchenRecipes.OVEN, this.playerInventory.player.level(), stack).isPresent();
    }

    @Override
    protected boolean moveToMachine(ItemStack stack)
    {
        return this.moveItemStackTo(stack, 0, 4, false);
    }
}
