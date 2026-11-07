package com.mrcrayfish.furniture.legacy.event;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

/**
 * Shapeless recipe whose damageable ingredient (the axe) loses one durability point and stays in
 * the crafting grid, until it breaks.
 */
public class CuttingShapelessRecipe extends ShapelessRecipe
{
    private final RandomSource random = RandomSource.create();

    public CuttingShapelessRecipe(ShapelessRecipe recipe)
    {
        super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container)
    {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for(int i = 0; i < remaining.size(); i++)
        {
            ItemStack stack = container.getItem(i);
            if(stack.getMaxDamage() > 0) // Not isDamageableItem: it would consume an unbreakable axe
            {
                remaining.set(i, this.damageTool(stack.copy()));
            }
            else if(stack.hasCraftingRemainingItem())
            {
                remaining.set(i, stack.getCraftingRemainingItem());
            }
        }
        return remaining;
    }

    private ItemStack damageTool(ItemStack stack)
    {
        Player player = ForgeHooks.getCraftingPlayer();
        if(stack.hurt(1, this.random, player instanceof ServerPlayer serverPlayer ? serverPlayer : null))
        {
            if(player != null)
            {
                ForgeEventFactory.onPlayerDestroyItem(player, stack, null);
            }
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return EventContent.CUTTING_SHAPELESS.get();
    }

    /** Same format as the vanilla shapeless recipe. */
    public static class Serializer implements RecipeSerializer<CuttingShapelessRecipe>
    {
        @Override
        public CuttingShapelessRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            return new CuttingShapelessRecipe(RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json));
        }

        @Nullable
        @Override
        public CuttingShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            return new CuttingShapelessRecipe(RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CuttingShapelessRecipe recipe)
        {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
