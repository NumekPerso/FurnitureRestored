package com.mrcrayfish.furniture.legacy;

import com.mrcrayfish.furniture.core.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.RegistryObject;

/**
 * Sounds of the legacy modules. The files are in assets/furniture_restored/sounds/legacy.
 */
public class LegacySounds
{
    public static final RegistryObject<SoundEvent> DOOR_BELL = ModSounds.register("legacy.door_bell");
    public static final RegistryObject<SoundEvent> FIRE_ALARM = ModSounds.register("legacy.fire_alarm");
    public static final RegistryObject<SoundEvent> ZAP = ModSounds.register("legacy.zap");
    public static final RegistryObject<SoundEvent> FLUSH = ModSounds.register("legacy.flush");
    public static final RegistryObject<SoundEvent> SHOWER = ModSounds.register("legacy.shower");
    public static final RegistryObject<SoundEvent> BIN_OPEN = ModSounds.register("legacy.bin_open");
    public static final RegistryObject<SoundEvent> BIN_CLOSE = ModSounds.register("legacy.bin_close");
    public static final RegistryObject<SoundEvent> TAP = ModSounds.register("legacy.tap");
    public static final RegistryObject<SoundEvent> FART_1 = ModSounds.register("legacy.fart_1");
    public static final RegistryObject<SoundEvent> FART_2 = ModSounds.register("legacy.fart_2");
    public static final RegistryObject<SoundEvent> FART_3 = ModSounds.register("legacy.fart_3");
    public static final RegistryObject<SoundEvent> BLENDER = ModSounds.register("legacy.blender");
    public static final RegistryObject<SoundEvent> DISHWASHER = ModSounds.register("legacy.dishwasher");
    public static final RegistryObject<SoundEvent> KNIFE_CHOP = ModSounds.register("legacy.knife_chop");
    public static final RegistryObject<SoundEvent> MICROWAVE_RUNNING = ModSounds.register("legacy.microwave_running");
    public static final RegistryObject<SoundEvent> MICROWAVE_FINISH = ModSounds.register("legacy.microwave_finish");
    public static final RegistryObject<SoundEvent> TOASTER_DOWN = ModSounds.register("legacy.toaster_down");
    public static final RegistryObject<SoundEvent> TOASTER_UP = ModSounds.register("legacy.toaster_up");
    public static final RegistryObject<SoundEvent> WASHING_MACHINE = ModSounds.register("legacy.washing_machine");
    public static final RegistryObject<SoundEvent> LIGHT_SWITCH = ModSounds.register("legacy.light_switch");
    public static final RegistryObject<SoundEvent> SLIDING_DOOR_OPEN = ModSounds.register("legacy.sliding_door_open");
    public static final RegistryObject<SoundEvent> SLIDING_DOOR_CLOSE = ModSounds.register("legacy.sliding_door_close");
    public static final RegistryObject<SoundEvent> SIZZLE = ModSounds.register("legacy.sizzle");
    public static final RegistryObject<SoundEvent> BOUNCE = ModSounds.register("legacy.bounce");

    public static SoundEvent randomFart(RandomSource random)
    {
        return switch(random.nextInt(3))
        {
            case 0 -> FART_1.get();
            case 1 -> FART_2.get();
            default -> FART_3.get();
        };
    }

    public static void init() {}
}
