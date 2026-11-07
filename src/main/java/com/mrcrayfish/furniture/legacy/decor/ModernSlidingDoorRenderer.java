package com.mrcrayfish.furniture.legacy.decor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/**
 * Draws the model of the door half, moved along the slide.
 */
public class ModernSlidingDoorRenderer implements BlockEntityRenderer<ModernSlidingDoorBlockEntity>
{
    public ModernSlidingDoorRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ModernSlidingDoorBlockEntity door, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        BlockState state = door.getBlockState();
        if(!(state.getBlock() instanceof ModernSlidingDoorBlock))
        {
            return;
        }
        float slide = door.getSlideProgress(partialTicks);
        Direction direction = state.getValue(ModernSlidingDoorBlock.DIRECTION).getCounterClockWise();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        poseStack.pushPose();
        poseStack.translate(slide * direction.getStepX(), 0.0, slide * direction.getStepZ());
        dispatcher.getModelRenderer().renderModel(poseStack.last(), source.getBuffer(Sheets.cutoutBlockSheet()), state, dispatcher.getBlockModel(state), 1.0F, 1.0F, 1.0F, light, overlay, ModelData.EMPTY, RenderType.cutout());
        poseStack.popPose();
    }

    @Override
    public int getViewDistance()
    {
        return 128;
    }
}
