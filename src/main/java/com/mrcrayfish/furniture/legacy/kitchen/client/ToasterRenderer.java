package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.ToasterBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ToasterRenderer implements BlockEntityRenderer<ToasterBlockEntity>
{
    public ToasterRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ToasterBlockEntity toaster, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        int metadata = toaster.getBlockState().getValue(FurnitureHorizontalBlock.DIRECTION).get2DDataValue();
        for(int i = 0; i < 2; i++)
        {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.05, 0.3);

            double xOffset = 0.0;
            double zOffset = 0.0;
            switch(metadata)
            {
                case 0 -> zOffset += i == 1 ? 0.27 : 0.14;
                case 1 ->
                {
                    xOffset += 0.2;
                    zOffset += i == 1 ? 0.07 : -0.06;
                    poseStack.mulPose(Axis.YP.rotationDegrees(270F));
                }
                case 2 ->
                {
                    zOffset += i == 1 ? -0.13 : -0.26;
                    poseStack.mulPose(Axis.YP.rotationDegrees(180F));
                }
                default ->
                {
                    xOffset -= 0.2;
                    zOffset += i == 1 ? 0.07 : -0.06;
                    poseStack.mulPose(Axis.YP.rotationDegrees(90F));
                }
            }

            double yOffset = toaster.isToasting() ? -0.25 : -0.15;
            KitchenRenderUtil.renderItem(toaster.getSlice(i), xOffset, yOffset, zOffset, poseStack, source, light, toaster.getLevel());
            poseStack.popPose();
        }
    }
}
