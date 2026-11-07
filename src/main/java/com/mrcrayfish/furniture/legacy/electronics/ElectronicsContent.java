package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.core.ModBlockEntities;
import com.mrcrayfish.furniture.core.ModContainers;
import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.legacy.LegacyRegistry;
import com.mrcrayfish.furniture.legacy.LegacyRegistry.Category;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Module electronics
 */
public class ElectronicsContent
{
    public static final TagKey<Item> PRINTER_COPYABLE = TagKey.create(Registries.ITEM, new ResourceLocation(Reference.MOD_ID, "printer_copyable"));

    public static final RegistryObject<Block> COMPUTER = LegacyRegistry.block("computer", () -> new ComputerBlock(props(1.0F, SoundType.ANVIL)), Category.ELECTRONICS);
    public static final RegistryObject<Block> TV = LegacyRegistry.block("tv", () -> new TvBlock(props(0F, SoundType.METAL)), Category.ELECTRONICS, Category.BEDROOM);
    public static final RegistryObject<Block> MODERN_TV = LegacyRegistry.block("modern_tv", () -> new ModernTvBlock(props(1.0F, SoundType.STONE)), Category.ELECTRONICS, Category.BEDROOM);

    public static final RegistryObject<Block> TV_STAND = LegacyRegistry.block("tv_stand", () -> new TvStandBlock(props(0.5F, SoundType.WOOD)), Category.BEDROOM, Category.STORAGE);
    public static final RegistryObject<Block> PRINTER = LegacyRegistry.block("printer", () -> new PrinterBlock(props(1.0F, SoundType.ANVIL)), Category.ELECTRONICS);
    public static final RegistryObject<Block> STEREO = LegacyRegistry.block("stereo", () -> new StereoBlock(props(1.0F, SoundType.METAL)), Category.ELECTRONICS, Category.BEDROOM);
    public static final Map<DyeColor, RegistryObject<Block>> DIGITAL_CLOCKS = LegacyRegistry.dyed("digital_clock", color -> new DigitalClockBlock(props(0.5F, SoundType.WOOD).lightLevel(state -> 7)), Category.ELECTRONICS, Category.BEDROOM);
    public static final RegistryObject<Block> ELECTRIC_FENCE = LegacyRegistry.block("electric_fence", () -> new ElectricFenceBlock(props(1.0F, SoundType.ANVIL).lightLevel(state -> 3)), Category.OUTDOORS);
    public static final List<RegistryObject<Block>> DOOR_BELLS = List.of(doorBell("oak"), doorBell("spruce"), doorBell("birch"), doorBell("jungle"), doorBell("acacia"), doorBell("dark_oak"));
    public static final RegistryObject<Block> FIRE_ALARM = LegacyRegistry.block("fire_alarm", () -> new FireAlarmBlock(props(0.5F, SoundType.STONE).lightLevel(state -> state.getValue(FireAlarmBlock.ON) ? 15 : 3)), Category.ELECTRONICS, Category.KITCHEN);
    public static final RegistryObject<Block> CEILING_LIGHT = LegacyRegistry.block("ceiling_light", () -> new CeilingLightBlock(props(0.5F, SoundType.GLASS).lightLevel(state -> state.getValue(CeilingLightBlock.LIT) ? 15 : 0)), Category.ELECTRONICS, Category.BEDROOM, Category.LIGHTING);
    public static final RegistryObject<Block> MODERN_LIGHT = LegacyRegistry.block("modern_light", () -> new ModernLightBlock(props(0.5F, SoundType.STONE).lightLevel(state -> state.getValue(ModernLightBlock.LIT) ? 15 : 0)), Category.ELECTRONICS, Category.BEDROOM, Category.LIGHTING);
    public static final RegistryObject<Block> LIGHT_SWITCH = LegacyRegistry.block("light_switch", () -> new LightSwitchBlock(props(0F, SoundType.WOOD)), block -> new LightSwitchItem(block, new Properties()), Category.ELECTRONICS, Category.BEDROOM, Category.LIGHTING);
    public static final RegistryObject<Block> CEILING_FAN = LegacyRegistry.block("ceiling_fan", () -> new CeilingFanBlock(props(0.5F, SoundType.WOOD)), Category.ELECTRONICS, Category.BEDROOM);
    public static final Map<DyeColor, RegistryObject<Block>> LAMPS = LegacyRegistry.dyed("lamp", color -> new LampBlock(props(0.75F, SoundType.WOOL).lightLevel(state -> state.getValue(LampBlock.LIT) ? 15 : 0)), Category.BEDROOM, Category.LIGHTING);

    public static final RegistryObject<BlockEntityType<TvStandBlockEntity>> TV_STAND_ENTITY = ModBlockEntities.register("tv_stand", TvStandBlockEntity::new, single(TV_STAND));
    public static final RegistryObject<BlockEntityType<PrinterBlockEntity>> PRINTER_ENTITY = ModBlockEntities.register("printer", PrinterBlockEntity::new, single(PRINTER));
    public static final RegistryObject<BlockEntityType<StereoBlockEntity>> STEREO_ENTITY = ModBlockEntities.register("stereo", StereoBlockEntity::new, single(STEREO));
    public static final RegistryObject<BlockEntityType<DigitalClockBlockEntity>> DIGITAL_CLOCK_ENTITY = ModBlockEntities.register("digital_clock", DigitalClockBlockEntity::new, () -> LegacyRegistry.blocks(DIGITAL_CLOCKS));
    public static final RegistryObject<BlockEntityType<LightSwitchBlockEntity>> LIGHT_SWITCH_ENTITY = ModBlockEntities.register("light_switch", LightSwitchBlockEntity::new, single(LIGHT_SWITCH));
    public static final RegistryObject<BlockEntityType<CeilingFanBlockEntity>> CEILING_FAN_ENTITY = ModBlockEntities.register("ceiling_fan", CeilingFanBlockEntity::new, single(CEILING_FAN));

    public static final RegistryObject<MenuType<PrinterMenu>> PRINTER_MENU = ModContainers.register("printer", PrinterMenu::new);

    private static BlockBehaviour.Properties props(float strength, SoundType sound)
    {
        return BlockBehaviour.Properties.of().strength(strength).sound(sound).noOcclusion();
    }

    private static RegistryObject<Block> doorBell(String wood)
    {
        return LegacyRegistry.block(wood + "_door_bell", () -> new DoorBellBlock(BlockBehaviour.Properties.of().strength(0F).sound(SoundType.WOOD).noCollission()), Category.ELECTRONICS, Category.OUTDOORS);
    }

    private static Supplier<Block[]> single(RegistryObject<Block> block)
    {
        return () -> new Block[]{block.get()};
    }

    /** Forces the static registrations of this module to run. */
    public static void init() {}

    public static void commonSetup() {}

    public static void registerPackets() {}
}
