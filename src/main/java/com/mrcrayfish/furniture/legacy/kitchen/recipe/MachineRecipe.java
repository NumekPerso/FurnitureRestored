package com.mrcrayfish.furniture.legacy.kitchen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

/**
 * A recipe with one ingredient and one result, used by the oven, the microwave, the toaster and the chopping board.
 */
public class MachineRecipe implements Recipe<Container>
{
    private final ResourceLocation id;
    private final Serializer serializer;
    private final Ingredient ingredient;
    private final ItemStack result;

    public MachineRecipe(ResourceLocation id, Serializer serializer, Ingredient ingredient, ItemStack result)
    {
        this.id = id;
        this.serializer = serializer;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(Container container, Level level)
    {
        return this.ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access)
    {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access)
    {
        return this.result;
    }

    /** The result, for callers without a registry access: it does not depend on it. */
    public ItemStack getResult()
    {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.of(Ingredient.EMPTY, this.ingredient);
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return this.serializer;
    }

    @Override
    public RecipeType<?> getType()
    {
        return this.serializer.type.get();
    }

    @Override
    public boolean isSpecial()
    {
        return true;
    }

    public static class Serializer implements RecipeSerializer<MachineRecipe>
    {
        private final RegistryObject<RecipeType<MachineRecipe>> type;

        public Serializer(RegistryObject<RecipeType<MachineRecipe>> type)
        {
            this.type = type;
        }

        @Override
        public MachineRecipe fromJson(ResourceLocation id, JsonObject object)
        {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(object, "ingredient"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(object, "result"));
            return new MachineRecipe(id, this, ingredient, result);
        }

        @Override
        public MachineRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            return new MachineRecipe(id, this, ingredient, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MachineRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}
