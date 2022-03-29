package dev.latvian.mods.kubejs.fluid;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.KubeJSItemEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;

public class FluidBucketItemBuilder extends ItemBuilder {
	public final FluidBuilder fluidBuilder;

	public FluidBucketItemBuilder(FluidBuilder b) {
		super(b.newID("", "_bucket"));
		fluidBuilder = b;
		maxStackSize(1);
	}

	@Override
	public Item createObject() {
		return KubeJSItemEventHandler.buildBucket(fluidBuilder, this);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void clientRegistry(Minecraft minecraft) {
		if (fluidBuilder.bucketColor != 0xFFFFFFFF) {
			ColorHandlerRegistry.registerItemColors((stack, index) -> index == 1 ? fluidBuilder.bucketColor : 0xFFFFFFFF, this);
		}
	}
}
