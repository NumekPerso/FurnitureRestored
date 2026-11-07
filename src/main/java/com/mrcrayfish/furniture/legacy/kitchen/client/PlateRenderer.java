package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.PlateBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PlateRenderer implements BlockEntityRenderer<PlateBlockEntity>
{
    public PlateRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(PlateBlockEntity plate, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        if(plate.getFood().isEmpty())
            return;

        double xOffset = 0.0;
        double zOffset = 0.0;
        switch(plate.getRotation())
        {
            case 0 -> zOffset -= 0.15;
            case 1 ->
            {
                xOffset += 0.35;
                zOffset += 0.2;
            }
            case 2 -> zOffset += 0.55;
            default ->
            {
                xOffset -= 0.35;
                zOffset += 0.2;
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.5 + xOffset, 0.05, 0.3 + zOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(plate.getRotation() * -90F));
        poseStack.mulPose(MicrowaveRenderer.FLAT.rotationDegrees(180F));
        poseStack.translate(0.0, -0.1, 0.0);
        KitchenRenderUtil.renderItem(plate.getFood(), 0.0, 0.0, 0.075, poseStack, source, light, plate.getLevel());
        poseStack.popPose();
    }
}
