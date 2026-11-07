package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * A running shower head: water, its sound, and it washes the players standing just below it.
 */
public class ShowerHeadBlockEntity extends BlockEntity
{
    private int timer = 20;

    public ShowerHeadBlockEntity(BlockPos pos, BlockState state)
    {
        super(BathroomContent.SHOWER_HEAD_BLOCK_ENTITY.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ShowerHeadBlockEntity blockEntity)
    {
        double x = pos.getX() + 0.35 + level.random.nextDouble() / 3.0;
        double z = pos.getZ() + 0.35 + level.random.nextDouble() / 3.0;
        level.addParticle(ParticleTypes.FALLING_WATER, x, pos.getY() + 0.065, z, 0.0, 0.0, 0.0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ShowerHeadBlockEntity blockEntity)
    {
        if(blockEntity.timer % 5 == 0)
        {
            AABB below = new AABB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ() + 1);
            for(Player player : level.getEntitiesOfClass(Player.class, below))
            {
                wash(player);
            }
        }
        if(blockEntity.timer >= 20)
        {
            level.playSound(null, pos, LegacySounds.SHOWER.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
            blockEntity.timer = 0;
        }
        blockEntity.timer++;
    }

    private static void wash(Player player)
    {
        player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        for(ItemStack armor : player.getInventory().armor)
        {
            if(armor.getItem() instanceof DyeableLeatherItem dyeable && dyeable.hasCustomColor(armor))
            {
                dyeable.clearColor(armor);
            }
        }
        if(player.isOnFire())
        {
            player.clearFire();
        }
    }
}
