package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.menu.WashingMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WashingMachineScreen extends KitchenScreen<WashingMachineMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/legacy/washingmachine.png");

    public WashingMachineScreen(WashingMachineMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, TEXTURE, 176, 228);
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 135;
    }

    @Override
    protected void init()
    {
        super.init();
        this.addStartButton(53, 5, this.menu::isWashing);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, LABEL_COLOR, false);
    }

    @Override
    protected void renderDetails(GuiGraphics graphics, int left, int top)
    {
        if(this.menu.isWashing())
        {
            int cycle = this.menu.isSuperMode() ? 20 : 50;
            int percent = (this.menu.getProgress() % cycle) * 73 / cycle;
            graphics.blit(TEXTURE, left + 34, top + 104 - percent, 176, 73 - percent, 16, percent);
        }

        int percent = this.menu.getTimeRemaining() * 73 / WashingBlockEntity.SOAP_TIME;
        int soap = this.menu.isSuperMode() ? 20 : 0;
        graphics.blit(TEXTURE, left + 129, top + 103 - percent, 192 + soap, 0, 10, percent);
        graphics.blit(TEXTURE, left + 129, top + 30, 202, 0, 10, 73);

        this.renderStatus(graphics, left, top, this.menu.isWashing());
    }

    @Override
    protected void renderIndicatorTooltips(GuiGraphics graphics, int mouseX, int mouseY)
    {
        this.renderStatusTooltip(graphics, this.menu.isWashing(), mouseX, mouseY);
        if(this.isHovering(129, 30, 10, 73, mouseX, mouseY))
        {
            graphics.renderTooltip(this.font, Component.translatable("furniture_restored.gui.soap_level", this.menu.getTimeRemaining()), mouseX, mouseY);
        }
    }
}
