package com.mrcrayfish.furniture.legacy.kitchen.recipe;

import com.mrcrayfish.furniture.core.ModRecipeSerializers;
import com.mrcrayfish.furniture.core.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

/**
 * One recipe type per machine. The recipes are in data/furniture_restored/recipes.
 */
public class KitchenRecipes
{
    public static final RegistryObject<RecipeType<MachineRecipe>> OVEN = type("oven");
    public static final RegistryObject<RecipeType<MachineRecipe>> MICROWAVE = type("microwave");
    public static final RegistryObject<RecipeType<MachineRecipe>> TOASTER = type("toaster");
    public static final RegistryObject<RecipeType<MachineRecipe>> CHOPPING_BOARD = type("chopping_board");
    public static final RegistryObject<RecipeType<BlenderRecipe>> BLENDER = type("blender");

    public static final RegistryObject<MachineRecipe.Serializer> OVEN_SERIALIZER = ModRecipeSerializers.REGISTER.register("oven", () -> new MachineRecipe.Serializer(OVEN));
    public static final RegistryObject<MachineRecipe.Serializer> MICROWAVE_SERIALIZER = ModRecipeSerializers.REGISTER.register("microwave", () -> new MachineRecipe.Serializer(MICROWAVE));
    public static final RegistryObject<MachineRecipe.Serializer> TOASTER_SERIALIZER = ModRecipeSerializers.REGISTER.register("toaster", () -> new MachineRecipe.Serializer(TOASTER));
    public static final RegistryObject<MachineRecipe.Serializer> CHOPPING_BOARD_SERIALIZER = ModRecipeSerializers.REGISTER.register("chopping_board", () -> new MachineRecipe.Serializer(CHOPPING_BOARD));
    public static final RegistryObject<BlenderRecipe.Serializer> BLENDER_SERIALIZER = ModRecipeSerializers.REGISTER.register("blender", BlenderRecipe.Serializer::new);

    public static final TagKey<Item> DISHWASHER_REPAIRABLE = ItemTags.create(new ResourceLocation("furniture_restored", "dishwasher_repairable"));
    public static final TagKey<Item> WASHING_MACHINE_REPAIRABLE = ItemTags.create(new ResourceLocation("furniture_restored", "washing_machine_repairable"));

    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> type(String name)
    {
        return ModRecipeTypes.REGISTER.register(name, () -> new RecipeType<>()
        {
            @Override
            public String toString()
            {
                return name;
            }
        });
    }

    public static Optional<MachineRecipe> find(RegistryObject<RecipeType<MachineRecipe>> type, Level level, ItemStack input)
    {
        if(input.isEmpty())
            return Optional.empty();
        return level.getRecipeManager().getRecipeFor(type.get(), new SimpleContainer(input), level);
    }

    public static void init() {}
}
