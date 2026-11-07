package com.mrcrayfish.furniture.legacy;

import com.mrcrayfish.furniture.legacy.bathroom.BathroomClient;
import com.mrcrayfish.furniture.legacy.decor.DecorClient;
import com.mrcrayfish.furniture.legacy.electronics.ElectronicsClient;
import com.mrcrayfish.furniture.legacy.event.EventClient;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenClient;
import com.mrcrayfish.furniture.legacy.shared.SharedClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

/**
 * Client entry point of the legacy modules.
 */
public class LegacyClient
{
    public static void setup()
    {
        KitchenClient.setup();
        BathroomClient.setup();
        ElectronicsClient.setup();
        DecorClient.setup();
        EventClient.setup();
        SharedClient.setup();
    }

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
    {
        KitchenClient.onRegisterBlockColors(event);
        BathroomClient.onRegisterBlockColors(event);
        ElectronicsClient.onRegisterBlockColors(event);
        DecorClient.onRegisterBlockColors(event);
        EventClient.onRegisterBlockColors(event);
        SharedClient.onRegisterBlockColors(event);
    }

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
    {
        KitchenClient.onRegisterItemColors(event);
        BathroomClient.onRegisterItemColors(event);
        ElectronicsClient.onRegisterItemColors(event);
        DecorClient.onRegisterItemColors(event);
        EventClient.onRegisterItemColors(event);
        SharedClient.onRegisterItemColors(event);
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        KitchenClient.onRegisterRenderers(event);
        BathroomClient.onRegisterRenderers(event);
        ElectronicsClient.onRegisterRenderers(event);
        DecorClient.onRegisterRenderers(event);
        EventClient.onRegisterRenderers(event);
        SharedClient.onRegisterRenderers(event);
    }
}
