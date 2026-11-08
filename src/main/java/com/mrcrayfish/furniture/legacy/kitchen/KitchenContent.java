package com.mrcrayfish.furniture.legacy.kitchen;

import com.mrcrayfish.furniture.core.ModBlockEntities;
import com.mrcrayfish.furniture.core.ModContainers;
import com.mrcrayfish.furniture.legacy.LegacyRegistry;
import com.mrcrayfish.furniture.legacy.LegacyRegistry.Category;
import com.mrcrayfish.furniture.legacy.kitchen.block.BarStoolBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.BlenderBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.ChoppingBoardBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.CookieJarBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.CupBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.DishwasherBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.KitchenCabinetBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.MicrowaveBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.OvenBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.PlateBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.RangeHoodBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.ToasterBlock;
import com.mrcrayfish.furniture.legacy.kitchen.block.WashingMachineBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.BlenderBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.ChoppingBoardBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.CookieJarBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.CupBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.DishwasherBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.KitchenCabinetBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.MicrowaveBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.OvenBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.PlateBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.ToasterBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingMachineBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.item.CupItem;
import com.mrcrayfish.furniture.legacy.kitchen.menu.DishwasherMenu;
import com.mrcrayfish.furniture.legacy.kitchen.menu.KitchenCabinetMenu;
import com.mrcrayfish.furniture.legacy.kitchen.menu.MicrowaveMenu;
import com.mrcrayfish.furniture.legacy.kitchen.menu.OvenMenu;
import com.mrcrayfish.furniture.legacy.kitchen.menu.WashingMachineMenu;
import com.mrcrayfish.furniture.legacy.kitchen.network.C2SMessageMachineControl;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import com.mrcrayfish.furniture.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Module kitchen
 */
public class KitchenContent
{
    public static final RegistryObject<Block> OVEN = LegacyRegistry.block("oven", () -> new OvenBlock(properties(MapColor.METAL, 1.0F, SoundType.METAL)), Category.KITCHEN);
    public static final RegistryObject<Block> RANGE_HOOD = LegacyRegistry.block("range_hood", () -> new RangeHoodBlock(properties(MapColor.METAL, 0.5F, SoundType.METAL).lightLevel(state -> state.getValue(RangeHoodBlock.POWERED) ? 7 : 0)), Category.KITCHEN);
    public static final RegistryObject<Block> TOASTER = LegacyRegistry.block("toaster", () -> new ToasterBlock(properties(MapColor.METAL, 0.5F, SoundType.ANVIL)), Category.ELECTRONICS, Category.KITCHEN);
    public static final RegistryObject<Block> MICROWAVE = LegacyRegistry.block("microwave", () -> new MicrowaveBlock(properties(MapColor.METAL, 0.5F, SoundType.ANVIL)), Category.ELECTRONICS, Category.KITCHEN);
    public static final RegistryObject<Block> BLENDER = LegacyRegistry.block("blender", () -> new BlenderBlock(properties(MapColor.METAL, 0.5F, SoundType.GLASS)), Category.ELECTRONICS, Category.KITCHEN);
    public static final RegistryObject<Block> WASHING_MACHINE = LegacyRegistry.block("washing_machine", () -> new WashingMachineBlock(properties(MapColor.METAL, 1.0F, SoundType.ANVIL)), Category.ELECTRONICS, Category.KITCHEN);
    public static final RegistryObject<Block> DISHWASHER = LegacyRegistry.block("dishwasher", () -> new DishwasherBlock(properties(MapColor.METAL, 0.5F, SoundType.ANVIL)), Category.ELECTRONICS, Category.KITCHEN);
    public static final RegistryObject<Block> CUP = LegacyRegistry.blockNoItem("cup", () -> new CupBlock(properties(MapColor.NONE, 0.1F, SoundType.GLASS).noLootTable()));
    public static final RegistryObject<Block> PLATE = LegacyRegistry.block("plate", () -> new PlateBlock(properties(MapColor.QUARTZ, 0.5F, SoundType.STONE)), Category.KITCHEN);
    public static final RegistryObject<Block> COOKIE_JAR = LegacyRegistry.block("cookie_jar", () -> new CookieJarBlock(properties(MapColor.NONE, 0.5F, SoundType.GLASS)), Category.KITCHEN);
    public static final RegistryObject<Block> CHOPPING_BOARD = LegacyRegistry.block("chopping_board", () -> new ChoppingBoardBlock(properties(MapColor.WOOD, 0.5F, SoundType.WOOD)), Category.KITCHEN);
    public static final Map<DyeColor, RegistryObject<Block>> BAR_STOOLS = LegacyRegistry.dyed("bar_stool", color -> new BarStoolBlock(properties(color.getMapColor(), 0.5F, SoundType.WOOD)), Category.GENERAL, Category.KITCHEN);
    public static final Map<DyeColor, RegistryObject<Block>> KITCHEN_CABINETS = LegacyRegistry.dyed("kitchen_cabinet", color -> new KitchenCabinetBlock(properties(color.getMapColor(), 0.5F, SoundType.WOOD)), Category.KITCHEN, Category.STORAGE);

