package com.mrcrayfish.furniture.legacy;

import com.mrcrayfish.furniture.common.ModTags;
import com.mrcrayfish.furniture.core.ModBlocks;
import com.mrcrayfish.furniture.core.ModItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registration helper for the legacy modules. It records what the data generators and the creative
 * tab filters need, so a module only has to call one method.
 *
 * Blocks registered with {@link #blockNoItem} need Properties#noLootTable, or a {@link #dropAs} call.
 */
public class LegacyRegistry
{
    public enum Category
    {
        GENERAL(ModTags.Items.GENERAL),
        STORAGE(ModTags.Items.STORAGE),
        BEDROOM(ModTags.Items.BEDROOM),
        OUTDOORS(ModTags.Items.OUTDOORS),
        KITCHEN(ModTags.Items.KITCHEN),
        ITEMS(ModTags.Items.ITEMS),
        BATHROOM(ModTags.Items.BATHROOM),
        ELECTRONICS(ModTags.Items.ELECTRONICS),
        EVENT(ModTags.Items.EVENT),
        LIGHTING(ModTags.Items.LIGHTING);

        private final TagKey<Item> tag;

        Category(TagKey<Item> tag)
        {
            this.tag = tag;
        }

        public TagKey<Item> tag()
        {
            return this.tag;
        }
    }

    public static final List<RegistryObject<Block>> DROP_SELF = new ArrayList<>();
    public static final Map<RegistryObject<Block>, Supplier<? extends ItemLike>> DROP_AS = new LinkedHashMap<>();
    public static final Map<Category, List<Supplier<? extends Item>>> CATEGORIES = new EnumMap<>(Category.class);

    public static RegistryObject<Block> block(String name, Supplier<Block> block, Category... categories)
    {
        return block(name, block, b -> new BlockItem(b, new Item.Properties()), categories);
    }

    public static RegistryObject<Block> block(String name, Supplier<Block> block, Function<Block, Item> item, Category... categories)
    {
        RegistryObject<Block> object = ModBlocks.REGISTER.register(name, block);
        RegistryObject<Item> itemObject = ModItems.REGISTER.register(name, () -> item.apply(object.get()));
        DROP_SELF.add(object);
        addToCategories(itemObject, categories);
        return object;
    }

    public static RegistryObject<Block> blockNoItem(String name, Supplier<Block> block)
    {
        return ModBlocks.REGISTER.register(name, block);
    }

    public static void dropAs(RegistryObject<Block> block, Supplier<? extends ItemLike> drop)
    {
        DROP_AS.put(block, drop);
    }

    public static RegistryObject<Item> item(String name, Supplier<Item> item, Category... categories)
    {
        RegistryObject<Item> object = ModItems.REGISTER.register(name, item);
        addToCategories(object, categories);
        return object;
    }

    /**
     * Registers one block per dye color, named color_name (for example red_lamp).
     */
    public static Map<DyeColor, RegistryObject<Block>> dyed(String name, Function<DyeColor, Block> factory, Category... categories)
    {
        return dyed(name, factory, b -> new BlockItem(b, new Item.Properties()), categories);
    }

    public static Map<DyeColor, RegistryObject<Block>> dyed(String name, Function<DyeColor, Block> factory, Function<Block, Item> item, Category... categories)
    {
        Map<DyeColor, RegistryObject<Block>> map = new EnumMap<>(DyeColor.class);
        for(DyeColor color : DyeColor.values())
        {
            map.put(color, block(color.getName() + "_" + name, () -> factory.apply(color), item, categories));
        }
        return map;
    }

    public static Block[] blocks(Map<DyeColor, RegistryObject<Block>> map)
    {
        return map.values().stream().map(RegistryObject::get).toArray(Block[]::new);
    }

    private static void addToCategories(RegistryObject<Item> item, Category... categories)
    {
        for(Category category : categories)
        {
            CATEGORIES.computeIfAbsent(category, c -> new ArrayList<>()).add(item);
        }
    }
}
