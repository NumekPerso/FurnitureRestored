package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.legacy.kitchen.menu.KitchenCabinetMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KitchenCabinetScreen extends KitchenScreen<KitchenCabinetMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/legacy/kitchen_cabinet.png");

    public KitchenCabinetScreen(KitchenCabinetMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, TEXTURE, 176, 167);
        this.titleLabelX = this.imageWidth / 2 - 44;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderDetails(GuiGraphics graphics, int left, int top) {}

    @Override
    protected void renderIndicatorTooltips(GuiGraphics graphics, int mouseX, int mouseY) {}
}
