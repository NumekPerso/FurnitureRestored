package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.BlenderBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class BlenderRenderer implements BlockEntityRenderer<BlenderBlockEntity>
{
    public BlenderRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(BlenderBlockEntity blender, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        NonNullList<ItemStack> ingredients = blender.getIngredients();
        float progress = blender.isBlending() ? blender.getProgress() + partialTicks : blender.getProgress();

        poseStack.pushPose();
        poseStack.translate(0.5, 0.2, 0.5);
        poseStack.scale(0.65F, 0.65F, 0.65F);
        for(int i = 0; i < ingredients.size(); i++)
        {
            if(!ingredients.get(i).isEmpty())
            {
                /* The rotations add up from one ingredient to the next */
                poseStack.mulPose(Axis.YP.rotationDegrees(i * -90F));
                poseStack.mulPose(Axis.YP.rotationDegrees(progress * 18F));
                KitchenRenderUtil.renderItem(ingredients.get(i), 0.0, 0.2, 0.0, poseStack, source, light, blender.getLevel());
            }
        }
        poseStack.popPose();

        if(blender.isBlending() || blender.hasDrink())
        {
            float alpha = blender.isBlending() ? Math.min(progress / BlenderBlockEntity.BLEND_TIME, 1.0F) : 1.0F;
            float height = blender.isBlending() ? 0.8F : 0.275F + 0.525F * ((float) blender.getDrinkCount() / BlenderBlockEntity.DRINK_COUNT);
            poseStack.pushPose();
            poseStack.translate(0.5, 0.05, 0.5);
            KitchenRenderUtil.renderCuboid(poseStack, source, -0.2F, 0.275F, -0.2F, 0.2F, height, 0.2F, blender.getRed() / 255F, blender.getGreen() / 255F, blender.getBlue() / 255F, alpha);
            poseStack.popPose();
        }
    }
}
