package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

/**
 * Client side of module event
 */
public class EventClient
{
    public static void setup()
    {
        MenuScreens.register(EventContent.PRESENT_MENU.get(), MailItemScreen::new);
        MenuScreens.register(EventContent.ENVELOPE_MENU.get(), MailItemScreen::new);
        MenuScreens.register(EventContent.PACKAGE_MENU.get(), MailItemScreen::new);
    }

    /* The tint index differs between the models, so the handlers must not depend on its value. */
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
    {
        event.register((state, level, pos, tintIndex) -> FoliageColor.getEvergreenColor(), EventContent.TREE.get(), EventContent.WREATH.get());
    }

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
    {
        event.register((stack, tintIndex) -> FoliageColor.getEvergreenColor(), EventContent.TREE.get(), EventContent.WREATH.get());
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(EventContent.TREE_BLOCK_ENTITY.get(), TreeBlockEntityRenderer::new);
    }
}
