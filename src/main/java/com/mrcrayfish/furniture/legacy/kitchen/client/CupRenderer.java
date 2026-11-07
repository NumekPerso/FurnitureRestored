package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.CupBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CupRenderer implements BlockEntityRenderer<CupBlockEntity>
{
    public CupRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CupBlockEntity cup, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        if(!cup.getDrink().isEmpty())
        {
            int color = cup.getColor();
            float red = (color >> 16 & 255) / 255F;
            float green = (color >> 8 & 255) / 255F;
            float blue = (color & 255) / 255F;
            poseStack.pushPose();
            poseStack.translate(0.5, 0.0, 0.5);
            KitchenRenderUtil.renderCuboid(poseStack, source, -0.124F, 0.5F * 0.0625F, -0.124F, 0.124F, 0.4F, 0.124F, red, green, blue, 1.0F);
            poseStack.popPose();
        }
    }
}
