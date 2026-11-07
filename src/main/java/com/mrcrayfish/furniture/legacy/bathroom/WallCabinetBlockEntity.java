package com.mrcrayfish.furniture.legacy.bathroom;

import com.mrcrayfish.furniture.core.ModSounds;
import com.mrcrayfish.furniture.tileentity.BasicLootBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Nine slots that only accept potions, also for hoppers.
 */
public class WallCabinetBlockEntity extends BasicLootBlockEntity
{
    public WallCabinetBlockEntity(BlockPos pos, BlockState state)
    {
        super(BathroomContent.WALL_CABINET_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 9;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.wall_cabinet");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new WallCabinetMenu(windowId, playerInventory, this);
    }

    @Override
    public boolean isMatchingContainerMenu(AbstractContainerMenu menu)
    {
        return menu instanceof WallCabinetMenu cabinetMenu && cabinetMenu.getContainer() == this;
    }

    /** Also used by the Forge item handler, which ignores the sided methods below. */
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof PotionItem;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction)
    {
        return stack.getItem() instanceof PotionItem;
    }

    @Override
    public void onOpen(Level level, BlockPos pos, BlockState state)
    {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSounds.BLOCK_CABINET_OPEN.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
    }

    @Override
    public void onClose(Level level, BlockPos pos, BlockState state)
    {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSounds.BLOCK_CABINET_CLOSE.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
    }
}
