package com.mrcrayfish.furniture.legacy.bathroom;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

/**
 * Client side of module bathroom
 */
public class BathroomClient
{
    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;

    public static void setup()
    {
        MenuScreens.register(BathroomContent.WALL_CABINET_MENU.get(), WallCabinetScreen::new);
        MenuScreens.register(BathroomContent.BIN_MENU.get(), BinScreen::new);
    }

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
    {
        event.register((state, level, pos, tintIndex) -> {
            if(tintIndex != 0)
                return -1;
            return level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : DEFAULT_WATER_COLOR;
        }, BathroomContent.TOILET.get(), BathroomContent.BASIN.get(), BathroomContent.BATH.get());
    }

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
    {
        event.register((stack, tintIndex) -> tintIndex == 0 ? DEFAULT_WATER_COLOR : -1, BathroomContent.TOILET.get(), BathroomContent.BASIN.get(), BathroomContent.BATH.get());
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {}
}
