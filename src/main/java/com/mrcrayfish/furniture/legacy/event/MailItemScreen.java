package com.mrcrayfish.furniture.legacy.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.furniture.network.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Screen of a present, envelope or package. The button only shows on an item not signed yet.
 */
public class MailItemScreen extends AbstractContainerScreen<MailItemMenu>
{
    private static final int TITLE_COLOR = 9999999;
    private static final int INVENTORY_COLOR = 4210752;

    public MailItemScreen(MailItemMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init()
    {
        super.init();
        ItemStack held = this.minecraft.player.getMainHandItem();
        if(held.getItem() instanceof IAuthored && !MailStorage.isSigned(held))
        {
            MailLayout layout = this.menu.getLayout();
            this.addRenderableWidget(Button.builder(Component.translatable(layout.buttonKey()), button -> {
                PacketHandler.getPlayChannel().sendToServer(new C2SMessageSignItem());
            }).pos(this.width / 2 + 40, this.height / 2 + layout.buttonY()).size(40, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.drawString(this.font, this.title, (this.imageWidth - this.font.width(this.title)) / 2, 5, TITLE_COLOR, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, INVENTORY_COLOR, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(this.menu.getLayout().texture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
