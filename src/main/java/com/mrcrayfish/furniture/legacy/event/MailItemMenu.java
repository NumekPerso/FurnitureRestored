package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Menu of an item held in the main hand. The content is written to the stack when the menu closes.
 * The stack itself cannot leave its slot while the menu is open, which keeps the player from
 * duplicating or losing the content.
 *
 * The order of the slots is: content of the item, then player inventory, then hotbar.
 */
public class MailItemMenu extends AbstractContainerMenu
{
    private static final int PLAYER_SLOTS = 36;

    private final MailLayout layout;
    private final Container container;
    private final boolean canInsert;
    private final int heldSlot;
    /* The stack in the hand of the player, null on the client */
    @Nullable
    private ItemStack stack;
    private boolean saved;

    /** Client side: the content comes from the server. A signed item can only be emptied. */
    public MailItemMenu(MailLayout layout, int windowId, Inventory playerInventory)
    {
        this(layout, windowId, playerInventory, new SimpleContainer(layout.size()), null, !(playerInventory.player.getMainHandItem().getItem() instanceof SignedMailItem));
    }

    private MailItemMenu(MailLayout layout, int windowId, Inventory playerInventory, Container container, @Nullable ItemStack stack, boolean canInsert)
    {
        super(layout.menuType(), windowId);
        this.layout = layout;
        this.container = container;
        this.stack = stack;
        this.canInsert = canInsert;
        this.heldSlot = playerInventory.selected;

        for(int i = 0; i < layout.size(); i++)
        {
            this.addSlot(new ContentSlot(container, i, layout.slotX(i), layout.slotY(i)));
        }
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for(int x = 0; x < 9; x++)
        {
            this.addSlot(x == this.heldSlot ? new LockedSlot(playerInventory, x, 8 + x * 18, 142) : new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    /**
     * Server side: opens the menu of a stack held in the main hand.
     *
     * @param canInsert false for a signed item, which can only be emptied
     */
    public static void open(ServerPlayer player, ItemStack stack, MailLayout layout, boolean canInsert)
    {
        player.openMenu(new SimpleMenuProvider((windowId, inventory, entity) -> {
            SimpleContainer container = new SimpleContainer(layout.size());
            NonNullList<ItemStack> items = MailStorage.read(stack, layout);
            for(int i = 0; i < items.size(); i++)
            {
                container.setItem(i, items.get(i));
            }
            // While the menu is open, the content lives only in the menu. If the stack left the
            // hand as a copy (dropped through a packet, which splits it), that copy must not keep
            // the content that removed() gives back to the player.
            MailStorage.clear(stack, layout);
            return new MailItemMenu(layout, windowId, inventory, container, stack, canInsert);
        }, layout.title()));
    }

    public MailLayout getLayout()
    {
        return this.layout;
    }

    /** True if the menu is open for this very stack. */
    public boolean isFor(ItemStack stack)
    {
        return this.stack == stack;
    }

    public boolean isContentEmpty()
    {
        return this.container.isEmpty();
    }

    /** Signing replaces the stack in the hand: the content has to follow. */
    public void replaceStack(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return this.stack == null || (player.isAlive() && player.getMainHandItem() == this.stack);
    }

    /* A number key on a slot would move the held stack through its hotbar index, not through the locked slot */
    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player)
    {
        if(clickType == ClickType.SWAP && button == this.heldSlot)
            return;
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = this.slots.get(index);
        if(!slot.hasItem())
            return ItemStack.EMPTY;
        ItemStack slotStack = slot.getItem();
        ItemStack copy = slotStack.copy();
        int size = this.layout.size();
        if(index < size)
        {
            if(!this.moveItemStackTo(slotStack, size, size + PLAYER_SLOTS, true))
                return ItemStack.EMPTY;
        }
        /* moveItemStackTo merges with existing stacks without asking the slot, so a signed item is refused here */
        else if(!this.canInsert || !slot.mayPickup(player) || !this.moveItemStackTo(slotStack, 0, size, false))
        {
            return ItemStack.EMPTY;
        }
        if(slotStack.isEmpty())
        {
            slot.set(ItemStack.EMPTY);
        }
        else
        {
            slot.setChanged();
        }
        return copy;
    }

    /**
     * The stack is written even if it left the hand (death): it is the same object as the dropped
     * item, so that the content is neither lost nor duplicated. Only a stack that no longer exists
     * gives its content back to the player.
     */
    @Override
    public void removed(Player player)
    {
        super.removed(player);
        if(this.stack != null && !this.saved)
        {
            this.saved = true;
            if(this.stack.isEmpty())
            {
                this.clearContainer(player, this.container);
            }
            else
            {
                MailStorage.write(this.stack, this.layout, this.container);
            }
        }
    }

    private class ContentSlot extends Slot
    {
        private ContentSlot(Container container, int index, int x, int y)
        {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return MailItemMenu.this.canInsert && !(stack.getItem() instanceof IItemInventory) && stack.getItem().canFitInsideContainerItems();
        }
    }

    private static class LockedSlot extends Slot
    {
        private LockedSlot(Container container, int index, int x, int y)
        {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPickup(Player player)
        {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return false;
        }
    }
}
