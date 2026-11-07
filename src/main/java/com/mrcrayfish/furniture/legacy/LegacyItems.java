package com.mrcrayfish.furniture.legacy;

import com.mrcrayfish.furniture.legacy.LegacyRegistry.Category;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;

/**
 * Items without any behaviour of their own. Items with a behaviour (cup, drink, envelope, package,
 * chopped log) are registered by their module.
 */
public class LegacyItems
{
    public static final RegistryObject<Item> FLESH = food("flesh", new FoodProperties.Builder().nutrition(1).saturationMod(2.0F).build());
    public static final RegistryObject<Item> COOKED_FLESH = food("cooked_flesh", new FoodProperties.Builder().nutrition(4).saturationMod(4.0F).build());
    public static final RegistryObject<Item> BREAD_SLICE = food("bread_slice", new FoodProperties.Builder().nutrition(2).saturationMod(0.6F).build());
    public static final RegistryObject<Item> TOAST = food("toast", new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build());
    public static final RegistryObject<Item> SAUSAGE = food("sausage", new FoodProperties.Builder().nutrition(1).saturationMod(0.6F).build());
    public static final RegistryObject<Item> COOKED_SAUSAGE = food("cooked_sausage", new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build());
    public static final RegistryObject<Item> KEBAB = food("kebab", new FoodProperties.Builder().nutrition(1).saturationMod(0.6F).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F).build());
    public static final RegistryObject<Item> COOKED_KEBAB = food("cooked_kebab", new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build());

    public static final RegistryObject<Item> KNIFE = LegacyRegistry.item("knife", () -> new Item(new Item.Properties().durability(100)), Category.ITEMS, Category.KITCHEN);
    public static final RegistryObject<Item> SOAP = LegacyRegistry.item("soap", () -> new Item(new Item.Properties()), Category.ITEMS, Category.KITCHEN);
    public static final RegistryObject<Item> SOAPY_WATER = LegacyRegistry.item("soapy_water", () -> new Item(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)), Category.ITEMS, Category.KITCHEN);
    public static final RegistryObject<Item> SUPER_SOAPY_WATER = LegacyRegistry.item("super_soapy_water", () -> new Item(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)), Category.ITEMS, Category.KITCHEN);
    public static final RegistryObject<Item> COOL_PACK = LegacyRegistry.item("cool_pack", () -> new Item(new Item.Properties()), Category.ITEMS, Category.KITCHEN);
    public static final RegistryObject<Item> INK_CARTRIDGE = LegacyRegistry.item("ink_cartridge", () -> new Item(new Item.Properties()), Category.ITEMS, Category.ELECTRONICS);
    public static final RegistryObject<Item> CROWBAR = LegacyRegistry.item("crowbar", () -> new Item(new Item.Properties().stacksTo(1)), Category.ITEMS);
    public static final RegistryObject<Item> HAMMER = LegacyRegistry.item("hammer", () -> new Item(new Item.Properties().stacksTo(1)), Category.ITEMS);
    public static final RegistryObject<Item> TV_REMOTE = LegacyRegistry.item("tv_remote", () -> new Item(new Item.Properties().stacksTo(1)), Category.ITEMS, Category.ELECTRONICS);
    public static final RegistryObject<Item> CEILING_FAN_FANS = LegacyRegistry.item("ceiling_fan_fans", () -> new Item(new Item.Properties()));

    private static RegistryObject<Item> food(String name, FoodProperties food)
    {
        return LegacyRegistry.item(name, () -> new Item(new Item.Properties().food(food)), Category.ITEMS, Category.KITCHEN);
    }

    public static void init() {}
}
