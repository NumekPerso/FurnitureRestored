package com.mrcrayfish.furniture.legacy.electronics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.block.FurnitureHorizontalBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

/**
 * Draws the time on the screen of the clock.
 */
public class DigitalClockRenderer implements BlockEntityRenderer<DigitalClockBlockEntity>
{
    private final Font font;

    public DigitalClockRenderer(BlockEntityRendererProvider.Context context)
    {
        this.font = context.getFont();
    }

    @Override
    public void render(DigitalClockBlockEntity clock, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        Level level = clock.getLevel();
        if(level == null)
            return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(clock.getBlockState().getValue(FurnitureHorizontalBlock.DIRECTION).get2DDataValue() * -90F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        poseStack.translate(0.0675, 0.005, -0.032);
        poseStack.translate(-4.2 * 0.0625, -5.0 * 0.0625, 1.55 * 0.0625);
        poseStack.scale(0.015625F, -0.015625F, 0.015625F);
        this.font.drawInBatch(formatTime(level.getDayTime()), 0, 0, 0xFF000000 | getTextColor(clock.getTextColor()), false, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    /** Day time 0 is 06:00. */
    private static String formatTime(long ticks)
    {
        int hours = (int) ((ticks / 1000 + 6) % 24);
        int minutes = (int) ((ticks % 1000) * 60 / 1000);
        return String.format("%02d:%02d", hours, minutes);
    }

    /** The text uses chat colors. */
    private static int getTextColor(DyeColor color)
    {
        ChatFormatting formatting = switch(color)
        {
            case ORANGE, BROWN -> ChatFormatting.GOLD;
            case MAGENTA, PINK -> ChatFormatting.LIGHT_PURPLE;
            case LIGHT_BLUE -> ChatFormatting.BLUE;
            case YELLOW -> ChatFormatting.YELLOW;
            case LIME -> ChatFormatting.GREEN;
            case GRAY -> ChatFormatting.DARK_GRAY;
            case LIGHT_GRAY -> ChatFormatting.GRAY;
            case CYAN -> ChatFormatting.DARK_AQUA;
            case PURPLE -> ChatFormatting.DARK_PURPLE;
            case BLUE -> ChatFormatting.DARK_BLUE;
            case GREEN -> ChatFormatting.DARK_GREEN;
            case RED -> ChatFormatting.DARK_RED;
            case BLACK -> ChatFormatting.BLACK;
            default -> ChatFormatting.WHITE;
        };
        return formatting.getColor();
    }
}
