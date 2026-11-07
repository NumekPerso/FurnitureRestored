package com.mrcrayfish.furniture.legacy.electronics;

import com.mrcrayfish.furniture.legacy.IPowered;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class LightSwitchBlockEntity extends BlockEntity
{
    private final List<BlockPos> lights = new ArrayList<>();

    public LightSwitchBlockEntity(BlockPos pos, BlockState state)
    {
        super(ElectronicsContent.LIGHT_SWITCH_ENTITY.get(), pos, state);
    }

    public boolean hasLights()
    {
        return !this.lights.isEmpty();
    }

    /** Applies the new state to the switch and its links, and forgets the links that are gone. */
    public void setState(boolean on)
    {
        if(this.level == null)
            return;

        this.lights.removeIf(lightPos -> !(this.level.getBlockState(lightPos).getBlock() instanceof IPowered));
        for(BlockPos lightPos : this.lights)
        {
            BlockState state = this.level.getBlockState(lightPos);
            ((IPowered) state.getBlock()).setPowered(this.level, lightPos, state, on);
        }
        this.level.setBlock(this.worldPosition, this.getBlockState().setValue(LightSwitchBlock.ON, on), Block.UPDATE_ALL);
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        ListTag list = new ListTag();
        this.lights.forEach(lightPos -> list.add(LongTag.valueOf(lightPos.asLong())));
        tag.put(LightSwitchItem.LIGHTS_TAG, list);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        if(tag.contains(LightSwitchItem.LIGHTS_TAG, Tag.TAG_LIST))
        {
            this.lights.clear();
            for(Tag light : tag.getList(LightSwitchItem.LIGHTS_TAG, Tag.TAG_LONG))
            {
                BlockPos lightPos = BlockPos.of(((LongTag) light).getAsLong());
                if(!this.lights.contains(lightPos))
                {
                    this.lights.add(lightPos);
                }
            }
        }
    }
}
