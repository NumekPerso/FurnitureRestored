package com.mrcrayfish.furniture.legacy.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TreeBlockEntityRenderer implements BlockEntityRenderer<TreeBlockEntity>
{
    /* Height of a resting item entity with the default ground transform */
    private static final float ITEM_HEIGHT = 0.225F;

    public TreeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TreeBlockEntity tree, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        boolean bottom = tree.getBlockState().getValue(TreeBlock.PART) == BlockPart.BOTTOM;
        float spread = bottom ? 0.45F : 0.3F;
        poseStack.pushPose();
        poseStack.translate(0.5F, bottom ? 0.5F : 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90F));
        for(int i = 0; i < TreeBlockEntity.SIDES; i++)
        {
            ItemStack stack = tree.getOrnaments().get(i);
            if(stack.isEmpty())
                continue;
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-90F * i));
            poseStack.translate(spread, 0.0F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-15F));
            poseStack.scale(0.9F, 0.9F, 0.9F);
            poseStack.translate(0.0F, ITEM_HEIGHT, 0.0F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, poseStack, source, tree.getLevel(), i);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
