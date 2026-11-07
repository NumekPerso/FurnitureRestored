package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Block entity without menu whose content is drawn by a renderer. It is not a Container: hoppers
 * cannot use it.
 */
public abstract class SyncedBlockEntity extends BlockEntity
{
    protected SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    /** Saves and sends the block entity to the players tracking it. */
    public void sync()
    {
        this.setChanged();
        if(this.level != null && !this.level.isClientSide())
        {
            BlockEntityUtil.sendUpdatePacket(this);
        }
    }

    /** Items dropped with the block. */
    public NonNullList<ItemStack> getDropStacks()
    {
        return NonNullList.create();
    }

    protected void updateComparators()
    {
        if(this.level != null)
        {
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
