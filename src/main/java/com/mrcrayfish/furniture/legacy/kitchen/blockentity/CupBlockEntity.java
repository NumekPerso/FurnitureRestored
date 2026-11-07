package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.item.CupItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Keeps the drink of a cup placed in the world: its item, for the drop, and its color, for the renderer.
 */
public class CupBlockEntity extends SyncedBlockEntity
{
    private ItemStack drink = ItemStack.EMPTY;
    private int color = 0xFFFFFF;

    public CupBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.CUP_ENTITY.get(), pos, state);
    }

    public void setDrink(ItemStack stack)
    {
        this.drink = stack.copyWithCount(1);
        this.color = CupItem.getColor(stack);
        this.sync();
    }

    public void clearDrink()
    {
        this.drink = ItemStack.EMPTY;
        this.color = 0xFFFFFF;
        this.sync();
    }

    public ItemStack getDrink()
    {
        return this.drink;
    }

    /** Color as 0xRRGGBB. */
    public int getColor()
    {
        return this.color;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.drink = tag.contains("Item", Tag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound("Item")) : ItemStack.EMPTY;
        this.color = this.drink.isEmpty() ? 0xFFFFFF : CupItem.getColor(this.drink);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        if(!this.drink.isEmpty())
        {
            tag.put("Item", this.drink.save(new CompoundTag()));
        }
    }
}
