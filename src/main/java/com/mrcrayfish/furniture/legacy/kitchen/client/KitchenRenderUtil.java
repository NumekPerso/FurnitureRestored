package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Drawing helpers of the kitchen renderers.
 */
public class KitchenRenderUtil
{
    /** Draws the item like a resting item entity of age 0. */
    public static void renderItem(ItemStack stack, double x, double y, double z, PoseStack poseStack, MultiBufferSource source, int light, Level level)
    {
        if(stack.isEmpty())
            return;
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = renderer.getModel(stack, level, null, 0);
        float scale = model.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.pushPose();
        poseStack.translate(x, y + 0.1F + 0.25F * scale, z);
        renderer.render(stack, ItemDisplayContext.GROUND, false, poseStack, source, light, OverlayTexture.NO_OVERLAY, model);
        poseStack.popPose();
    }

    /** Plain colored box without texture nor lighting, for the liquids. */
    public static void renderCuboid(PoseStack poseStack, MultiBufferSource source, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha)
    {
        LevelRenderer.addChainedFilledBoxVertices(poseStack, source.getBuffer(RenderType.debugFilledBox()), x1, y1, z1, x2, y2, z2, red, green, blue, alpha);
    }
}
