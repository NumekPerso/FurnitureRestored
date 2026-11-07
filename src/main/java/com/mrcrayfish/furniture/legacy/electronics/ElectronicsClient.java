package com.mrcrayfish.furniture.legacy.electronics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Client side of module electronics
 */
public class ElectronicsClient
{
    private static final int TOOLTIP_WIDTH = 150;

    public static void setup()
    {
        MenuScreens.register(ElectronicsContent.PRINTER_MENU.get(), PrinterScreen::new);
    }

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {}

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {}

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ElectronicsContent.DIGITAL_CLOCK_ENTITY.get(), DigitalClockRenderer::new);
        event.registerBlockEntityRenderer(ElectronicsContent.CEILING_FAN_ENTITY.get(), CeilingFanRenderer::new);
    }

    /**
     * Adds the text shown while shift is held, otherwise the optional description and a hint to hold shift.
     * Tooltips are only built on the client, so this class is not loaded on a server.
     */
    public static void addInfo(List<Component> tooltip, String infoKey, @Nullable String descriptionKey)
    {
        if(Screen.hasShiftDown())
        {
            addWrapped(tooltip, infoKey);
            return;
        }
        if(descriptionKey != null)
        {
            addWrapped(tooltip, descriptionKey);
        }
        tooltip.add(Component.translatable("furniture_restored.info").withStyle(ChatFormatting.YELLOW));
    }

    private static void addWrapped(List<Component> tooltip, String key)
    {
        for(FormattedText line : Minecraft.getInstance().font.getSplitter().splitLines(Component.translatable(key), TOOLTIP_WIDTH, Style.EMPTY))
        {
            tooltip.add(Component.literal(line.getString()));
        }
    }
}
