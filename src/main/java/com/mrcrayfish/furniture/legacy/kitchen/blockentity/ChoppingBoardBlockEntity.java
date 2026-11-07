package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ChoppingBoardBlockEntity extends SyncedBlockEntity
{
    private ItemStack food = ItemStack.EMPTY;

    public ChoppingBoardBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.CHOPPING_BOARD_ENTITY.get(), pos, state);
    }

    public void setFood(ItemStack food)
    {
        this.food = food;
        this.sync();
        this.updateComparators();
    }

    public ItemStack getFood()
    {
        return this.food;
    }

    /** Cuts the food with the knife and spawns the result. Returns true if the knife was used. */
    public boolean chopFood()
    {
        Optional<MachineRecipe> recipe = KitchenRecipes.find(KitchenRecipes.CHOPPING_BOARD, this.level, this.food);
        if(recipe.isEmpty())
            return false;
        if(!this.level.isClientSide())
        {
            this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.2, this.worldPosition.getZ() + 0.5, recipe.get().getResult().copy()));
            this.level.playSound(null, this.worldPosition.getX() + 0.5, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5, LegacySounds.KNIFE_CHOP.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
        }
        this.setFood(ItemStack.EMPTY);
        return true;
    }

    @Override
    public NonNullList<ItemStack> getDropStacks()
    {
        return NonNullList.of(ItemStack.EMPTY, this.food);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.food = tag.contains("Food", Tag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound("Food")) : ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        if(!this.food.isEmpty())
        {
            tag.put("Food", this.food.save(new CompoundTag()));
        }
    }
}
