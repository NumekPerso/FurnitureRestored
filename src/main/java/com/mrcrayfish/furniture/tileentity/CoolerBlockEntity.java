package com.mrcrayfish.furniture.tileentity;

import com.mrcrayfish.furniture.block.CoolerBlock;
import com.mrcrayfish.furniture.core.ModBlockEntities;
import com.mrcrayfish.furniture.core.ModSounds;
import com.mrcrayfish.furniture.legacy.shared.FilteredChestMenu;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class CoolerBlockEntity extends BasicLootBlockEntity
{
    protected CoolerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public CoolerBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.COOLER.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 9;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.cooler");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return FilteredChestMenu.cooler(windowId, playerInventory, this);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        return FilteredChestMenu.isCoolerItem(stack);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction)
    {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public void onOpen(Level level, BlockPos pos, BlockState state)
    {
        this.playLidSound(state, ModSounds.BLOCK_CABINET_OPEN.get());
        this.setLidState(state, true);
    }

    @Override
    public void onClose(Level level, BlockPos pos, BlockState state)
    {
        this.playLidSound(state, ModSounds.BLOCK_CABINET_CLOSE.get());
        this.setLidState(state, false);
    }

    private void playLidSound(BlockState state, SoundEvent event)
    {
        Vec3i directionVec = state.getValue(CoolerBlock.DIRECTION).getNormal();
        double x = this.worldPosition.getX() + 0.5D + directionVec.getX() / 2.0D;
        double y = this.worldPosition.getY() + 0.5D + directionVec.getY() / 2.0D;
        double z = this.worldPosition.getZ() + 0.5D + directionVec.getZ() / 2.0D;
        Level level = this.getLevel();
        if(level != null)
        {
            level.playSound(null, x, y, z, event, SoundSource.BLOCKS, 0.75F, level.random.nextFloat() * 0.1F + 0.7F);
        }
    }

    private void setLidState(BlockState state, boolean open)
    {
        Level level = this.getLevel();
        if(level != null)
        {
            level.setBlock(this.getBlockPos(), state.setValue(CoolerBlock.OPEN, open), 3);
        }
    }
}
