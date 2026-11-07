package com.mrcrayfish.furniture.legacy.kitchen.recipe;

import com.google.gson.JsonObject;
import com.mrcrayfish.furniture.core.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.registries.RegistryObject;

/**
 * Crafting recipes that leave nothing in the grid. Soapy water keeps the bucket of its ingredient
 * as its own crafting remainder: the vanilla recipes would give a second, free bucket.
 */
public class NoRemainderRecipes
{
    public static final RegistryObject<RecipeSerializer<Shapeless>> SHAPELESS = ModRecipeSerializers.REGISTER.register("shapeless_no_remainder", ShapelessSerializer::new);
    public static final RegistryObject<RecipeSerializer<Shaped>> SHAPED = ModRecipeSerializers.REGISTER.register("shaped_no_remainder", ShapedSerializer::new);

    public static void init() {}

    public static class Shapeless extends ShapelessRecipe
    {
        public Shapeless(ShapelessRecipe recipe)
        {
            super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(CraftingContainer container)
        {
            return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return SHAPELESS.get();
        }
    }

    public static class Shaped extends ShapedRecipe
    {
        public Shaped(ShapedRecipe recipe)
        {
            super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(null), recipe.showNotification());
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(CraftingContainer container)
        {
            return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return SHAPED.get();
        }
    }

    private static class ShapelessSerializer implements RecipeSerializer<Shapeless>
    {
        @Override
        public Shapeless fromJson(ResourceLocation id, JsonObject json)
        {
            return new Shapeless(RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json));
        }

        @Override
        public Shapeless fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            return new Shapeless(RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, Shapeless recipe)
        {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
        }
    }

    private static class ShapedSerializer implements RecipeSerializer<Shaped>
    {
        @Override
        public Shaped fromJson(ResourceLocation id, JsonObject json)
        {
            return new Shaped(RecipeSerializer.SHAPED_RECIPE.fromJson(id, json));
        }

        @Override
        public Shaped fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            return new Shaped(RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, Shaped recipe)
        {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
