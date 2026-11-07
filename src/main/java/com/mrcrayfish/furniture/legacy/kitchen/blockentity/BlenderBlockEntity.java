package com.mrcrayfish.furniture.legacy.kitchen.blockentity;

import com.mrcrayfish.furniture.legacy.LegacySounds;
import com.mrcrayfish.furniture.legacy.kitchen.KitchenContent;
import com.mrcrayfish.furniture.legacy.kitchen.item.CupItem;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.BlenderRecipe;
import com.mrcrayfish.furniture.legacy.kitchen.recipe.KitchenRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Takes up to four whole stacks. Blending gives six drinks, each one needs a cup.
 */
public class BlenderBlockEntity extends SyncedBlockEntity implements RunningMachine
{
    public static final int BLEND_TIME = 200;
    public static final int DRINK_COUNT = 6;

    private final NonNullList<ItemStack> ingredients = NonNullList.withSize(4, ItemStack.EMPTY);
    private boolean blending;
    private int progress;
    private int timer;
    private int drinkCount;
    private String drinkName = "";
    private int healAmount;
    private int red, green, blue;

    public BlenderBlockEntity(BlockPos pos, BlockState state)
    {
        super(KitchenContent.BLENDER_ENTITY.get(), pos, state);
    }

    public void addIngredient(ItemStack stack)
    {
        for(int i = 0; i < this.ingredients.size(); i++)
        {
            if(this.ingredients.get(i).isEmpty())
            {
                this.ingredients.set(i, stack.copy());
                this.sync();
                return;
            }
        }
    }

    public void removeIngredient()
    {
        for(int i = this.ingredients.size() - 1; i >= 0; i--)
        {
            ItemStack stack = this.ingredients.get(i);
            if(!stack.isEmpty())
            {
                this.spawn(stack, 1.0);
                this.ingredients.set(i, ItemStack.EMPTY);
                this.sync();
                return;
            }
        }
    }

    public boolean isFull()
    {
        return this.ingredients.stream().noneMatch(ItemStack::isEmpty);
    }

    public NonNullList<ItemStack> getIngredients()
    {
        return this.ingredients;
    }

    /** Starts when the ingredients match a recipe, and keeps the drink it gives. */
    public boolean tryStartBlending()
    {
        SimpleContainer container = new SimpleContainer(this.ingredients.toArray(new ItemStack[0]));
        Optional<BlenderRecipe> recipe = this.level.getRecipeManager().getRecipeFor(KitchenRecipes.BLENDER.get(), container, this.level);
        if(recipe.isEmpty())
            return false;
        this.drinkName = recipe.get().getName();
        this.healAmount = recipe.get().getHeal();
        int[] color = recipe.get().getColor();
        this.red = color[0];
        this.green = color[1];
        this.blue = color[2];
        this.blending = true;
        this.progress = 0;
        this.timer = 0;
        this.sync();
        this.updateComparators();
        return true;
    }

    public boolean isBlending()
    {
        return this.blending;
    }

    @Override
    public boolean isRunning()
    {
        return this.blending;
    }

    public int getProgress()
    {
        return this.progress;
    }

    public int getDrinkCount()
    {
        return this.drinkCount;
    }

    public boolean hasDrink()
    {
        return this.drinkCount > 0;
    }

    public int getRed()
    {
        return this.red;
    }

    public int getGreen()
    {
        return this.green;
    }

    public int getBlue()
    {
        return this.blue;
    }

    /** Takes one of the drinks. */
    public ItemStack takeDrink()
    {
        this.drinkCount--;
        this.sync();
        return CupItem.createDrink(this.drinkName, this.healAmount, new int[]{this.red, this.green, this.blue});
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlenderBlockEntity blender)
    {
        if(!blender.blending)
            return;

        blender.progress++;
        if(blender.progress >= BLEND_TIME)
        {
            blender.clearIngredients();
            blender.drinkCount = DRINK_COUNT;
            blender.progress = 0;
            blender.blending = false;
            blender.sync();
            blender.updateComparators();
            return;
        }

        if(blender.timer == 20)
        {
            blender.timer = 0;
        }
        if(blender.timer == 0)
        {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, LegacySounds.BLENDER.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
        }
        blender.timer++;
        level.blockEntityChanged(pos);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, BlenderBlockEntity blender)
    {
        if(blender.blending && blender.progress < BLEND_TIME)
        {
            blender.progress++;
        }
    }

    /** Empties the blender, the containers of the ingredients stay (the bucket of the milk). */
    private void clearIngredients()
    {
        for(int i = 0; i < this.ingredients.size(); i++)
        {
            ItemStack stack = this.ingredients.get(i);
            if(stack.hasCraftingRemainingItem())
            {
                ItemStack remainder = stack.getCraftingRemainingItem();
                remainder.setCount(stack.getCount());
                this.spawn(remainder, 0.6);
            }
            this.ingredients.set(i, ItemStack.EMPTY);
        }
    }

    private void spawn(ItemStack stack, double yOffset)
    {
        if(this.level != null && !this.level.isClientSide())
        {
            this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + yOffset, this.worldPosition.getZ() + 0.5, stack));
        }
    }

    @Override
    public NonNullList<ItemStack> getDropStacks()
    {
        return this.ingredients;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.ingredients.clear();
        ContainerHelper.loadAllItems(tag, this.ingredients);
        this.blending = tag.getBoolean("Blending");
        this.progress = tag.getInt("Progress");
        this.drinkCount = tag.getInt("DrinkCount");
        this.drinkName = tag.getString("DrinkName");
        this.healAmount = tag.getInt("HealAmount");
        this.red = tag.getInt("CurrentRed");
        this.green = tag.getInt("CurrentGreen");
        this.blue = tag.getInt("CurrentBlue");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.ingredients);
        tag.putBoolean("Blending", this.blending);
        tag.putInt("Progress", this.progress);
        tag.putInt("DrinkCount", this.drinkCount);
        tag.putString("DrinkName", this.drinkName);
        tag.putInt("HealAmount", this.healAmount);
        tag.putInt("CurrentRed", this.red);
        tag.putInt("CurrentGreen", this.green);
        tag.putInt("CurrentBlue", this.blue);
    }
}
