package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.core.ModBlockEntities;
import com.mrcrayfish.furniture.legacy.LegacyRegistry;
import com.mrcrayfish.furniture.legacy.LegacyRegistry.Category;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

/**
 * Module decor
 */
public class DecorContent
{
    /** Steps of the inflatable castle are silent. */
    private static final SoundType CASTLE_SOUND = new SoundType(0.0F, 1.0F, SoundEvents.WOOL_BREAK, SoundEvents.WOOL_STEP, SoundEvents.WOOL_PLACE, SoundEvents.WOOL_HIT, SoundEvents.WOOL_FALL);

    public static final Map<DyeColor, RegistryObject<Block>> CURTAINS = LegacyRegistry.dyed("curtains", color -> new CurtainsBlock(props(color.getMapColor(), 0.5F, SoundType.WOOL).noOcclusion()), Category.BEDROOM);
    public static final Map<DyeColor, RegistryObject<Block>> PHOTO_FRAMES = LegacyRegistry.dyed("photo_frame", color -> new PhotoFrameBlock(props(color.getMapColor(), 0.0F, SoundType.STONE).noOcclusion()), Category.BEDROOM);
    public static final RegistryObject<Block> BIRD_BATH = LegacyRegistry.block("bird_bath", () -> new BirdBathBlock(props(MapColor.STONE, 1.0F, SoundType.STONE).noOcclusion()), Category.OUTDOORS);
    public static final RegistryObject<Block> TAP = LegacyRegistry.block("tap", () -> new TapBlock(props(MapColor.STONE, 0.5F, SoundType.STONE).noOcclusion()), Category.OUTDOORS);
    public static final RegistryObject<Block> MODERN_WINDOW = LegacyRegistry.block("modern_window", () -> new ModernWindowBlock(props(MapColor.SNOW, 0.0F, SoundType.STONE).noOcclusion()), Category.BATHROOM, Category.BEDROOM, Category.KITCHEN);
    public static final RegistryObject<Block> MODERN_SLIDING_DOOR = LegacyRegistry.block("modern_sliding_door", () -> new ModernSlidingDoorBlock(props(MapColor.SNOW, 1.0F, SoundType.STONE).noOcclusion().pushReaction(PushReaction.DESTROY)), block -> new DoubleHighBlockItem(block, new Item.Properties()), Category.BEDROOM, Category.OUTDOORS);
    public static final RegistryObject<BlockEntityType<ModernSlidingDoorBlockEntity>> MODERN_SLIDING_DOOR_ENTITY = ModBlockEntities.register("modern_sliding_door", ModernSlidingDoorBlockEntity::new, () -> new Block[]{MODERN_SLIDING_DOOR.get()});
    public static final Map<DyeColor, RegistryObject<Block>> MODERN_COUCHES = LegacyRegistry.dyed("modern_couch", color -> new ModernCouchBlock(props(color.getMapColor(), 0.5F, SoundType.WOOD).noOcclusion()), Category.GENERAL);
    public static final RegistryObject<Block> MODERN_TABLE = LegacyRegistry.block("modern_table", () -> new ModernTableBlock(props(MapColor.SNOW, 1.0F, SoundType.WOOD).noOcclusion()), Category.GENERAL, Category.KITCHEN);
    public static final RegistryObject<Block> MODERN_CHAIR = LegacyRegistry.block("modern_chair", () -> new ModernChairBlock(props(MapColor.SNOW, 1.0F, SoundType.WOOD).noOcclusion()), Category.GENERAL, Category.KITCHEN);
    public static final RegistryObject<Block> MODERN_COFFEE_TABLE = LegacyRegistry.block("modern_coffee_table", () -> new ModernCoffeeTableBlock(props(MapColor.SNOW, 1.0F, SoundType.WOOD).noOcclusion()), Category.GENERAL);
    public static final RegistryObject<Block> MODERN_STAIRS = LegacyRegistry.block("modern_stairs", () -> new ModernStairsBlock(props(MapColor.SNOW, 1.0F, SoundType.STONE).noOcclusion()), Category.BEDROOM);
    public static final RegistryObject<Block> MODERN_TABLE_OUTDOOR = LegacyRegistry.block("modern_table_outdoor", () -> new GlassTableBlock(props(MapColor.SNOW, 1.0F, SoundType.WOOD).noOcclusion()), Category.OUTDOORS);
    public static final Map<DyeColor, RegistryObject<Block>> MODERN_BEDS = LegacyRegistry.dyed("modern_bed", color -> new ModernBedBlock(color, props(color.getMapColor(), 0.5F, SoundType.STONE).noOcclusion().pushReaction(PushReaction.DESTROY)), block -> new BedItem(block, new Item.Properties().stacksTo(1)), Category.BEDROOM);
    public static final RegistryObject<Block> BENCH = LegacyRegistry.block("bench", () -> new BenchBlock(props(MapColor.WOOD, 1.0F, SoundType.WOOD).noOcclusion()), Category.GENERAL, Category.OUTDOORS);
    public static final RegistryObject<Block> TABLE_OUTDOOR = LegacyRegistry.block("outdoor_table", () -> new ModernTableBlock(props(MapColor.WOOD, 1.0F, SoundType.WOOD).noOcclusion()), Category.GENERAL, Category.OUTDOORS);
    public static final Map<DyeColor, RegistryObject<Block>> INFLATABLE_CASTLES = LegacyRegistry.dyed("inflatable_castle", color -> new InflatableCastleBlock(props(color.getMapColor(), 0.25F, CASTLE_SOUND)), Category.OUTDOORS);
    public static final RegistryObject<Block> CASTLE_NETTING = LegacyRegistry.block("castle_netting", () -> new CastleNettingBlock(props(MapColor.COLOR_BLACK, 0.0F, SoundType.STONE).noOcclusion()), Category.OUTDOORS);

    private static BlockBehaviour.Properties props(MapColor color, float strength, SoundType sound)
    {
        return BlockBehaviour.Properties.of().mapColor(color).strength(strength).sound(sound);
    }

    /** Forces the static registrations of this module to run. */
    public static void init() {}

    public static void commonSetup() {}

    public static void registerPackets() {}
}
