package com.mrcrayfish.furniture.legacy.shared;

import com.mrcrayfish.furniture.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Holds the item placed on a coffee table.
 */
public class CoffeeTableBlockEntity extends BlockEntity
{
    private ItemStack item = ItemStack.EMPTY;
    private int rotation;

    public CoffeeTableBlockEntity(BlockPos pos, BlockState state)
    {
        super(SharedContent.COFFEE_TABLE.get(), pos, state);
    }

    public ItemStack getItem()
    {
        return this.item;
    }

    public int getRotation()
    {
        return this.rotation;
    }

    public void setItem(ItemStack item, int rotation)
    {
        this.item = item;
        this.rotation = rotation;
        this.setChanged();
        BlockEntityUtil.sendUpdatePacket(this);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        this.item = compound.contains("Item", Tag.TAG_COMPOUND) ? ItemStack.of(compound.getCompound("Item")) : ItemStack.EMPTY;
        this.rotation = compound.getInt("Rotation");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        if(!this.item.isEmpty())
        {
            tag.put("Item", this.item.save(new CompoundTag()));
        }
        tag.putInt("Rotation", this.rotation);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveWithFullMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        this.load(pkt.getTag());
    }
}
