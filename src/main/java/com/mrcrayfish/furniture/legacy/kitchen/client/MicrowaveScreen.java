package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.MicrowaveBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.menu.MicrowaveMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MicrowaveScreen extends KitchenScreen<MicrowaveMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/legacy/microwave.png");

    public MicrowaveScreen(MicrowaveMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, TEXTURE, 176, 174);
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 80;
    }

    @Override
    protected void init()
    {
        super.init();
        this.addStartButton(118, 39, this.menu::isCooking);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, LABEL_COLOR, false);
    }

    @Override
    protected void renderDetails(GuiGraphics graphics, int left, int top)
    {
        int percent = this.menu.getProgress() * 27 / MicrowaveBlockEntity.COOK_TIME;
        graphics.blit(TEXTURE, left + 120, top + 26, 176, 0, percent, 5);
    }

    @Override
    protected void renderIndicatorTooltips(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if(this.isHovering(120, 26, 27, 5, mouseX, mouseY))
        {
            int percent = this.menu.getProgress() * 100 / MicrowaveBlockEntity.COOK_TIME;
            graphics.renderTooltip(this.font, Component.translatable("furniture_restored.gui.progress", percent, "100%"), mouseX, mouseY);
        }
    }
}
