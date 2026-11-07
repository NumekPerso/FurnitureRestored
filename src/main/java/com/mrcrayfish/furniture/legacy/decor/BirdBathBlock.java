package com.mrcrayfish.furniture.legacy.decor;

import com.mrcrayfish.furniture.block.FurnitureBlock;
import com.mrcrayfish.furniture.legacy.LegacyTriggers;
import com.mrcrayfish.furniture.legacy.LegacyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BirdBathBlock extends FurnitureBlock
{
    private static final VoxelShape SHAPE = LegacyUtil.union(
            Block.box(5, 1.75, 5, 11, 12, 11),
            Block.box(4, 0, 4, 12, 1.5, 12),
            Block.box(1, 11.25, 1, 15, 12.75, 15),
            Block.box(0, 12.75, 0, 16, 14.5, 16));

    public BirdBathBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(placer instanceof Player player)
        {
            LegacyTriggers.PLACE_OUTDOOR_FURNITURE.trigger(player);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
