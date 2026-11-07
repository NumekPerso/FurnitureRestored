package com.mrcrayfish.furniture.legacy.kitchen.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Common part of the kitchen menus: the machine slots come first, then the player inventory.
 */
public abstract class KitchenMenu extends AbstractContainerMenu
{
    protected final Container container;
    private final int containerSlots;

    protected KitchenMenu(@Nullable MenuType<?> type, int windowId, Inventory playerInventory, Container container)
    {
        super(type, windowId);
        checkContainerSize(container, container.getContainerSize());
        this.container = container;
        this.containerSlots = container.getContainerSize();
        if(!playerInventory.player.level().isClientSide())
        {
            container.startOpen(playerInventory.player);
        }
    }

    public Container getContainer()
    {
        return this.container;
    }

    /** Call after adding the slots of the machine. */
    protected void addPlayerSlots(Inventory playerInventory, int mainY, int hotbarY)
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, j * 18 + 8, i * 18 + mainY));
            }
        }
        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, i * 18 + 8, hotbarY));
        }
    }

    /** Should a shift click from the player inventory send this stack to the machine? */
    protected abstract boolean isMachineItem(ItemStack stack);

    /** Moves the stack to its slots in the machine. Returns false if nothing moved. */
    protected abstract boolean moveToMachine(ItemStack stack);

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = this.slots.get(index);
        if(slot == null || !slot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int hotbarStart = this.slots.size() - 9;
        if(index < this.containerSlots)
        {
            if(!this.moveItemStackTo(stack, this.containerSlots, this.slots.size(), true))
                return ItemStack.EMPTY;
        }
        else if(this.isMachineItem(stack))
        {
            if(!this.moveToMachine(stack))
                return ItemStack.EMPTY;
        }
        else if(index < hotbarStart)
        {
            if(!this.moveItemStackTo(stack, hotbarStart, this.slots.size(), false))
                return ItemStack.EMPTY;
        }
        else if(!this.moveItemStackTo(stack, this.containerSlots, hotbarStart, false))
        {
            return ItemStack.EMPTY;
        }

        if(stack.isEmpty())
        {
            slot.setByPlayer(ItemStack.EMPTY);
        }
        else
        {
            slot.setChanged();
        }
        if(stack.getCount() == copy.getCount())
            return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        if(!player.level().isClientSide())
        {
            this.container.stopOpen(player);
        }
    }
}
