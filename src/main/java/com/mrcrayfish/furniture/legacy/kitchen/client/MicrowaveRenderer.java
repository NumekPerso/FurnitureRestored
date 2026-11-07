package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.MicrowaveBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Vector3f;

public class MicrowaveRenderer implements BlockEntityRenderer<MicrowaveBlockEntity>
{
    /** Lays an item flat. */
    static final Axis FLAT = Axis.of(new Vector3f(0.0F, 1.0F, 1.0F).normalize());

    public MicrowaveRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(MicrowaveBlockEntity microwave, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        int metadata = microwave.getBlockState().getValue(FurnitureHorizontalBlock.DIRECTION).get2DDataValue();

        double xOffset = 0.0;
        double zOffset = 0.0;
        switch(metadata)
        {
            case 0 -> xOffset += 0.1;
            case 1 ->
            {
                xOffset += 0.2;
                zOffset += 0.3;
            }
            case 2 ->
            {
                xOffset -= 0.1;
                zOffset += 0.4;
            }
            default ->
            {
                xOffset -= 0.2;
                zOffset += 0.1;
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.5 + xOffset, 0.075, 0.3 + zOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(metadata * -90F));
        poseStack.mulPose(FLAT.rotationDegrees(180F));
        poseStack.translate(0.0, -0.3, 0.0);
        KitchenRenderUtil.renderItem(microwave.getItem(0), 0.0, 0.0, 0.075, poseStack, source, light, microwave.getLevel());
        poseStack.popPose();
    }
}
