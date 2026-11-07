package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.legacy.kitchen.menu.OvenMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OvenScreen extends KitchenScreen<OvenMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/legacy/oven.png");

    public OvenScreen(OvenMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, TEXTURE, 176, 228);
        this.titleLabelX = 70;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderDetails(GuiGraphics graphics, int left, int top)
    {
        int arrow = this.menu.getProgressScaled(24);
        graphics.blit(TEXTURE, left + 75, top + 20, 176, 14, arrow + 1, 16);
        if(this.menu.isCooking())
        {
            int progress = this.menu.getProgressScaled(14);
            graphics.blit(TEXTURE, left + 54 + this.menu.getCookingItem() * 18, top + 55 + (14 - progress), 176, 14 - progress, 14, progress);
        }
        this.renderStatus(graphics, left, top, this.menu.isCooking());
    }

    @Override
    protected void renderIndicatorTooltips(GuiGraphics graphics, int mouseX, int mouseY)
    {
        this.renderStatusTooltip(graphics, this.menu.isCooking(), mouseX, mouseY);
    }
}
