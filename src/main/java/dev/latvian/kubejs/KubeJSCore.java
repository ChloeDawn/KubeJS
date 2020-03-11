package dev.latvian.kubejs;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSCore
{
	// Access Transformers //

	public static List<IFutureReloadListener> getReloadListeners(SimpleReloadableResourceManager manager)
	{
		return manager.reloadListeners;
	}

	public static List<IFutureReloadListener> getInitTaskQueue(SimpleReloadableResourceManager manager)
	{
		return manager.initTaskQueue;
	}

	public static Map<String, FallbackResourceManager> getNamespaceResourceManagers(SimpleReloadableResourceManager manager)
	{
		return manager.namespaceResourceManagers;
	}

	public static <T> void setTagMap(TagCollection<T> collection, Map<ResourceLocation, Tag<T>> m)
	{
		collection.tagMap = m;
	}

	public static <T> void setTaggedItems(Tag.ListEntry<T> tag, Collection<T> collection)
	{
		tag.taggedItems = collection;
	}

	public static <T> void setEntries(Tag<T> tag, Collection<Tag.ITagEntry<T>> collection)
	{
		tag.entries = collection;
	}

	public static Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> getRecipes(RecipeManager manager)
	{
		return manager.recipes;
	}

	public static void setLifeTime(FireworkRocketEntity rocket, int lifeTime)
	{
		rocket.lifetime = lifeTime;
	}

	public static boolean isDestroyingBlock(PlayerInteractionManager manager)
	{
		return manager.isDestroyingBlock;
	}

	public static Map<String, INBT> getNBTTagMap(CompoundNBT nbt)
	{
		return nbt.tagMap;
	}

	public static void setNBTTagMap(CompoundNBT nbt, Map<String, INBT> map)
	{
		nbt.tagMap = map;
	}

	public static void setHardness(Block block, float hardness)
	{
		block.blockHardness = hardness;
	}

	public static void setResistance(Block block, float resistance)
	{
		block.blockResistance = resistance;
	}

	public static void setLightLevel(Block block, int lightLevel)
	{
		block.lightValue = lightLevel;
	}

	// Mixin Helpers //

	public static void customRecipes(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> map, IResourceManager resourceManager, IProfiler profiler)
	{
		//KubeJS.LOGGER.info("Custom recipes! " + getRecipes(recipeManager));
	}
}