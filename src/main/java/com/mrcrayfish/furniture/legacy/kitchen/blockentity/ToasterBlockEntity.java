package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ToasterBlockEntity extends SyncedBlockEntity implements RunningMachine
{
    private static final int TOAST_TIME = 200;

    private final NonNullList<ItemStack> slices = NonNullList.withSize(2, ItemStack.EMPTY);
    private int toastingTime;
    private boolean toasting;

    public ToasterBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.TOASTER_ENTITY.get(), pos, state);
    }

    public boolean addSlice(ItemStack stack)
    {
        for(int i = 0; i < this.slices.size(); i++)
        {
            if(this.slices.get(i).isEmpty())
            {
                this.slices.set(i, stack.copyWithCount(1));
                this.sync();
                return true;
            }
        }
        return false;
    }

    public void removeSlice()
    {
        for(int i = 0; i < this.slices.size(); i++)
        {
            ItemStack slice = this.slices.get(i);
            if(!slice.isEmpty())
            {
                this.spawn(slice);
                this.slices.set(i, ItemStack.EMPTY);
                this.sync();
                return;
            }
        }
    }

    public void startToasting()
    {
        this.toasting = true;
        this.playSound(LegacySounds.TOASTER_DOWN.get());
        this.sync();
        this.updateComparators();
    }

    public boolean isToasting()
    {
        return this.toasting;
    }

    @Override
    public boolean isRunning()
    {
        return this.toasting;
    }

    public ItemStack getSlice(int slot)
    {
        return this.slices.get(slot);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ToasterBlockEntity toaster)
    {
        if(!toaster.toasting)
            return;

        if(toaster.toastingTime < TOAST_TIME)
        {
            toaster.toastingTime++;
            level.blockEntityChanged(pos);
            return;
        }

        for(int i = 0; i < toaster.slices.size(); i++)
        {
            ItemStack slice = toaster.slices.get(i);
            if(!slice.isEmpty())
            {
                KitchenRecipes.find(KitchenRecipes.TOASTER, level, slice).ifPresent(recipe -> toaster.spawn(recipe.getResult().copy()));
                toaster.slices.set(i, ItemStack.EMPTY);
            }
        }
        toaster.playSound(LegacySounds.TOASTER_DOWN.get());
        toaster.toastingTime = 0;
        toaster.toasting = false;
        toaster.sync();
        toaster.updateComparators();
    }

    private void spawn(ItemStack stack)
    {
        if(this.level != null && !this.level.isClientSide())
        {
            this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.6, this.worldPosition.getZ() + 0.5, stack));
        }
    }

    private void playSound(SoundEvent sound)
    {
        this.level.playSound(null, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, sound, SoundSource.BLOCKS, 0.75F, 1.0F);
    }

    @Override
    public NonNullList<ItemStack> getDropStacks()
    {
        return this.slices;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.slices.clear();
        ContainerHelper.loadAllItems(tag, this.slices);
        this.toastingTime = tag.getInt("ToastTime");
        this.toasting = tag.getBoolean("Toasting");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.slices);
        tag.putInt("ToastTime", this.toastingTime);
        tag.putBoolean("Toasting", this.toasting);
    }
}
