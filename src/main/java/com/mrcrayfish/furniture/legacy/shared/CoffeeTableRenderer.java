package com.mrcrayfish.furniture.legacy.shared;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.block.CoffeeTableBlock;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws the item lying on a coffee table.
 */
public class CoffeeTableRenderer implements BlockEntityRenderer<CoffeeTableBlockEntity>
{
    private final ItemRenderer itemRenderer;

    public CoffeeTableRenderer(BlockEntityRendererProvider.Context context)
    {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CoffeeTableBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        ItemStack stack = entity.getItem();
        if(stack.isEmpty())
            return;

        BlockState state = entity.getBlockState();
        boolean tall = state.getBlock() instanceof CoffeeTableBlock && state.getValue(CoffeeTableBlock.TALL);
        Level level = entity.getLevel();
        int itemLight = tall && level != null ? LevelRenderer.getLightColor(level, entity.getBlockPos().above()) : light;

        poseStack.pushPose();
        poseStack.translate(0.5, (tall ? 1.0 : 0.5) + 0.03125, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90F * entity.getRotation()));
        poseStack.mulPose(Axis.XP.rotationDegrees(90F));
        poseStack.scale(0.75F, 0.75F, 0.75F);
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, itemLight, overlay, poseStack, source, level, (int) entity.getBlockPos().asLong());
        poseStack.popPose();
    }
}
