package com.mrcrayfish.furniture.legacy.kitchen.menu;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.MicrowaveBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * The slots of the kitchen machines. The icons are in the block atlas, which reads textures/item.
 */
public class KitchenSlots
{
    private static ResourceLocation outline(String name)
    {
        return new ResourceLocation(Reference.MOD_ID, "item/legacy/outline_" + name);
    }

    public static class Output extends Slot
    {
        public Output(Container container, int index, int x, int y)
        {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack)
        {
            LegacyTriggers.OVEN_COOK.trigger(player);
            super.onTake(player, stack);
        }
    }

    /** Locked while the microwave runs. */
    public static class Microwave extends Slot
    {
        private final MicrowaveBlockEntity microwave;

        public Microwave(MicrowaveBlockEntity microwave, int index, int x, int y)
        {
            super(microwave, index, x, y);
            this.microwave = microwave;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return !this.microwave.isCooking();
        }

        @Override
        public boolean mayPickup(Player player)
        {
            return !this.microwave.isCooking();
        }
    }

    public static class Soap extends Slot
    {
        public Soap(Container container, int index, int x, int y)
        {
            super(container, index, x, y);
            this.setBackground(InventoryMenu.BLOCK_ATLAS, outline("bucket"));
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return WashingBlockEntity.isSoap(stack);
        }
    }

    public static class Armour extends Slot
    {
        private final EquipmentSlot equipmentSlot;

        public Armour(Container container, int index, int x, int y, EquipmentSlot equipmentSlot)
        {
            super(container, index, x, y);
            this.equipmentSlot = equipmentSlot;
            ResourceLocation icon = switch(equipmentSlot)
            {
                case HEAD -> InventoryMenu.EMPTY_ARMOR_SLOT_HELMET;
                case CHEST -> InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE;
                case LEGS -> InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS;
                default -> InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS;
            };
            this.setBackground(InventoryMenu.BLOCK_ATLAS, icon);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return stack.is(KitchenRecipes.WASHING_MACHINE_REPAIRABLE) && Mob.getEquipmentSlotForItem(stack) == this.equipmentSlot;
        }
    }

    /** Tool slots of the dishwasher: pickaxe, shovel, sword, axe, hoe, then anything else that can be repaired. */
    public static class Tool extends Slot
    {
        private final int toolType;

        public Tool(Container container, int index, int x, int y, int toolType)
        {
            super(container, index, x, y);
            this.toolType = toolType;
            ResourceLocation icon = switch(toolType)
            {
                case 0 -> outline("pickaxe");
                case 1 -> outline("shovel");
                case 2 -> outline("sword");
                case 3 -> outline("axe");
                case 4 -> outline("hoe");
                default -> InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
            };
            this.setBackground(InventoryMenu.BLOCK_ATLAS, icon);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            int target = DishwasherMenu.toolToSlot(stack);
            if(this.toolType == 5)
            {
                return target == 5 && stack.is(KitchenRecipes.DISHWASHER_REPAIRABLE);
            }
            return target == this.toolType;
        }
    }
}
