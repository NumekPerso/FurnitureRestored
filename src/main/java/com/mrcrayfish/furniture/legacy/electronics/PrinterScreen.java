package com.mrcrayfish.furniture.legacy.electronics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.furniture.Reference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PrinterScreen extends AbstractContainerScreen<PrinterMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/legacy/printer.png");

    public PrinterScreen(PrinterMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 185;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        if(this.isHovering(73, 30, 5, 18, mouseX, mouseY))
        {
            graphics.renderTooltip(this.font, Component.translatable("furniture_restored.gui.ink_level", this.menu.getInkTime(), this.menu.getInkTotal()), mouseX, mouseY);
        }

        if(this.isHovering(79, 30, 5, 18, mouseX, mouseY))
        {
            graphics.renderTooltip(this.font, Component.translatable("furniture_restored.gui.progress", this.menu.getPrintTime(), this.menu.getPrintTotal()), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight + 21);
        if(this.menu.isPrinting())
        {
            int total = this.menu.getPrintTotal();
            int progress = total != 0 && this.menu.getPrintTime() != 0 ? this.menu.getPrintTime() * 16 / total : 0;
            graphics.blit(TEXTURE, this.leftPos + 80, this.topPos + 45 - progress, 176, 15 - progress, 3, progress + 1);

            int inkTotal = this.menu.getInkTotal() != 0 ? this.menu.getInkTotal() : 200;
            int ink = this.menu.getInkTime() * 16 / inkTotal;
            graphics.blit(TEXTURE, this.leftPos + 74, this.topPos + 46 - ink, 179, 16 - ink, 3, ink);
        }
    }
}
