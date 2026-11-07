package com.mrcrayfish.furniture.legacy.shared;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Predicate;

/**
 * A chest menu whose container slots only accept some items. It stays a ChestMenu so the vanilla
 * ContainerScreen and the openers counter of BasicLootBlockEntity keep working.
 */
public class FilteredChestMenu extends ChestMenu
{
    private static final ResourceLocation DRINK = new ResourceLocation("furniture_restored", "drink");

    public FilteredChestMenu(MenuType<?> type, int windowId, Inventory playerInventory, Container container, int rows, Predicate<ItemStack> filter)
    {
        super(type, windowId, playerInventory, container, rows);
        for(int i = 0; i < rows * 9; i++)
        {
            Slot old = this.slots.get(i);
            Slot slot = new FilteredSlot(container, i, old.x, old.y, filter);
            slot.index = i;
            this.slots.set(i, slot);
        }
    }

    public static boolean isFridgeItem(ItemStack stack)
    {
        return stack.getItem().isEdible();
    }

    public static boolean isCoolerItem(ItemStack stack)
    {
        return stack.getItem() instanceof PotionItem || stack.is(Items.MILK_BUCKET) || stack.is(Items.WATER_BUCKET) || DRINK.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()));
    }

    public static FilteredChestMenu fridge(int windowId, Inventory playerInventory, Container container)
    {
        return new FilteredChestMenu(SharedContent.FRIDGE_MENU.get(), windowId, playerInventory, container, 3, FilteredChestMenu::isFridgeItem);
    }

    public static FilteredChestMenu cooler(int windowId, Inventory playerInventory, Container container)
    {
        return new FilteredChestMenu(SharedContent.COOLER_MENU.get(), windowId, playerInventory, container, 1, FilteredChestMenu::isCoolerItem);
    }

    public static FilteredChestMenu clientFridge(int windowId, Inventory playerInventory)
    {
        return fridge(windowId, playerInventory, new SimpleContainer(27));
    }

    public static FilteredChestMenu clientCooler(int windowId, Inventory playerInventory)
    {
        return cooler(windowId, playerInventory, new SimpleContainer(9));
    }

    private static class FilteredSlot extends Slot
    {
        private final Predicate<ItemStack> filter;

        private FilteredSlot(Container container, int index, int x, int y, Predicate<ItemStack> filter)
        {
            super(container, index, x, y);
            this.filter = filter;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return this.filter.test(stack);
        }
    }
}
