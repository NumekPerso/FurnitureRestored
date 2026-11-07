package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.core.ModSounds;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.menu.KitchenCabinetMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KitchenCabinetBlockEntity extends KitchenContainerBlockEntity
{
    public KitchenCabinetBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.KITCHEN_CABINET_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 15;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.kitchen_cabinet");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new KitchenCabinetMenu(windowId, playerInventory, this);
    }

    /* Nothing draws the content of a cabinet: no need to send it to the clients */
    @Override
    protected void sync() {}

    @Override
    public void onOpen(Level level, BlockPos pos, BlockState state)
    {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSounds.BLOCK_CABINET_OPEN.get(), SoundSource.BLOCKS, 0.75F, 0.9F);
    }

    @Override
    public void onClose(Level level, BlockPos pos, BlockState state)
    {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSounds.BLOCK_CABINET_CLOSE.get(), SoundSource.BLOCKS, 0.75F, 0.8F);
    }
}
