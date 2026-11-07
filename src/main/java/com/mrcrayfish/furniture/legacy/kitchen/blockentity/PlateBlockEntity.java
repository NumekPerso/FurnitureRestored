package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class PlateBlockEntity extends SyncedBlockEntity
{
    private ItemStack food = ItemStack.EMPTY;
    private int rotation;

    public PlateBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.PLATE_ENTITY.get(), pos, state);
    }

    public void setFood(ItemStack food, int rotation)
    {
        this.food = food;
        this.rotation = rotation;
        this.sync();
    }

    public ItemStack getFood()
    {
        return this.food;
    }

    /** Horizontal index of the player who put the food: 0 south, 1 west, 2 north and 3 east. */
    public int getRotation()
    {
        return this.rotation;
    }

    @Override
    public NonNullList<ItemStack> getDropStacks()
    {
        return NonNullList.of(ItemStack.EMPTY, this.food);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.food = tag.contains("Item", Tag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound("Item")) : ItemStack.EMPTY;
        this.rotation = tag.getInt("Rotation");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        if(!this.food.isEmpty())
        {
            tag.put("Item", this.food.save(new CompoundTag()));
        }
        tag.putInt("Rotation", this.rotation);
    }
}
