package com.mrcrayfish.furniture.legacy.kitchen.menu;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.MicrowaveBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public class MicrowaveMenu extends KitchenMenu implements MachineControlMenu
{
    private final MicrowaveBlockEntity microwave;
    private final ContainerData data;
    private final Inventory playerInventory;

    public MicrowaveMenu(int windowId, Inventory playerInventory, MicrowaveBlockEntity microwave, ContainerData data)
    {
        super(KitchenContent.MICROWAVE_MENU.get(), windowId, playerInventory, microwave);
        this.microwave = microwave;
        this.data = data;
        this.playerInventory = playerInventory;
        this.addSlot(new KitchenSlots.Microwave(microwave, 0, 65, 43));
        this.addPlayerSlots(playerInventory, 92, 150);
        this.addDataSlots(data);
    }

    public boolean isCooking()
    {
        return this.data.get(MicrowaveBlockEntity.DATA_COOKING) != 0;
    }

    public int getProgress()
    {
        return this.data.get(MicrowaveBlockEntity.DATA_PROGRESS);
    }

    @Override
    public void startMachine()
    {
        this.microwave.startCooking();
    }

    @Override
    public void stopMachine()
    {
        this.microwave.stopCooking();
    }

    @Override
    protected boolean isMachineItem(ItemStack stack)
    {
        return KitchenRecipes.find(KitchenRecipes.MICROWAVE, this.playerInventory.player.level(), stack).isPresent();
    }

    @Override
    protected boolean moveToMachine(ItemStack stack)
    {
        return this.moveItemStackTo(stack, 0, 1, false);
    }
}
