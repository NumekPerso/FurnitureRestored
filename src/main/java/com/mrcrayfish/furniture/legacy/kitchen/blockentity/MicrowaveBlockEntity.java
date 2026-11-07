package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.menu.MicrowaveMenu;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class MicrowaveBlockEntity extends KitchenContainerBlockEntity implements RunningMachine
{
    public static final int COOK_TIME = 40;
    public static final int DATA_COUNT = 2;
    public static final int DATA_COOKING = 0;
    public static final int DATA_PROGRESS = 1;

    private boolean cooking;
    private int progress;
    private int timer;

    private final ContainerData data = new ContainerData()
    {
        @Override
        public int get(int index)
        {
            return index == DATA_COOKING ? (MicrowaveBlockEntity.this.cooking ? 1 : 0) : MicrowaveBlockEntity.this.progress;
        }

        @Override
        public void set(int index, int value)
        {
            if(index == DATA_COOKING)
            {
                MicrowaveBlockEntity.this.cooking = value != 0;
            }
            else
            {
                MicrowaveBlockEntity.this.progress = value;
            }
        }

        @Override
        public int getCount()
        {
            return DATA_COUNT;
        }
    };

    public MicrowaveBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.MICROWAVE_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 1;
    }

    @Override
    public int getMaxStackSize()
    {
        return 1;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.microwave");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new MicrowaveMenu(windowId, playerInventory, this, this.data);
    }

    public boolean isCooking()
    {
        return this.cooking;
    }

    @Override
    public boolean isRunning()
    {
        return this.cooking;
    }

    public void startCooking()
    {
        if(!this.cooking && KitchenRecipes.find(KitchenRecipes.MICROWAVE, this.level, this.getItem(0)).isPresent())
        {
            this.cooking = true;
            this.timer = 0;
            this.setChanged();
            this.updateComparators();
        }
    }

    public void stopCooking()
    {
        this.cooking = false;
        this.progress = 0;
        this.setChanged();
        this.updateComparators();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MicrowaveBlockEntity microwave)
    {
        if(!microwave.cooking)
            return;

        microwave.progress++;
        if(microwave.progress >= COOK_TIME)
        {
            KitchenRecipes.find(KitchenRecipes.MICROWAVE, level, microwave.getItem(0)).ifPresent(recipe -> microwave.setItem(0, recipe.getResult().copy()));
            microwave.playSound(LegacySounds.MICROWAVE_FINISH.get());
            microwave.stopCooking();
        }
        else
        {
            if(microwave.timer == 20)
            {
                microwave.timer = 0;
            }
            if(microwave.timer == 0)
            {
                microwave.playSound(LegacySounds.MICROWAVE_RUNNING.get());
            }
            microwave.timer++;
            level.blockEntityChanged(pos);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MicrowaveBlockEntity microwave)
    {
        if(microwave.cooking)
        {
            double posX = pos.getX() + 0.35 + level.random.nextDouble() / 3.0;
            double posZ = pos.getZ() + 0.35 + level.random.nextDouble() / 3.0;
            level.addParticle(ParticleTypes.SMOKE, posX, pos.getY() + 0.065, posZ, 0.0, 0.0, 0.0);
        }
    }

    private void playSound(SoundEvent sound)
    {
        this.level.playSound(null, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, sound, SoundSource.BLOCKS, 0.75F, 1.0F);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return KitchenRecipes.find(KitchenRecipes.MICROWAVE, this.level, stack).isPresent();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction)
    {
        return !this.cooking && this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction)
    {
        return !this.cooking && !this.canPlaceItem(slot, stack);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.cooking = tag.getBoolean("Cooking");
        this.progress = tag.getInt("Progress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putBoolean("Cooking", this.cooking);
        tag.putInt("Progress", this.progress);
    }
}
