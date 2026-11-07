package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingMachineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class WashingMachineRenderer implements BlockEntityRenderer<WashingMachineBlockEntity>
{
    private static final double[] OFFSETS = {-0.2, -0.1, 0.0, 0.1};

    public WashingMachineRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(WashingMachineBlockEntity machine, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        int metadata = machine.getBlockState().getValue(FurnitureHorizontalBlock.DIRECTION).get2DDataValue();
        float progress = machine.isWashing() ? machine.getProgress() + partialTicks : machine.getProgress();

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(metadata * -90F));
        for(int i = 0; i < 4; i++)
        {
            /* The rotations add up from one slot to the next */
            poseStack.mulPose(Axis.ZP.rotationDegrees(progress * 5F));
            KitchenRenderUtil.renderItem(machine.getItem(i), 0.0, -0.35, OFFSETS[i], poseStack, source, light, machine.getLevel());
        }
        poseStack.popPose();
    }
}
