package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.tileentity.BasicLootBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BinBlockEntity extends BasicLootBlockEntity
{
    public BinBlockEntity(BlockPos pos, BlockState state)
    {
        super(BathroomContent.BIN_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 12;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.bin");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new BinMenu(windowId, playerInventory, this);
    }

    @Override
    public boolean isMatchingContainerMenu(AbstractContainerMenu menu)
    {
        return menu instanceof BinMenu binMenu && binMenu.getContainer() == this;
    }

    @Override
    public void onOpen(Level level, BlockPos pos, BlockState state)
    {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, LegacySounds.BIN_OPEN.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
    }

    @Override
    public void onClose(Level level, BlockPos pos, BlockState state)
    {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, LegacySounds.BIN_CLOSE.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
    }
}
