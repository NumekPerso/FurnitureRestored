package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.core.ModBlockEntities;
import com.mrcrayfish.furniture.core.ModContainers;
import com.mrcrayfish.furniture.legacy.LegacyRegistry;
import com.mrcrayfish.furniture.legacy.LegacyRegistry.Category;
import com.mrcrayfish.furniture.network.PacketHandler;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.RegistryObject;

/**
 * Module bathroom
 */
public class BathroomContent
{
    public static final RegistryObject<Block> TOILET = LegacyRegistry.block("toilet", () -> new ToiletBlock(stone()), Category.BATHROOM);
    public static final RegistryObject<Block> BASIN = LegacyRegistry.block("basin", () -> new BasinBlock(stone()), Category.BATHROOM);
    public static final RegistryObject<Block> WALL_CABINET = LegacyRegistry.block("wall_cabinet", () -> new WallCabinetBlock(stone()), Category.BATHROOM, Category.STORAGE);
    public static final RegistryObject<Block> BATH = LegacyRegistry.block("bath", () -> new BathBlock(stone().pushReaction(PushReaction.BLOCK)), block -> new BathItem(block, new Item.Properties()), Category.BATHROOM);
    public static final RegistryObject<Block> SHOWER = LegacyRegistry.block("shower", () -> new ShowerBlock(stone().pushReaction(PushReaction.BLOCK)), Category.BATHROOM);
    public static final RegistryObject<Block> SHOWER_HEAD = LegacyRegistry.block("shower_head", () -> new ShowerHeadBlock(stone()), Category.BATHROOM);
    // METAL rather than ANVIL: the mining tags follow the sound and do not know ANVIL
    public static final RegistryObject<Block> BIN = LegacyRegistry.block("bin", () -> new BinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5F).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops()), Category.BATHROOM, Category.KITCHEN);
    public static final RegistryObject<Block> MIRROR = LegacyRegistry.block("mirror", () -> new MirrorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(0.0F).sound(SoundType.STONE).noOcclusion()), Category.BATHROOM, Category.BEDROOM);

    public static final RegistryObject<BlockEntityType<ShowerHeadBlockEntity>> SHOWER_HEAD_BLOCK_ENTITY = ModBlockEntities.register("shower_head", ShowerHeadBlockEntity::new, () -> new Block[]{SHOWER_HEAD.get()});
    public static final RegistryObject<BlockEntityType<WallCabinetBlockEntity>> WALL_CABINET_BLOCK_ENTITY = ModBlockEntities.register("wall_cabinet", WallCabinetBlockEntity::new, () -> new Block[]{WALL_CABINET.get()});
    public static final RegistryObject<BlockEntityType<BinBlockEntity>> BIN_BLOCK_ENTITY = ModBlockEntities.register("bin", BinBlockEntity::new, () -> new Block[]{BIN.get()});

    public static final RegistryObject<MenuType<WallCabinetMenu>> WALL_CABINET_MENU = ModContainers.register("wall_cabinet", (IContainerFactory<WallCabinetMenu>) (windowId, playerInventory, data) -> new WallCabinetMenu(windowId, playerInventory));
    public static final RegistryObject<MenuType<BinMenu>> BIN_MENU = ModContainers.register("bin", (IContainerFactory<BinMenu>) (windowId, playerInventory, data) -> new BinMenu(windowId, playerInventory));

    /** Hardness 1, and it only drops when mined with a pickaxe. */
    private static BlockBehaviour.Properties stone()
    {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops();
    }

    /** Forces the static registrations of this module to run. */
    public static void init() {}

    public static void commonSetup() {}

    public static void registerPackets()
    {
        PacketHandler.register(C2SMessageFart.class, new C2SMessageFart(), NetworkDirection.PLAY_TO_SERVER);
        PacketHandler.register(C2SMessageEmptyBin.class, new C2SMessageEmptyBin(), NetworkDirection.PLAY_TO_SERVER);
    }
}
