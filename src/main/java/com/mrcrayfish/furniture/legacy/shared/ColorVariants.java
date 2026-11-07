package com.mrcrayfish.furniture.legacy.shared;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * In place dyeing of the base blocks named color_type. The other variants of a type (wood, rainbow) are left alone.
 */
public class ColorVariants
{
    public static boolean tryRecolor(String type, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand)
    {
        if(!isColored(type, state.getBlock()))
            return false;
        return LegacyUtil.tryRecolor(state, level, pos, player, hand, color -> get(type, color));
    }

    private static boolean isColored(String type, Block block)
    {
        for(DyeColor color : DyeColor.values())
        {
            if(block == get(type, color))
            {
                return true;
            }
        }
        return false;
    }

    private static Block get(String type, DyeColor color)
    {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, color.getName() + "_" + type);
        return ForgeRegistries.BLOCKS.containsKey(id) ? ForgeRegistries.BLOCKS.getValue(id) : null;
    }
}
