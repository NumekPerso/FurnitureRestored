package com.mrcrayfish.furniture.legacy.kitchen;

import com.mrcrayfish.furniture.legacy.kitchen.client.BlenderRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.ChoppingBoardRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.CookieJarRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.CupRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.DishwasherScreen;
import com.mrcrayfish.furniture.legacy.kitchen.client.KitchenCabinetScreen;
import com.mrcrayfish.furniture.legacy.kitchen.client.MicrowaveRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.MicrowaveScreen;
import com.mrcrayfish.furniture.legacy.kitchen.client.OvenRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.OvenScreen;
import com.mrcrayfish.furniture.legacy.kitchen.client.PlateRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.ToasterRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.WashingMachineRenderer;
import com.mrcrayfish.furniture.legacy.kitchen.client.WashingMachineScreen;
import com.mrcrayfish.furniture.legacy.kitchen.item.CupItem;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

/**
 * Client side of module kitchen
 */
public class KitchenClient
{
    public static void setup()
    {
        MenuScreens.register(KitchenContent.OVEN_MENU.get(), OvenScreen::new);
        MenuScreens.register(KitchenContent.MICROWAVE_MENU.get(), MicrowaveScreen::new);
        MenuScreens.register(KitchenContent.WASHING_MACHINE_MENU.get(), WashingMachineScreen::new);
        MenuScreens.register(KitchenContent.DISHWASHER_MENU.get(), DishwasherScreen::new);
        MenuScreens.register(KitchenContent.KITCHEN_CABINET_MENU.get(), KitchenCabinetScreen::new);
    }

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {}

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
    {
        /* Layer 1 of the drink model is the liquid */
        event.register((stack, tintIndex) -> tintIndex == 1 ? CupItem.getColor(stack) | 0xFF000000 : -1, KitchenContent.DRINK.get());
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(KitchenContent.OVEN_ENTITY.get(), OvenRenderer::new);
        event.registerBlockEntityRenderer(KitchenContent.TOASTER_ENTITY.get(), ToasterRenderer::new);
        event.registerBlockEntityRenderer(KitchenContent.MICROWAVE_ENTITY.get(), MicrowaveRenderer::new);
        event.registerBlockEntityRenderer(KitchenContent.BLENDER_ENTITY.get(), BlenderRenderer::new);
        event.registerBlockEntityRenderer(KitchenContent.WASHING_MACHINE_ENTITY.get(), WashingMachineRenderer::new);
        event.registerBlockEntityRenderer(KitchenContent.CUP_ENTITY.get(), CupRenderer::new);
        event.registerBlockEntityRenderer(KitchenContent.PLATE_ENTITY.get(), PlateRenderer::new);
        event.registerBlockEntityRenderer(KitchenContent.COOKIE_JAR_ENTITY.get(), CookieJarRenderer::new);
        event.registerBlockEntityRenderer(KitchenContent.CHOPPING_BOARD_ENTITY.get(), ChoppingBoardRenderer::new);
    }
}
