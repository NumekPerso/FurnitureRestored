package com.mrcrayfish.furniture.legacy.kitchen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mrcrayfish.furniture.legacy.kitchen.item.CupItem;
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
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipe of a blender. Each ingredient needs one stack of the exact count.
 */
public class BlenderRecipe implements Recipe<Container>
{
    public record Entry(Ingredient ingredient, int count) {}

    private final ResourceLocation id;
    private final String name;
    private final int heal;
    private final int[] color;
    private final List<Entry> entries;

    public BlenderRecipe(ResourceLocation id, String name, int heal, int[] color, List<Entry> entries)
    {
        this.id = id;
        this.name = name;
        this.heal = heal;
        this.color = color;
        this.entries = entries;
    }

    public String getName()
    {
        return this.name;
    }

    public int getHeal()
    {
        return this.heal;
    }

    public int[] getColor()
    {
        return this.color;
    }

    @Override
    public boolean matches(Container container, Level level)
    {
        boolean[] found = new boolean[this.entries.size()];
        int count = 0;
        for(int i = 0; i < container.getContainerSize(); i++)
        {
            ItemStack stack = container.getItem(i);
            if(stack.isEmpty())
                continue;
            for(int j = 0; j < found.length; j++)
            {
                Entry entry = this.entries.get(j);
                if(!found[j] && entry.count() == stack.getCount() && entry.ingredient().test(stack))
                {
                    found[j] = true;
                    count++;
                    break;
                }
            }
        }
        return count == found.length;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access)
    {
        return CupItem.createDrink(this.name, this.heal, this.color);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access)
    {
        return CupItem.createDrink(this.name, this.heal, this.color);
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        NonNullList<Ingredient> list = NonNullList.create();
        this.entries.forEach(entry -> list.add(entry.ingredient()));
        return list;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return KitchenRecipes.BLENDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return KitchenRecipes.BLENDER.get();
    }

    @Override
    public boolean isSpecial()
    {
        return true;
    }

    public static class Serializer implements RecipeSerializer<BlenderRecipe>
    {
        @Override
        public BlenderRecipe fromJson(ResourceLocation id, JsonObject object)
        {
            String name = GsonHelper.getAsString(object, "name");
            int heal = GsonHelper.getAsInt(object, "heal");
            JsonArray colorArray = GsonHelper.getAsJsonArray(object, "color");
            if(colorArray.size() != 3)
                throw new JsonSyntaxException("Expected a color with 3 values in " + id);
            int[] color = {colorArray.get(0).getAsInt(), colorArray.get(1).getAsInt(), colorArray.get(2).getAsInt()};
            List<Entry> entries = new ArrayList<>();
            for(JsonElement element : GsonHelper.getAsJsonArray(object, "ingredients"))
            {
                JsonObject entry = element.getAsJsonObject();
                entries.add(new Entry(Ingredient.fromJson(entry), GsonHelper.getAsInt(entry, "count", 1)));
            }
            if(entries.isEmpty() || entries.size() > 4)
                throw new JsonSyntaxException("Expected 1 to 4 ingredients in " + id);
            return new BlenderRecipe(id, name, heal, color, entries);
        }

        @Override
        public BlenderRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            String name = buffer.readUtf();
            int heal = buffer.readVarInt();
            int[] color = {buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt()};
            int size = buffer.readVarInt();
            List<Entry> entries = new ArrayList<>();
            for(int i = 0; i < size; i++)
            {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);
                entries.add(new Entry(ingredient, buffer.readVarInt()));
            }
            return new BlenderRecipe(id, name, heal, color, entries);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BlenderRecipe recipe)
        {
            buffer.writeUtf(recipe.name);
            buffer.writeVarInt(recipe.heal);
            for(int value : recipe.color)
            {
                buffer.writeVarInt(value);
            }
            buffer.writeVarInt(recipe.entries.size());
            for(Entry entry : recipe.entries)
            {
                entry.ingredient().toNetwork(buffer);
                buffer.writeVarInt(entry.count());
            }
        }
    }
}
