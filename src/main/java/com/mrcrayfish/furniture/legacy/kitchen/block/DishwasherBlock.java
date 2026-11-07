package com.mrcrayfish.furniture.legacy.kitchen.block;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.DishwasherBlockEntity;
import com.mrcrayfish.furniture.legacy.kitchen.blockentity.WashingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class DishwasherBlock extends MenuApplianceBlock
{
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    public DishwasherBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new DishwasherBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return level.isClientSide() ? createTickerHelper(type, KitchenContent.DISHWASHER_ENTITY.get(), WashingBlockEntity::clientTick) : createTickerHelper(type, KitchenContent.DISHWASHER_ENTITY.get(), WashingBlockEntity::serverTick);
    }
}
