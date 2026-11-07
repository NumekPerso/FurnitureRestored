package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.furniture.legacy.kitchen.menu.KitchenMenu;
import com.mrcrayfish.furniture.legacy.kitchen.network.C2SMessageMachineControl;
import com.mrcrayfish.furniture.network.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.BooleanSupplier;

/**
 * Common part of the kitchen screens.
 */
public abstract class KitchenScreen<T extends KitchenMenu> extends AbstractContainerScreen<T>
{
    protected static final int LABEL_COLOR = 4210752;
    protected static final int COLOR_RUNNING = -16711936;
    protected static final int COLOR_STOPPED = -65280;

    private final ResourceLocation texture;
    private Button startButton;
    private BooleanSupplier running = () -> false;

    protected KitchenScreen(T menu, Inventory inventory, Component title, ResourceLocation texture, int width, int height)
    {
        super(menu, inventory, title);
        this.texture = texture;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    /** Adds the Start/Stop button, at a position relative to the screen. */
    protected void addStartButton(int x, int y, BooleanSupplier running)
    {
        this.running = running;
        int labelWidth = Math.max(this.font.width(Component.translatable("furniture_restored.button.start")), this.font.width(Component.translatable("furniture_restored.button.stop")));
        int width = Math.max(32, labelWidth + 8);
        this.startButton = this.addRenderableWidget(Button.builder(this.getStartLabel(), button -> {
            PacketHandler.getPlayChannel().sendToServer(new C2SMessageMachineControl(!this.running.getAsBoolean()));
        }).bounds(this.leftPos + x, this.topPos + y, width, 20).build());
    }

    private Component getStartLabel()
    {
        return Component.translatable(this.running.getAsBoolean() ? "furniture_restored.button.stop" : "furniture_restored.button.start");
    }

    protected ResourceLocation getTexture()
    {
        return this.texture;
    }

    /** Draws what is above the background, at the screen position. */
    protected abstract void renderDetails(GuiGraphics graphics, int left, int top);

    /** Draws the tooltips of the indicators. */
    protected abstract void renderIndicatorTooltips(GuiGraphics graphics, int mouseX, int mouseY);

    /** The light of the machine, with its tooltip. */
    protected void renderStatusTooltip(GuiGraphics graphics, boolean running, int mouseX, int mouseY)
    {
        if(this.isHovering(37, 9, 11, 11, mouseX, mouseY))
        {
            graphics.renderTooltip(this.font, Component.translatable(running ? "furniture_restored.gui.run" : "furniture_restored.gui.stop"), mouseX, mouseY);
        }
    }

    protected void renderStatus(GuiGraphics graphics, int left, int top, boolean running)
    {
        graphics.fill(left + 37, top + 9, left + 48, top + 20, running ? COLOR_RUNNING : COLOR_STOPPED);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if(this.startButton != null)
        {
            this.startButton.setMessage(this.getStartLabel());
        }
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        if(this.menu.getCarried().isEmpty() && this.hoveredSlot == null)
        {
            this.renderIndicatorTooltips(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(this.texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.renderDetails(graphics, this.leftPos, this.topPos);
    }
}
