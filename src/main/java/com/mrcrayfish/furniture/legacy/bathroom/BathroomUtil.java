package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Behaviours shared by the blocks of the bathroom module.
 */
final class BathroomUtil
{
    private BathroomUtil() {}

    /** The water source two blocks below is what the taps draw from and use up. */
    static boolean hasWaterSource(Level level, BlockPos pos)
    {
        return level.getBlockState(pos.below(2)) == Blocks.WATER.defaultBlockState();
    }

    /** Level#removeBlock would put the water back: it leaves the fluid state of the position. */
    static void consumeWaterSource(Level level, BlockPos pos)
    {
        level.setBlock(pos.below(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    static boolean isWaterPotion(ItemStack stack)
    {
        return stack.getItem() instanceof PotionItem && PotionUtils.getPotion(stack) == Potions.WATER;
    }

    static ItemStack waterBottle()
    {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
    }

    /** What is left of a used water potion: a bottle for a drinkable one, nothing for a splash one. */
    static ItemStack emptiedPotion(ItemStack potion)
    {
        return potion.is(Items.POTION) ? new ItemStack(Items.GLASS_BOTTLE) : ItemStack.EMPTY;
    }

    /** Replaces one item of the held stack. Creative players keep their stack unchanged. */
    static void exchange(Player player, InteractionHand hand, ItemStack held, ItemStack result)
    {
        if(player.getAbilities().instabuild)
            return;
        if(held.getCount() > 1)
        {
            held.shrink(1);
            if(!player.getInventory().add(result))
            {
                player.drop(result, false);
            }
        }
        else
        {
            player.setItemInHand(hand, result);
        }
    }

    static void triggerPlaced(LivingEntity placer)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_BATHROOM_FURNITURE.trigger(player);
        }
    }
}
