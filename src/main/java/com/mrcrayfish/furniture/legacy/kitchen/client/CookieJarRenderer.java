package com.mrcrayfish.furniture.legacy.kitchen.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.furniture.legacy.kitchen.block.CookieJarBlock;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.CookieJarBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CookieJarRenderer implements BlockEntityRenderer<CookieJarBlockEntity>
{
    private final ItemStack cookie = new ItemStack(Items.COOKIE);

    public CookieJarRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CookieJarBlockEntity jar, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        int count = jar.getBlockState().getValue(CookieJarBlock.COOKIE_COUNT);
        poseStack.pushPose();
        poseStack.translate(0.5, 0.05, 0.18);
        poseStack.mulPose(MicrowaveRenderer.FLAT.rotationDegrees(180F));
        poseStack.translate(0.0, -0.1, 0.0);
        poseStack.scale(0.9F, 0.9F, 0.9F);
        for(int i = 0; i < count; i++)
        {
            KitchenRenderUtil.renderItem(this.cookie, 0.0, 0.0, 0.1 * i, poseStack, source, light, jar.getLevel());
        }
        poseStack.popPose();
    }
}
