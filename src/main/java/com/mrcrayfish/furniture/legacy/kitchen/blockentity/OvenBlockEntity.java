package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.menu.OvenMenu;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Slots 0 to 3 are cooked into the slots 4 to 7 below them, one item at a time.
 */
public class OvenBlockEntity extends KitchenContainerBlockEntity implements RunningMachine
{
    public static final int COOK_TIME = 200;
    public static final int DATA_COUNT = 2;
    private static final int[] INPUT_SLOTS = {0, 1, 2, 3};
    private static final int[] OUTPUT_SLOTS = {4, 5, 6, 7};

    private int cookingTime;
    private int cookingItem = -1;
    private boolean active;

    private final ContainerData data = new ContainerData()
    {
        @Override
        public int get(int index)
        {
            return index == 0 ? OvenBlockEntity.this.cookingTime : OvenBlockEntity.this.cookingItem;
        }

        @Override
        public void set(int index, int value)
        {
            if(index == 0)
            {
                OvenBlockEntity.this.cookingTime = value;
            }
            else
            {
                OvenBlockEntity.this.cookingItem = value;
            }
        }

        @Override
        public int getCount()
        {
            return DATA_COUNT;
        }
    };

    public OvenBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.OVEN_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 8;
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("container.furniture_restored.oven");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory)
    {
        return new OvenMenu(windowId, playerInventory, this, this.data);
    }

    @Override
    public boolean isRunning()
    {
        return this.cookingTime > 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, OvenBlockEntity oven)
    {
        boolean wasCooking = oven.cookingTime > 0;

        oven.cookingItem = oven.findCookingSlot();
        if(oven.cookingItem != -1)
        {
            oven.cookingTime++;
            if(!wasCooking)
            {
                oven.updateComparators();
            }
            if(oven.cookingTime >= COOK_TIME)
            {
                oven.cookingTime = 0;
                oven.cookItem(oven.cookingItem);
            }
            level.blockEntityChanged(pos);
        }
        else
        {
            oven.cookingTime = 0;
        }

        if(wasCooking && oven.cookingTime == 0)
        {
            oven.updateComparators();
        }

        boolean active = oven.cookingItem != -1;
        if(active != oven.active)
        {
            oven.active = active;
            oven.setChanged();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, OvenBlockEntity oven)
    {
        if(oven.active)
        {
            level.addParticle(ParticleTypes.SMOKE, pos.getX() + level.random.nextDouble(), pos.getY() + 1.0, pos.getZ() + level.random.nextDouble(), 0.0, 0.0, 0.0);
        }
    }

    /** The first slot with an item decides: an item without recipe stops the oven. */
    private int findCookingSlot()
    {
        for(int slot = 0; slot < 4; slot++)
        {
            ItemStack input = this.getItem(slot);
            if(!input.isEmpty())
            {
                Optional<MachineRecipe> recipe = KitchenRecipes.find(KitchenRecipes.OVEN, this.level, input);
                return recipe.isPresent() && this.fitsInOutput(slot + 4, recipe.get().getResult()) ? slot : -1;
            }
        }
        return -1;
    }

    private boolean fitsInOutput(int slot, ItemStack result)
    {
        ItemStack output = this.getItem(slot);
        if(output.isEmpty())
            return true;
        if(!ItemStack.isSameItemSameTags(output, result))
            return false;
        return output.getCount() + result.getCount() <= Math.min(this.getMaxStackSize(), output.getMaxStackSize());
    }

    private void cookItem(int slot)
    {
        ItemStack input = this.getItem(slot);
        Optional<MachineRecipe> recipe = KitchenRecipes.find(KitchenRecipes.OVEN, this.level, input);
        if(recipe.isEmpty() || !this.fitsInOutput(slot + 4, recipe.get().getResult()))
            return;

        ItemStack result = recipe.get().getResult();
        ItemStack output = this.getItem(slot + 4);
        if(output.isEmpty())
        {
            this.setItem(slot + 4, result.copy());
        }
        else
        {
            output.grow(result.getCount());
        }

        ItemStack remainder = input.hasCraftingRemainingItem() ? input.getCraftingRemainingItem() : ItemStack.EMPTY;
        input.shrink(1);
        if(input.isEmpty())
        {
            this.setItem(slot, remainder);
        }
        else if(!remainder.isEmpty())
        {
            Containers.dropItemStack(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + 0.5, remainder);
        }
        this.setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction direction)
    {
        return direction == Direction.DOWN ? OUTPUT_SLOTS : INPUT_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction)
    {
        return slot < 4 && KitchenRecipes.find(KitchenRecipes.OVEN, this.level, stack).isPresent();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction)
    {
        return slot >= 4;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.cookingTime = tag.getShort("CookingTime");
        this.active = tag.getBoolean("Active");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putShort("CookingTime", (short) this.cookingTime);
        tag.putBoolean("Active", this.active);
    }
}
