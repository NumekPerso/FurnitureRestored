package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PrinterMenu extends AbstractContainerMenu
{
    private final Container container;
    private final ContainerData data;

    public PrinterMenu(int windowId, Inventory playerInventory)
    {
        this(windowId, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public PrinterMenu(int windowId, Inventory playerInventory, Container container, ContainerData data)
    {
        super(ElectronicsContent.PRINTER_MENU.get(), windowId);
        checkContainerSize(container, 3);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, PrinterBlockEntity.SLOT_INPUT, 80, 5)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return PrinterBlockEntity.isCopyable(stack);
            }

            @Override
            public int getMaxStackSize()
            {
                return 1;
            }
        });
        this.addSlot(new Slot(container, PrinterBlockEntity.SLOT_INK, 55, 30)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return PrinterBlockEntity.getInkTime(stack) > 0;
            }
        });
        this.addSlot(new Slot(container, PrinterBlockEntity.SLOT_OUTPUT, 80, 61)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack)
            {
                LegacyTriggers.PRINTER_COPY.trigger(player);
                super.onTake(player, stack);
            }
        });

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, j * 18 + 8, i * 18 + 103));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, i * 18 + 8, 161));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot != null && slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if(index == PrinterBlockEntity.SLOT_OUTPUT)
            {
                if(!this.moveItemStackTo(stack, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, copy);
            }
            else if(index > PrinterBlockEntity.SLOT_OUTPUT)
            {
                if(PrinterBlockEntity.isCopyable(stack))
                {
                    if(!this.moveItemStackTo(stack, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(PrinterBlockEntity.getInkTime(stack) > 0)
                {
                    if(!this.moveItemStackTo(stack, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(index < 30)
                {
                    if(!this.moveItemStackTo(stack, 30, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(!this.moveItemStackTo(stack, 3, 30, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(stack, 3, 39, false))
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

            if(stack.getCount() == copy.getCount())
            {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return copy;
    }

    public boolean isPrinting()
    {
        return this.getInkTime() > 0;
    }

    public int getInkTime()
    {
        return this.data.get(0);
    }

    public int getInkTotal()
    {
        return this.data.get(1);
    }

    public int getPrintTime()
    {
        return this.data.get(2);
    }

    public int getPrintTotal()
    {
        return this.data.get(3);
    }
}
