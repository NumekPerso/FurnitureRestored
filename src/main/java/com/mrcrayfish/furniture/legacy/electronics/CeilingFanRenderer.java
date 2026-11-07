package com.mrcrayfish.furniture.legacy.electronics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.legacy.LegacyItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Draws the blades, which are the model of an item.
 */
public class CeilingFanRenderer implements BlockEntityRenderer<CeilingFanBlockEntity>
{
    private final ItemStack blades = new ItemStack(LegacyItems.CEILING_FAN_FANS.get());

    public CeilingFanRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CeilingFanBlockEntity fan, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(1.35F, 1.0F, 1.35F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-fan.getRotation(partialTicks)));
        Minecraft.getInstance().getItemRenderer().renderStatic(this.blades, ItemDisplayContext.NONE, light, overlay, poseStack, source, fan.getLevel(), (int) fan.getBlockPos().asLong());
        poseStack.popPose();
    }

    @Override
    public int getViewDistance()
    {
        return 128;
    }
}
