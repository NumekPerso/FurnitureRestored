package com.mrcrayfish.furniture.legacy.shared;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

/**
 * Client side of module shared
 */
public class SharedClient
{
    public static void setup()
    {
        MenuScreens.<ChestMenu, ContainerScreen>register(SharedContent.FRIDGE_MENU.get(), ContainerScreen::new);
        MenuScreens.<ChestMenu, ContainerScreen>register(SharedContent.COOLER_MENU.get(), ContainerScreen::new);
    }

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {}

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {}

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(SharedContent.COFFEE_TABLE.get(), CoffeeTableRenderer::new);
    }

    /** "Hold SHIFT for Info" tooltip. */
    public static void addShiftInfo(List<Component> tooltip, String infoKey)
    {
        if(Screen.hasShiftDown())
        {
            for(FormattedText line : Minecraft.getInstance().font.getSplitter().splitLines(Component.translatable(infoKey), 150, Style.EMPTY))
            {
                tooltip.add(Component.literal(line.getString()).withStyle(ChatFormatting.GRAY));
            }
        }
        else
        {
            tooltip.add(Component.translatable("furniture_restored.info").withStyle(ChatFormatting.YELLOW));
        }
    }
}
