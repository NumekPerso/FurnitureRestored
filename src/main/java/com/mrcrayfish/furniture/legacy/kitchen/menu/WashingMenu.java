package com.mrcrayfish.furniture.legacy.kitchen.menu;

import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

/**
 * Common part of the washing machine and dishwasher menus.
 */
public abstract class WashingMenu extends KitchenMenu implements MachineControlMenu
{
    private final WashingBlockEntity machine;
    private final ContainerData data;

    protected WashingMenu(@Nullable MenuType<?> type, int windowId, Inventory playerInventory, WashingBlockEntity machine, ContainerData data)
    {
        super(type, windowId, playerInventory, machine);
        this.machine = machine;
        this.data = data;
    }

    public boolean isWashing()
    {
        return this.data.get(WashingBlockEntity.DATA_WASHING) != 0;
    }

    public boolean isSuperMode()
    {
        return this.data.get(WashingBlockEntity.DATA_SUPER_MODE) != 0;
    }

    public int getProgress()
    {
        return this.data.get(WashingBlockEntity.DATA_PROGRESS);
    }

    public int getTimeRemaining()
    {
        return this.data.get(WashingBlockEntity.DATA_TIME_REMAINING);
    }

    @Override
    public void startMachine()
    {
        this.machine.startWashing();
    }

    @Override
    public void stopMachine()
    {
        this.machine.stopWashing();
    }
}
