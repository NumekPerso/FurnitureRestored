package com.mrcrayfish.furniture.legacy.electronics;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalClockBlockEntity extends BlockEntity
{
    private DyeColor textColor = DyeColor.WHITE;

    public DigitalClockBlockEntity(BlockPos pos, BlockState state)
    {
        super(ElectronicsContent.DIGITAL_CLOCK_ENTITY.get(), pos, state);
    }

    public DyeColor getTextColor()
    {
        return this.textColor;
    }

    public void setTextColor(DyeColor color)
    {
        this.textColor = color;
        this.setChanged();
        if(this.level != null)
        {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putByte("textColor", (byte) this.textColor.getId());
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        if(tag.contains("textColor", Tag.TAG_BYTE))
        {
            this.textColor = DyeColor.byId(tag.getByte("textColor"));
        }
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
