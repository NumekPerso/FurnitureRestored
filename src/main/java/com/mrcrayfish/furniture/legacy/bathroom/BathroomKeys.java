package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.network.PacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * The fart key. RegisterKeyMappingsEvent fires before BathroomClient#setup, so it needs its own
 * subscriber on the mod bus.
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = "furniture_restored")
public class BathroomKeys
{
    public static final KeyMapping FART = new KeyMapping("key.furniture_restored.fart", GLFW.GLFW_KEY_G, "keys.furniture_restored.category");

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.register(FART);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = "furniture_restored")
    public static class Input
    {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event)
        {
            if(event.phase != TickEvent.Phase.END)
                return;
            Player player = Minecraft.getInstance().player;
            while(FART.consumeClick())
            {
                if(player != null && ToiletBlock.isSeatedOnToilet(player))
                {
                    PacketHandler.getPlayChannel().sendToServer(new C2SMessageFart());
                }
            }
        }
    }
}
