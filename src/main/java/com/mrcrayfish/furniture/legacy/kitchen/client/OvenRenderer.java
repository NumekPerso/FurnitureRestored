package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.OvenBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class OvenRenderer implements BlockEntityRenderer<OvenBlockEntity>
{
    public OvenRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(OvenBlockEntity oven, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        poseStack.pushPose();
        poseStack.translate(0.3, 0.52, 0.5);
        poseStack.scale(0.66F, 0.66F, 0.66F);
        for(int i = 0; i < oven.getContainerSize(); i++)
        {
            double height = (i / 4) * -0.25;
            double xOffset = i % 2 != 0 ? 0.5 : 0.0;
            double zOffset = i >= (i / 4) + 2 && i <= (i / 4) + 4 ? 0.5 : 0.0;
            poseStack.pushPose();
            poseStack.translate(xOffset, height, zOffset);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
            poseStack.translate(0.0, -0.2, 0.0);
            KitchenRenderUtil.renderItem(oven.getItem(i), 0.0, 0.0, 0.0, poseStack, source, light, oven.getLevel());
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
