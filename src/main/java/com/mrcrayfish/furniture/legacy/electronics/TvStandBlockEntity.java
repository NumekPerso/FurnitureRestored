package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.core.ModSounds;
import com.mrcrayfish.furniture.tileentity.BasicLootBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TvStandBlockEntity extends BasicLootBlockEntity
{
    public TvStandBlockEntity(BlockPos pos, BlockState state)
    {
        super(ElectronicsContent.TV_STAND_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 9;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.tv_stand");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new ChestMenu(MenuType.GENERIC_9x1, windowId, playerInventory, this, 1);
    }

    @Override
    public void onOpen(Level level, BlockPos pos, BlockState state)
    {
        level.playSound(null, pos, ModSounds.BLOCK_CABINET_OPEN.get(), SoundSource.BLOCKS, 0.75F, 0.9F);
    }

    @Override
    public void onClose(Level level, BlockPos pos, BlockState state)
    {
        level.playSound(null, pos, ModSounds.BLOCK_CABINET_CLOSE.get(), SoundSource.BLOCKS, 0.75F, 0.8F);
    }
}
