package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.ChoppingBoardBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ChoppingBoardRenderer implements BlockEntityRenderer<ChoppingBoardBlockEntity>
{
    public ChoppingBoardRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ChoppingBoardBlockEntity board, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        if(board.getFood().isEmpty())
            return;

        int metadata = board.getBlockState().getValue(FurnitureHorizontalBlock.DIRECTION).get2DDataValue();
        double xOffset = 0.0;
        double zOffset = 0.0;
        switch(metadata)
        {
            case 0 -> zOffset -= 0.1;
            case 1 ->
            {
                xOffset += 0.3;
                zOffset += 0.2;
            }
            case 2 -> zOffset += 0.5;
            default ->
            {
                xOffset -= 0.3;
                zOffset += 0.2;
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.5 + xOffset, 0.02, 0.3 + zOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(metadata * -90F));
        poseStack.mulPose(MicrowaveRenderer.FLAT.rotationDegrees(180F));
        poseStack.translate(0.0, -0.15, 0.0);
        KitchenRenderUtil.renderItem(board.getFood(), 0.0, 0.0, 0.075, poseStack, source, light, board.getLevel());
        poseStack.popPose();
    }
}
