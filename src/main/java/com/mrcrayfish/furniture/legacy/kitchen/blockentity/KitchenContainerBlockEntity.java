package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.kitchen.menu.KitchenMenu;
import com.mrcrayfish.furniture.tileentity.BasicLootBlockEntity;
import com.mrcrayfish.furniture.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

/**
 * Inventory synchronised with the clients, for the renderers and the particles. Hoppers go through
 * the capability, so it has to respect the sides given by getSlotsForFace.
 */
public abstract class KitchenContainerBlockEntity extends BasicLootBlockEntity
{
    private LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    protected KitchenContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public boolean isMatchingContainerMenu(AbstractContainerMenu menu)
    {
        return menu instanceof KitchenMenu kitchenMenu && kitchenMenu.getContainer() == this;
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        this.sync();
    }

    /** Sends the block entity to the players tracking it. */
    protected void sync()
    {
        if(this.level != null && !this.level.isClientSide())
        {
            BlockEntityUtil.sendUpdatePacket(this);
        }
    }

    /** Tells the comparators next to this block that the machine started or stopped. */
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side)
    {
        if(!this.remove && side != null && capability == ForgeCapabilities.ITEM_HANDLER)
        {
            return switch(side)
            {
                case UP -> this.handlers[0].cast();
                case DOWN -> this.handlers[1].cast();
                default -> this.handlers[2].cast();
            };
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        for(LazyOptional<? extends IItemHandler> handler : this.handlers)
        {
            handler.invalidate();
        }
    }

    @Override
    public void reviveCaps()
    {
        super.reviveCaps();
        this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    }
}
