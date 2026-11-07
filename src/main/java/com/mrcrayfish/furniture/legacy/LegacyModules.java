package com.mrcrayfish.furniture.legacy;

import com.mrcrayfish.furniture.legacy.bathroom.BathroomContent;
import com.mrcrayfish.furniture.legacy.decor.DecorContent;
import com.mrcrayfish.furniture.legacy.electronics.ElectronicsContent;
import com.mrcrayfish.furniture.legacy.event.EventContent;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.shared.SharedContent;

/**
 * Entry point of the legacy modules.
 */
public class LegacyModules
{
    public static void init()
    {
        LegacyItems.init();
        LegacySounds.init();
        KitchenContent.init();
        BathroomContent.init();
        ElectronicsContent.init();
        DecorContent.init();
        EventContent.init();
        SharedContent.init();
    }

    public static void commonSetup()
    {
        LegacyTriggers.register();
        KitchenContent.commonSetup();
        BathroomContent.commonSetup();
        ElectronicsContent.commonSetup();
        DecorContent.commonSetup();
        EventContent.commonSetup();
        SharedContent.commonSetup();
    }

    public static void registerPackets()
    {
        KitchenContent.registerPackets();
        BathroomContent.registerPackets();
        ElectronicsContent.registerPackets();
        DecorContent.registerPackets();
        EventContent.registerPackets();
        SharedContent.registerPackets();
    }
}
