package dev.latvian.mods.kubejs.recipe.fabric;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.function.Consumer;

public class RecipeEventJSImpl {
	public static boolean processConditions(RecipeManager recipeManager, JsonObject json, String key) {
		return true;
	}

	public static void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
		FabricLoader.getInstance().getEntrypoints("kubejs-set-recipes", Consumer.class).forEach(consumer -> consumer.accept(map));
	}
}
