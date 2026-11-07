package com.mrcrayfish.furniture.legacy.event;

import com.mrcrayfish.furniture.core.ModBlockEntities;
import com.mrcrayfish.furniture.core.ModContainers;
import com.mrcrayfish.furniture.core.ModRecipeSerializers;
import com.mrcrayfish.furniture.legacy.LegacyRegistry;
import com.mrcrayfish.furniture.legacy.LegacyRegistry.Category;
import com.mrcrayfish.furniture.network.PacketHandler;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

/**
 * Module event
 */
public class EventContent
{
    public static final Map<DyeColor, RegistryObject<Block>> PRESENTS = LegacyRegistry.dyed("present", color -> new PresentBlock(BlockBehaviour.Properties.of().mapColor(color).strength(0.5F).sound(SoundType.WOOL).noOcclusion()), block -> new PresentItem(block, new Item.Properties().stacksTo(1)), Category.EVENT);
    public static final RegistryObject<Block> TREE = LegacyRegistry.block("tree", () -> new TreeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.5F).sound(SoundType.WOOD).lightLevel(state -> 4).noOcclusion()), Category.EVENT, Category.LIGHTING);
    public static final RegistryObject<Block> MANTEL_PIECE = LegacyRegistry.block("mantel_piece", () -> new MantelPieceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.0F).sound(SoundType.STONE).noOcclusion()), Category.EVENT);
    public static final RegistryObject<Block> GRAND_CHAIR = LegacyRegistry.block("grand_chair", () -> new GrandChairBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK)), Category.EVENT, Category.GENERAL);
    public static final RegistryObject<Block> CANDLE = LegacyRegistry.block("candle", () -> new CandleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0F).sound(SoundType.STONE).lightLevel(state -> 12).noOcclusion()), Category.EVENT);
    public static final RegistryObject<Block> CHIMNEY = LegacyRegistry.block("chimney", () -> new ChimneyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.0F).sound(SoundType.STONE).noOcclusion()), Category.EVENT);
    public static final RegistryObject<Block> WREATH = LegacyRegistry.block("wreath", () -> new WreathBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.5F).sound(SoundType.GRASS).noCollission().noOcclusion()), Category.EVENT);
    public static final RegistryObject<Block> FAIRY_LIGHT = LegacyRegistry.block("fairy_light", () -> new FairyLightBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(0.5F).sound(SoundType.GLASS).lightLevel(state -> 7).noCollission().noOcclusion()), Category.EVENT, Category.LIGHTING);
    public static final RegistryObject<Block> FIRE_PIT = LegacyRegistry.block("fire_pit", () -> new FirePitBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).sound(SoundType.WOOD).lightLevel(state -> state.getValue(FirePitBlock.LIT) ? 15 : 0).noOcclusion()), block -> new FirePitItem(block, new Item.Properties()), Category.EVENT, Category.LIGHTING, Category.OUTDOORS);

    public static final RegistryObject<Item> CHOPPED_LOG = LegacyRegistry.item("chopped_log", () -> new ChoppedLogItem(FIRE_PIT.get(), new Item.Properties().stacksTo(16)), Category.ITEMS, Category.OUTDOORS);
    public static final RegistryObject<Item> ENVELOPE = LegacyRegistry.item("envelope", () -> new UnsignedMailItem(MailLayout.ENVELOPE, new Item.Properties().stacksTo(1)), Category.ITEMS);
    public static final RegistryObject<Item> SIGNED_ENVELOPE = LegacyRegistry.item("signed_envelope", () -> new SignedMailItem(MailLayout.ENVELOPE, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PACKAGE = LegacyRegistry.item("package", () -> new UnsignedMailItem(MailLayout.PACKAGE, new Item.Properties().stacksTo(1)), Category.ITEMS);
    public static final RegistryObject<Item> SIGNED_PACKAGE = LegacyRegistry.item("signed_package", () -> new SignedMailItem(MailLayout.PACKAGE, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<BlockEntityType<PresentBlockEntity>> PRESENT_BLOCK_ENTITY = ModBlockEntities.register("present", PresentBlockEntity::new, () -> LegacyRegistry.blocks(PRESENTS));
    public static final RegistryObject<BlockEntityType<TreeBlockEntity>> TREE_BLOCK_ENTITY = ModBlockEntities.register("tree", TreeBlockEntity::new, () -> new Block[]{TREE.get()});

    public static final RegistryObject<MenuType<MailItemMenu>> PRESENT_MENU = ModContainers.register("present", (windowId, inventory) -> new MailItemMenu(MailLayout.PRESENT, windowId, inventory));
    public static final RegistryObject<MenuType<MailItemMenu>> ENVELOPE_MENU = ModContainers.register("envelope", (windowId, inventory) -> new MailItemMenu(MailLayout.ENVELOPE, windowId, inventory));
    public static final RegistryObject<MenuType<MailItemMenu>> PACKAGE_MENU = ModContainers.register("package", (windowId, inventory) -> new MailItemMenu(MailLayout.PACKAGE, windowId, inventory));

    public static final RegistryObject<CuttingShapelessRecipe.Serializer> CUTTING_SHAPELESS = ModRecipeSerializers.REGISTER.register("cutting_shapeless", CuttingShapelessRecipe.Serializer::new);

    /** Forces the static registrations of this module to run. */
    public static void init() {}

    public static void commonSetup() {}

    public static void registerPackets()
    {
        PacketHandler.register(C2SMessageSignItem.class, new C2SMessageSignItem(), NetworkDirection.PLAY_TO_SERVER);
    }
}
