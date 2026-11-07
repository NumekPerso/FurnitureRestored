package com.mrcrayfish.furniture.legacy.shared;

import com.mrcrayfish.furniture.block.BedsideCabinetBlock;
import com.mrcrayfish.furniture.block.CabinetBlock;
import com.mrcrayfish.furniture.block.ChairBlock;
import com.mrcrayfish.furniture.block.CoffeeTableBlock;
import com.mrcrayfish.furniture.block.DeskBlock;
import com.mrcrayfish.furniture.block.DeskCabinetBlock;
import com.mrcrayfish.furniture.block.DoorMatBlock;
import com.mrcrayfish.furniture.block.TableBlock;
import com.mrcrayfish.furniture.block.UpgradedFenceBlock;
import com.mrcrayfish.furniture.core.ModBlockEntities;
import com.mrcrayfish.furniture.core.ModBlocks;
import com.mrcrayfish.furniture.core.ModContainers;
import com.mrcrayfish.furniture.item.DoorMatItem;
import com.mrcrayfish.furniture.legacy.LegacyRegistry;
import com.mrcrayfish.furniture.legacy.LegacyRegistry.Category;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Module shared
 * Stone furniture, nether brick fence and colored door mats, plus the behaviours added to
 * existing blocks.
 */
public class SharedContent
{
    public static final List<RegistryObject<Block>> STONE_CABINETS = new ArrayList<>();
    public static final List<RegistryObject<Block>> STONE_BEDSIDE_CABINETS = new ArrayList<>();
    public static final List<RegistryObject<Block>> STONE_DESK_CABINETS = new ArrayList<>();

