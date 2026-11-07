package com.mrcrayfish.furniture.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.furniture.block.DoorMatBlock;
import com.mrcrayfish.furniture.legacy.shared.SharedContent;
import com.mrcrayfish.furniture.tileentity.DoorMatBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class DoorMatBlockEntityRenderer implements BlockEntityRenderer<DoorMatBlockEntity>
{
    /* The colours whose wool gives more contrast with white text than with black text */
    private static final Set<DyeColor> DARK_COLORS = EnumSet.of(DyeColor.GRAY, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BROWN, DyeColor.GREEN, DyeColor.RED, DyeColor.BLACK);

    private final Font font;

    public DoorMatBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this.font = context.getFont();
    }

    private static boolean isDark(BlockState state)
    {
        for(DyeColor color : DARK_COLORS)
        {
            if(state.is(SharedContent.DOOR_MATS.get(color).get()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(DoorMatBlockEntity tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay)
    {
        if(tileEntity.getMessage() != null)
        {
            BlockState state = tileEntity.getBlockState();
            if(state.getBlock() instanceof DoorMatBlock)
            {
                poseStack.pushPose(); //Push

                poseStack.translate(0.5, 0.0626, 0.5);

                int rotation = state.getValue(DoorMatBlock.DIRECTION).get2DDataValue();
                poseStack.mulPose(Axis.YP.rotationDegrees(-90F * rotation + 180F));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90F));

                poseStack.scale(1.0F, -1.0F, -1.0F);
                poseStack.scale(0.0125F, 0.0125F, 0.0125F);

                List<FormattedCharSequence> lines = this.font.split(FormattedText.of(tileEntity.getMessage()), 60);
                poseStack.translate(0.0, -(lines.size() * this.font.lineHeight - 1.0) / 2.0, 0);

                int color = isDark(state) ? 0xFFFFFF : 0x000000;

                //TODO test
                for(int j = 0; j < lines.size(); j++)
                {
                    poseStack.pushPose();
                    poseStack.translate(-this.font.width(lines.get(j)) / 2.0, (j * this.font.lineHeight), 0.0);
                    this.font.drawInBatch(lines.get(j), 0, 0, color, false, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, light);
                    poseStack.popPose();
                }
                poseStack.popPose(); //Pop
            }
        }
    }
}
