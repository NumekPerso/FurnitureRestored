package com.mrcrayfish.furniture.legacy.electronics;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * The server only stores whether the fan runs, the rotation is animated on the client.
 */
public class CeilingFanBlockEntity extends BlockEntity
{
    private boolean powered;
    private float maxSpeed = 30F;
    private float acceleration = 0.25F;

    private float currentSpeed;
    private float prevFanRotation;
    private float fanRotation;

    public CeilingFanBlockEntity(BlockPos pos, BlockState state)
    {
        super(ElectronicsContent.CEILING_FAN_ENTITY.get(), pos, state);
    }

    public boolean isPowered()
    {
        return this.powered;
    }

    public void setPowered(boolean powered)
    {
        this.powered = powered;
        this.setChanged();
        if(this.level != null)
        {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public float getRotation(float partialTicks)
    {
        return this.prevFanRotation + (this.fanRotation - this.prevFanRotation) * partialTicks;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CeilingFanBlockEntity fan)
    {
        fan.prevFanRotation = fan.fanRotation;
        if(fan.powered)
        {
            fan.currentSpeed = Math.min(fan.currentSpeed + fan.acceleration, fan.maxSpeed);
        }
        else
        {
            fan.currentSpeed *= 0.95F;
        }
        fan.fanRotation += fan.currentSpeed;
        if(fan.fanRotation >= 360F) // Keeps the precision of the float when the fan runs for hours
        {
            fan.fanRotation -= 360F;
            fan.prevFanRotation -= 360F;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putBoolean("powered", this.powered);
        tag.putFloat("maxSpeed", this.maxSpeed);
        tag.putFloat("acceleration", this.acceleration);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        if(tag.contains("maxSpeed", Tag.TAG_FLOAT))
        {
            this.maxSpeed = tag.getFloat("maxSpeed");
        }
        if(tag.contains("acceleration", Tag.TAG_FLOAT))
        {
            this.acceleration = tag.getFloat("acceleration");
        }
        // The field and not setPowered: the level is not set yet when a chunk loads
        this.powered = tag.getBoolean("powered");
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

    @Override
    public AABB getRenderBoundingBox()
    {
        return new AABB(this.worldPosition).inflate(2.0);
    }
}
