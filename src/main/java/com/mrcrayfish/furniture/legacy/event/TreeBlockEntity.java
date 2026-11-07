package com.mrcrayfish.furniture.legacy.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * One ornament per side, indexed by Direction#get2DDataValue. Not a Container: hoppers must not
 * empty the tree.
 */
public class TreeBlockEntity extends BlockEntity
{
    public static final int SIDES = 4;
    private NonNullList<ItemStack> ornaments = NonNullList.withSize(SIDES, ItemStack.EMPTY);

    public TreeBlockEntity(BlockPos pos, BlockState state)
    {
        super(EventContent.TREE_BLOCK_ENTITY.get(), pos, state);
    }

    public NonNullList<ItemStack> getOrnaments()
    {
        return this.ornaments;
    }

    /**
     * Drops the ornament of this side if there is one, then hangs a copy of one item of the stack.
     *
     * @param stack the new ornament, not modified. Empty only removes the current ornament
     */
    public void setOrnament(Direction direction, ItemStack stack)
    {
        int index = direction.get2DDataValue();
        ItemStack old = this.ornaments.get(index);
        if(!old.isEmpty() && this.level != null)
        {
            this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5, old));
        }
        this.ornaments.set(index, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        this.setChanged();
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.ornaments = NonNullList.withSize(SIDES, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.ornaments);
    }

    /* Saves the list even when it is empty, a client must also see an ornament being removed */
    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.ornaments, true);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
