package com.mrcrayfish.furniture.legacy.bathroom;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

public class WallCabinetMenu extends AbstractContainerMenu
{
    private static final int SLOTS = 9;

    private final Container container;

    /** Client side: the slots are synced by the menu. */
    public WallCabinetMenu(int windowId, Inventory playerInventory)
    {
        this(windowId, playerInventory, new SimpleContainer(SLOTS));
    }

    public WallCabinetMenu(int windowId, Inventory playerInventory, Container container)
    {
        super(BathroomContent.WALL_CABINET_MENU.get(), windowId);
        checkContainerSize(container, SLOTS);
        this.container = container;
        container.startOpen(playerInventory.player);

        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 3; x++)
            {
                this.addSlot(new Slot(container, x + y * 3, x * 18 + 62, y * 27 + 17)
                {
                    @Override
                    public boolean mayPlace(ItemStack stack)
                    {
                        return stack.getItem() instanceof PotionItem;
                    }
                });
            }
        }

        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, x * 18 + 8, y * 18 + 115));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInventory, x, x * 18 + 8, 173));
        }
    }

    public Container getContainer()
    {
        return this.container;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = this.slots.get(index);
        if(!slot.hasItem() || !(slot.getItem().getItem() instanceof PotionItem))
            return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if(index < SLOTS)
        {
            if(!this.moveItemStackTo(stack, SLOTS, this.slots.size(), true))
                return ItemStack.EMPTY;
        }
        else if(!this.moveItemStackTo(stack, 0, SLOTS, false))
        {
            return ItemStack.EMPTY;
        }

        if(stack.isEmpty())
        {
            slot.set(ItemStack.EMPTY);
        }
        else
        {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
