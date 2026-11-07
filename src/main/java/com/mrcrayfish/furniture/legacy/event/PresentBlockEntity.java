package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Keeps the content of a placed present. Not a Container: a hopper must not open a present.
 */
public class PresentBlockEntity extends BlockEntity
{
    private String ownerName = "Unknown";
    private NonNullList<ItemStack> items = NonNullList.withSize(MailLayout.PRESENT.size(), ItemStack.EMPTY);

    public PresentBlockEntity(BlockPos pos, BlockState state)
    {
        super(EventContent.PRESENT_BLOCK_ENTITY.get(), pos, state);
    }

    public void setContent(String ownerName, NonNullList<ItemStack> items)
    {
        this.ownerName = ownerName;
        this.items = items;
        this.setChanged();
    }

    public String getOwnerName()
    {
        return this.ownerName;
    }

    public NonNullList<ItemStack> getItems()
    {
        return this.items;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.ownerName = tag.getString("OwnerName");
        this.items = NonNullList.withSize(MailLayout.PRESENT.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putString("OwnerName", this.ownerName);
        ContainerHelper.saveAllItems(tag, this.items);
    }
}
