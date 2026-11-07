package com.mrcrayfish.furniture.legacy.decor;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

/**
 * Client side of module decor
 */
public class DecorClient
{
    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;

    public static void setup() {}

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
    {
        event.register((state, level, pos, tintIndex) -> {
            if(tintIndex != 0)
            {
                return -1;
            }
            return level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : DEFAULT_WATER_COLOR;
        }, DecorContent.BIRD_BATH.get());
    }

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
    {
        event.register((stack, tintIndex) -> tintIndex == 0 ? DEFAULT_WATER_COLOR : -1, DecorContent.BIRD_BATH.get());
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(DecorContent.MODERN_SLIDING_DOOR_ENTITY.get(), ModernSlidingDoorRenderer::new);
    }
}