    public static final RegistryObject<Item> CUP_ITEM = LegacyRegistry.item("cup", () -> new CupItem(new Item.Properties(), false), Category.ITEMS, Category.KITCHEN);
    public static final RegistryObject<Item> DRINK = LegacyRegistry.item("drink", () -> new CupItem(new Item.Properties().stacksTo(1), true));

    public static final RegistryObject<BlockEntityType<OvenBlockEntity>> OVEN_ENTITY = ModBlockEntities.register("oven", OvenBlockEntity::new, () -> new Block[]{OVEN.get()});
    public static final RegistryObject<BlockEntityType<ToasterBlockEntity>> TOASTER_ENTITY = ModBlockEntities.register("toaster", ToasterBlockEntity::new, () -> new Block[]{TOASTER.get()});
    public static final RegistryObject<BlockEntityType<MicrowaveBlockEntity>> MICROWAVE_ENTITY = ModBlockEntities.register("microwave", MicrowaveBlockEntity::new, () -> new Block[]{MICROWAVE.get()});
    public static final RegistryObject<BlockEntityType<BlenderBlockEntity>> BLENDER_ENTITY = ModBlockEntities.register("blender", BlenderBlockEntity::new, () -> new Block[]{BLENDER.get()});
    public static final RegistryObject<BlockEntityType<WashingMachineBlockEntity>> WASHING_MACHINE_ENTITY = ModBlockEntities.register("washing_machine", WashingMachineBlockEntity::new, () -> new Block[]{WASHING_MACHINE.get()});
    public static final RegistryObject<BlockEntityType<DishwasherBlockEntity>> DISHWASHER_ENTITY = ModBlockEntities.register("dishwasher", DishwasherBlockEntity::new, () -> new Block[]{DISHWASHER.get()});
    public static final RegistryObject<BlockEntityType<CupBlockEntity>> CUP_ENTITY = ModBlockEntities.register("cup", CupBlockEntity::new, () -> new Block[]{CUP.get()});
    public static final RegistryObject<BlockEntityType<PlateBlockEntity>> PLATE_ENTITY = ModBlockEntities.register("plate", PlateBlockEntity::new, () -> new Block[]{PLATE.get()});
    public static final RegistryObject<BlockEntityType<CookieJarBlockEntity>> COOKIE_JAR_ENTITY = ModBlockEntities.register("cookie_jar", CookieJarBlockEntity::new, () -> new Block[]{COOKIE_JAR.get()});
    public static final RegistryObject<BlockEntityType<ChoppingBoardBlockEntity>> CHOPPING_BOARD_ENTITY = ModBlockEntities.register("chopping_board", ChoppingBoardBlockEntity::new, () -> new Block[]{CHOPPING_BOARD.get()});
    public static final RegistryObject<BlockEntityType<KitchenCabinetBlockEntity>> KITCHEN_CABINET_ENTITY = ModBlockEntities.register("kitchen_cabinet", KitchenCabinetBlockEntity::new, () -> LegacyRegistry.blocks(KITCHEN_CABINETS));