    public static final RegistryObject<Block> STONE_TABLE = stone("stone_table", TableBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> GRANITE_TABLE = stone("granite_table", TableBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> DIORITE_TABLE = stone("diorite_table", TableBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> ANDESITE_TABLE = stone("andesite_table", TableBlock::new, Category.GENERAL);

    public static final RegistryObject<Block> STONE_COFFEE_TABLE = stone("stone_coffee_table", CoffeeTableBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> GRANITE_COFFEE_TABLE = stone("granite_coffee_table", CoffeeTableBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> DIORITE_COFFEE_TABLE = stone("diorite_coffee_table", CoffeeTableBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> ANDESITE_COFFEE_TABLE = stone("andesite_coffee_table", CoffeeTableBlock::new, Category.GENERAL);

    public static final RegistryObject<Block> STONE_CHAIR = stone("stone_chair", ChairBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> GRANITE_CHAIR = stone("granite_chair", ChairBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> DIORITE_CHAIR = stone("diorite_chair", ChairBlock::new, Category.GENERAL);
    public static final RegistryObject<Block> ANDESITE_CHAIR = stone("andesite_chair", ChairBlock::new, Category.GENERAL);

    public static final RegistryObject<Block> STONE_CABINET = cabinet("stone_cabinet");
    public static final RegistryObject<Block> GRANITE_CABINET = cabinet("granite_cabinet");
    public static final RegistryObject<Block> DIORITE_CABINET = cabinet("diorite_cabinet");
    public static final RegistryObject<Block> ANDESITE_CABINET = cabinet("andesite_cabinet");

    public static final RegistryObject<Block> STONE_BEDSIDE_CABINET = bedsideCabinet("stone_bedside_cabinet");
    public static final RegistryObject<Block> GRANITE_BEDSIDE_CABINET = bedsideCabinet("granite_bedside_cabinet");
    public static final RegistryObject<Block> DIORITE_BEDSIDE_CABINET = bedsideCabinet("diorite_bedside_cabinet");
    public static final RegistryObject<Block> ANDESITE_BEDSIDE_CABINET = bedsideCabinet("andesite_bedside_cabinet");

    public static final RegistryObject<Block> STONE_DESK = desk("stone_desk", DeskBlock.MaterialType.STONE);
    public static final RegistryObject<Block> GRANITE_DESK = desk("granite_desk", DeskBlock.MaterialType.GRANITE);
    public static final RegistryObject<Block> DIORITE_DESK = desk("diorite_desk", DeskBlock.MaterialType.DIORITE);
    public static final RegistryObject<Block> ANDESITE_DESK = desk("andesite_desk", DeskBlock.MaterialType.ANDESITE);

    public static final RegistryObject<Block> STONE_DESK_CABINET = deskCabinet("stone_desk_cabinet", DeskBlock.MaterialType.STONE);
    public static final RegistryObject<Block> GRANITE_DESK_CABINET = deskCabinet("granite_desk_cabinet", DeskBlock.MaterialType.GRANITE);
    public static final RegistryObject<Block> DIORITE_DESK_CABINET = deskCabinet("diorite_desk_cabinet", DeskBlock.MaterialType.DIORITE);
    public static final RegistryObject<Block> ANDESITE_DESK_CABINET = deskCabinet("andesite_desk_cabinet", DeskBlock.MaterialType.ANDESITE);

    /* Mined with a pickaxe through BlockTagGen, its sound type is not one of those detected there */
    public static final RegistryObject<Block> NETHER_BRICK_UPGRADED_FENCE = LegacyRegistry.block("nether_brick_upgraded_fence", () -> new UpgradedFenceBlock(Block.Properties.copy(Blocks.NETHER_BRICK_FENCE).noOcclusion()), Category.OUTDOORS);

    public static final Map<DyeColor, RegistryObject<Block>> DOOR_MATS = LegacyRegistry.dyed("door_mat", color -> new DoorMatBlock(Block.Properties.copy(Blocks.HAY_BLOCK).mapColor(color).noOcclusion()), block -> new DoorMatItem(block, new Item.Properties()), Category.OUTDOORS);

    /* Includes the coffee tables of every module through their common class */
    public static final RegistryObject<BlockEntityType<CoffeeTableBlockEntity>> COFFEE_TABLE = ModBlockEntities.register("coffee_table", CoffeeTableBlockEntity::new, () -> ModBlocks.REGISTER.getEntries().stream().map(RegistryObject::get).filter(block -> block instanceof CoffeeTableBlock || block instanceof com.mrcrayfish.furniture.legacy.decor.ModernCoffeeTableBlock).toArray(Block[]::new));

    public static final RegistryObject<MenuType<FilteredChestMenu>> FRIDGE_MENU = ModContainers.register("fridge", FilteredChestMenu::clientFridge);
    public static final RegistryObject<MenuType<FilteredChestMenu>> COOLER_MENU = ModContainers.register("cooler", FilteredChestMenu::clientCooler);

    private static RegistryObject<Block> stone(String name, Function<Block.Properties, Block> factory, Category... categories)
    {
        return LegacyRegistry.block(name, () -> factory.apply(Block.Properties.copy(Blocks.STONE).noOcclusion()), categories);
    }

    private static RegistryObject<Block> cabinet(String name)
    {
        RegistryObject<Block> block = stone(name, CabinetBlock::new, Category.STORAGE);
        STONE_CABINETS.add(block);
        return block;
    }

    private static RegistryObject<Block> bedsideCabinet(String name)
    {
        RegistryObject<Block> block = stone(name, BedsideCabinetBlock::new, Category.BEDROOM, Category.STORAGE);
        STONE_BEDSIDE_CABINETS.add(block);
        return block;
    }

    private static RegistryObject<Block> desk(String name, DeskBlock.MaterialType material)
    {
        return stone(name, properties -> new DeskBlock(properties, material), Category.BEDROOM);
    }

    private static RegistryObject<Block> deskCabinet(String name, DeskBlock.MaterialType material)
    {
        RegistryObject<Block> block = stone(name, properties -> new DeskCabinetBlock(properties, material), Category.BEDROOM, Category.STORAGE);
        STONE_DESK_CABINETS.add(block);
        return block;
    }

    /** Adds this module's blocks to the valid blocks of a block entity type of the base. */
    public static Block[] with(Block[] blocks, Collection<RegistryObject<Block>> extra)
    {
        return Stream.concat(Stream.of(blocks), extra.stream().map(RegistryObject::get)).toArray(Block[]::new);
    }

    /** Forces the static registrations of this module to run. */
    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(new SharedEvents());
    }

    public static void commonSetup() {}

    public static void registerPackets() {}
}
