package com.mrcrayfish.furniture.legacy.electronics;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Remembers the current track, or -1 when stopped.
 */
public class StereoBlockEntity extends BlockEntity
{
    private static final int STOPPED = -1;

    private int track = STOPPED;

    public StereoBlockEntity(BlockPos pos, BlockState state)
    {
        super(ElectronicsContent.STEREO_ENTITY.get(), pos, state);
    }

    public int getTrack()
    {
        return this.track;
    }

    public boolean isPlaying()
    {
        return this.track != STOPPED;
    }

    /** Moves to the next track, wrapping around, and returns it. */
    public int nextTrack(int trackCount)
    {
        this.track = this.track + 1 >= trackCount ? 0 : this.track + 1;
        this.setChanged();
        return this.track;
    }

    public void stop()
    {
        this.track = STOPPED;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putInt("track", this.track);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.track = tag.contains("track", Tag.TAG_INT) ? tag.getInt("track") : STOPPED;
    }
}
