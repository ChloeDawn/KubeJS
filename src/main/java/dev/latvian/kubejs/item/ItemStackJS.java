package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.OreDictionaryIngredientJS;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author LatvianModder
 */
public abstract class ItemStackJS implements IngredientJS
{
	public static ItemStackJS of(@Nullable Object o)
	{
		if (o == null)
		{
			return EmptyItemStackJS.INSTANCE;
		}
		else if (o instanceof ItemStackJS)
		{
			return (ItemStackJS) o;
		}
		else if (o instanceof IngredientJS)
		{
			return ((IngredientJS) o).getFirst();
		}
		else if (o instanceof ItemStack)
		{
			ItemStack stack = (ItemStack) o;
			return stack.isEmpty() ? EmptyItemStackJS.INSTANCE : new BoundItemStackJS(stack);
		}
		else if (o instanceof JSObject)
		{
			JSObject js = (JSObject) o;

			if (js.getMember("item") instanceof String)
			{
				Item item = Item.REGISTRY.getObject(new ResourceLocation(KubeJS.appendModId(js.getMember("item").toString())));

				if (item != null && item != Items.AIR)
				{
					ItemStackJS stack = new UnboundItemStackJS(item);

					if (js.getMember("count") instanceof Number)
					{
						stack.count(((Number) js.getMember("count")).intValue());
					}

					if (js.getMember("data") instanceof Number)
					{
						stack.data(((Number) js.getMember("data")).intValue());
					}

					if (js.hasMember("nbt"))
					{
						stack.nbt(js.getMember("nbt"));
					}

					return stack;
				}

				return EmptyItemStackJS.INSTANCE;
			}
			else if (js.getMember("ore") instanceof String)
			{
				return new OreDictionaryIngredientJS(js.getMember("ore").toString()).getFirst();
			}
		}

		String s0 = String.valueOf(o).trim();

		if (s0.isEmpty() || s0.equals("air"))
		{
			return EmptyItemStackJS.INSTANCE;
		}

		String[] s = s0.split("\\s", 4);
		ResourceLocation id = new ResourceLocation(KubeJS.appendModId(s[0]));

		if (id.getNamespace().equals("ore"))
		{
			return new OreDictionaryIngredientJS(id.getPath()).getFirst();
		}

		Item item = Item.REGISTRY.getObject(id);

		if (item != null && item != Items.AIR)
		{
			ItemStackJS stack = new UnboundItemStackJS(item);

			if (s.length >= 2)
			{
				stack.count(Integer.parseInt(s[1]));
			}

			if (s.length >= 3)
			{
				stack.data(Integer.parseInt(s[2]));
			}

			if (s.length >= 4)
			{
				stack.nbt(s[3]);
			}

			return stack;
		}

		return EmptyItemStackJS.INSTANCE;
	}

	public static List<ItemStackJS> list()
	{
		List<ItemStackJS> list = new ArrayList<>();
		NonNullList<ItemStack> stackList = NonNullList.create();

		for (Item item : Item.REGISTRY)
		{
			item.getSubItems(CreativeTabs.SEARCH, stackList);

			for (ItemStack stack : stackList)
			{
				list.add(new BoundItemStackJS(stack));
			}

			stackList.clear();
		}

		return list;
	}

	public abstract Item item();

	public ID id()
	{
		return new ID(item().getRegistryName());
	}

	public abstract ItemStackJS copy();

	public abstract ItemStackJS count(int c);

	public abstract int count();

	@Override
	public boolean isEmpty()
	{
		return count() <= 0;
	}

	public abstract ItemStackJS data(int d);

	public abstract int data();

	public abstract ItemStackJS nbt(@Nullable Object o);

	public abstract NBTCompoundJS nbt();

	public abstract ItemStackJS caps(@Nullable Object o);

	public abstract NBTCompoundJS caps();

	public abstract ItemStack itemStack();

	public NBTCompoundJS nbtOrNew()
	{
		NBTCompoundJS nbt = nbt();

		if (nbt.isNull())
		{
			nbt = new NBTCompoundJS();
			nbt(nbt);
		}

		return nbt;
	}

	public ItemStackJS grow(int c)
	{
		return count(count() + c);
	}

	public ItemStackJS shrink(int c)
	{
		return grow(-c);
	}

	public ItemStackJS name(String displayName)
	{
		return nbt(nbtOrNew().compoundOrNew("display").set("Name", displayName));
	}

	public String name()
	{
		return itemStack().getDisplayName();
	}

	public String toString()
	{
		NBTCompoundJS out = new NBTCompoundJS();
		out.set("item", id());

		if (count() > 1)
		{
			out.set("count", count());
		}

		if (item().getHasSubtypes())
		{
			out.set("data", data());
		}

		if (!nbt().isNull())
		{
			out.set("nbt", nbt());
		}

		if (!caps().isNull())
		{
			out.set("caps", caps());
		}

		return out.toString();
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return item() == stack.item() && (data() == 32767 || data() == stack.data()) && Objects.equals(nbt(), stack.nbt());
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return Collections.singleton(this);
	}

	@Override
	public ItemStackJS getFirst()
	{
		return this;
	}

	@Override
	public Ingredient createVanillaIngredient()
	{
		return Ingredient.fromStacks(itemStack());
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ItemStackJS)
		{
			ItemStackJS stack = (ItemStackJS) obj;
			return item() == stack.item() && data() == stack.data() && Objects.equals(nbt(), stack.nbt());
		}

		return false;
	}
}