    public static final RegistryObject<MenuType<OvenMenu>> OVEN_MENU = ModContainers.register("oven", (IContainerFactory<OvenMenu>) (windowId, inventory, data) -> new OvenMenu(windowId, inventory, clientBlockEntity(inventory, data, OvenBlockEntity.class, OvenBlockEntity::new, OVEN), new SimpleContainerData(OvenBlockEntity.DATA_COUNT)));
    public static final RegistryObject<MenuType<MicrowaveMenu>> MICROWAVE_MENU = ModContainers.register("microwave", (IContainerFactory<MicrowaveMenu>) (windowId, inventory, data) -> new MicrowaveMenu(windowId, inventory, clientBlockEntity(inventory, data, MicrowaveBlockEntity.class, MicrowaveBlockEntity::new, MICROWAVE), new SimpleContainerData(MicrowaveBlockEntity.DATA_COUNT)));
    public static final RegistryObject<MenuType<WashingMachineMenu>> WASHING_MACHINE_MENU = ModContainers.register("washing_machine", (IContainerFactory<WashingMachineMenu>) (windowId, inventory, data) -> new WashingMachineMenu(windowId, inventory, clientBlockEntity(inventory, data, WashingMachineBlockEntity.class, WashingMachineBlockEntity::new, WASHING_MACHINE), new SimpleContainerData(WashingBlockEntity.DATA_COUNT)));
    public static final RegistryObject<MenuType<DishwasherMenu>> DISHWASHER_MENU = ModContainers.register("dishwasher", (IContainerFactory<DishwasherMenu>) (windowId, inventory, data) -> new DishwasherMenu(windowId, inventory, clientBlockEntity(inventory, data, DishwasherBlockEntity.class, DishwasherBlockEntity::new, DISHWASHER), new SimpleContainerData(WashingBlockEntity.DATA_COUNT)));
    public static final RegistryObject<MenuType<KitchenCabinetMenu>> KITCHEN_CABINET_MENU = ModContainers.register("kitchen_cabinet", (IContainerFactory<KitchenCabinetMenu>) (windowId, inventory, data) -> new KitchenCabinetMenu(windowId, inventory, new SimpleContainer(15)));

    /**
     * The block entity of a machine whose menu opens on the client. A client out of sync (chunk
     * not received yet, block just broken) has none, or another one: the cast crashed the game.
     * A detached block entity stands in, the menu then shows the slots the server syncs.
     */
    private static <T extends BlockEntity> T clientBlockEntity(Inventory inventory, FriendlyByteBuf data, Class<T> type, BiFunction<BlockPos, BlockState, T> factory, RegistryObject<Block> block)
    {
        BlockPos pos = data.readBlockPos();
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(pos);
        return type.isInstance(blockEntity) ? type.cast(blockEntity) : factory.apply(pos, block.get().defaultBlockState());
    }

    private static Properties properties(MapColor color, float strength, SoundType sound)
    {
        return Properties.of().mapColor(color).strength(strength).sound(sound).noOcclusion();
    }

    /** Forces the static registrations of this module to run. */
    public static void init()
    {
        KitchenRecipes.init();
        com.mrcrayfish.furniture.legacy.kitchen.recipe.NoRemainderRecipes.init();
    }

    public static void commonSetup() {}

    public static void registerPackets()
    {
        PacketHandler.register(C2SMessageMachineControl.class, new C2SMessageMachineControl(), NetworkDirection.PLAY_TO_SERVER);
    }
}
