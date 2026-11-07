package com.mrcrayfish.furniture.legacy.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Client animation of a sliding door half. Nothing is saved: the position comes from the blockstate.
 */
public class ModernSlidingDoorBlockEntity extends BlockEntity
{
    private static final int MAX_PROGRESS = 10;

    private boolean started;
    private int slideProgress;
    private int prevSlideProgress;

    public ModernSlidingDoorBlockEntity(BlockPos pos, BlockState state)
    {
        super(DecorContent.MODERN_SLIDING_DOOR_ENTITY.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ModernSlidingDoorBlockEntity door)
    {
        boolean open = state.getValue(ModernSlidingDoorBlock.OPEN);
        if(!door.started)
        {
            // A door that comes into view already open must not play the animation
            door.started = true;
            door.slideProgress = open ? MAX_PROGRESS : 0;
            door.prevSlideProgress = door.slideProgress;
            return;
        }
        door.prevSlideProgress = door.slideProgress;
        if(open)
        {
            if(door.slideProgress < MAX_PROGRESS)
            {
                door.slideProgress++;
            }
        }
        else if(door.slideProgress > 0)
        {
            door.slideProgress--;
        }
    }

    public float getSlideProgress(float partialTicks)
    {
        float partial = this.prevSlideProgress + (this.slideProgress - this.prevSlideProgress) * partialTicks;
        float normal = partial / (float) MAX_PROGRESS;
        return 0.815F * (1.0F - ((float) Math.sin(Math.toRadians(90.0 + 180.0 * normal)) / 2.0F + 0.5F));
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return new AABB(this.worldPosition).inflate(1.0);
    }
}
