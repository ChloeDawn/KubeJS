package dev.latvian.mods.kubejs.level;

import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.block.MaterialJS;
import dev.latvian.mods.kubejs.block.MaterialListJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.player.ServerPlayerJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockContainerJS implements SpecialEquality {
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	public final Level minecraftLevel;
	private final BlockPos pos;

	private BlockState cachedState;
	private BlockEntity cachedEntity;

	public BlockContainerJS(Level w, BlockPos p) {
		minecraftLevel = w;
		pos = p;
	}

	public void clearCache() {
		cachedState = null;
		cachedEntity = null;
	}

	public LevelJS getLevel() {
		return UtilsJS.getLevel(minecraftLevel);
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.2")
	public final LevelJS getWorld() {
		return getLevel();
	}

	public BlockPos getPos() {
		return pos;
	}

	public String getDimension() {
		return minecraftLevel.dimension().location().toString();
	}

	public int getX() {
		return getPos().getX();
	}

	public int getY() {
		return getPos().getY();
	}

	public int getZ() {
		return getPos().getZ();
	}

	public BlockContainerJS offset(Direction f, int d) {
		return new BlockContainerJS(minecraftLevel, getPos().relative(f, d));
	}

	public BlockContainerJS offset(Direction f) {
		return offset(f, 1);
	}

	public BlockContainerJS offset(int x, int y, int z) {
		return new BlockContainerJS(minecraftLevel, getPos().offset(x, y, z));
	}

	public BlockContainerJS getDown() {
		return offset(Direction.DOWN);
	}

	public BlockContainerJS getUp() {
		return offset(Direction.UP);
	}

	public BlockContainerJS getNorth() {
		return offset(Direction.NORTH);
	}

	public BlockContainerJS getSouth() {
		return offset(Direction.SOUTH);
	}

	public BlockContainerJS getWest() {
		return offset(Direction.WEST);
	}

	public BlockContainerJS getEast() {
		return offset(Direction.EAST);
	}

	public BlockState getBlockState() {
		if (cachedState == null) {
			cachedState = minecraftLevel.getBlockState(getPos());
		}

		return cachedState;
	}

	public void setBlockState(BlockState state, int flags) {
		minecraftLevel.setBlock(getPos(), state, flags);
		clearCache();
	}

	public String getId() {
		return Registries.getId(getBlockState().getBlock(), Registry.BLOCK_REGISTRY).toString();
	}

	public Collection<ResourceLocation> getTags() {
		return Tags.byBlockState(getBlockState());
	}

	public boolean hasTag(ResourceLocation tag) {
		return Tags.blocks().getTagOrEmpty(tag).contains(getBlockState().getBlock());
	}

	public void set(ResourceLocation id, Map<?, ?> properties, int flags) {
		var block = KubeJSRegistries.blocks().get(id);
		var state = block.defaultBlockState();

		if (!properties.isEmpty() && state.getBlock() != Blocks.AIR) {
			Map<String, Property<?>> pmap = new HashMap<>();

			for (var property : state.getProperties()) {
				pmap.put(property.getName(), property);
			}

			for (var entry : properties.entrySet()) {
				var property = pmap.get(String.valueOf(entry.getKey()));

				if (property != null) {
					state = state.setValue(property, UtilsJS.cast(property.getValue(String.valueOf(entry.getValue())).get()));
				}
			}
		}

		setBlockState(state, flags);
	}

	public void set(ResourceLocation id, Map<?, ?> properties) {
		set(id, properties, 3);
	}

	public void set(ResourceLocation id) {
		set(id, Collections.emptyMap());
	}

	public Map<String, String> getProperties() {
		Map<String, String> map = new HashMap<>();
		var state = getBlockState();

		for (Property property : state.getProperties()) {
			map.put(property.getName(), property.getName(state.getValue(property)));
		}

		return map;
	}

	@Nullable
	public BlockEntity getEntity() {
		if (cachedEntity == null || cachedEntity.isRemoved()) {
			cachedEntity = minecraftLevel.getBlockEntity(pos);
		}

		return cachedEntity;
	}

	public String getEntityId() {
		var entity = getEntity();
		return entity == null ? "minecraft:air" : Registries.getId(entity.getType(), Registry.BLOCK_ENTITY_TYPE_REGISTRY).toString();
	}

	@Nullable
	public CompoundTag getEntityData() {
		var entity = getEntity();

		if (entity != null) {
			return entity.saveWithFullMetadata();
		}

		return null;
	}

	public void setEntityData(@Nullable CompoundTag tag) {
		if (tag != null) {
			var entity = getEntity();

			if (entity != null) {
				entity.load(tag);
			}
		}
	}

	public void mergeEntityData(@Nullable CompoundTag tag) {
		var t = getEntityData();

		if (t == null) {
			setEntityData(tag);
		} else if (tag != null && !tag.isEmpty()) {
			for (var s : tag.getAllKeys()) {
				t.put(s, tag.get(s));
			}
		}

		setEntityData(t);
	}

	public int getLight() {
		return minecraftLevel.getMaxLocalRawBrightness(pos);
	}

	public boolean getCanSeeSky() {
		return minecraftLevel.canSeeSkyFromBelowWater(pos);
	}

	@Override
	public String toString() {
		var id = getId();
		var properties = getProperties();

		if (properties.isEmpty()) {
			return id;
		}

		var builder = new StringBuilder(id);
		builder.append('[');

		var first = true;

		for (var entry : properties.entrySet()) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}

			builder.append(entry.getKey());
			builder.append('=');
			builder.append(entry.getValue());
		}

		builder.append(']');
		return builder.toString();
	}

	public ExplosionJS createExplosion() {
		return new ExplosionJS(minecraftLevel, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D);
	}

	@Nullable
	public EntityJS createEntity(ResourceLocation id) {
		var entity = getLevel().createEntity(id);

		if (entity != null) {
			entity.setPosition(this);
		}

		return entity;
	}

	public void spawnLightning(boolean effectOnly, @Nullable EntityJS player) {
		if (minecraftLevel instanceof ServerLevel) {
			var e = EntityType.LIGHTNING_BOLT.create(minecraftLevel);
			e.moveTo(getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D);
			e.setCause(player instanceof ServerPlayerJS serverPlayer ? serverPlayer.minecraftPlayer : null);
			e.setVisualOnly(effectOnly);
			minecraftLevel.addFreshEntity(e);
		}
	}

	public void spawnLightning(boolean effectOnly) {
		spawnLightning(effectOnly, null);
	}

	public void spawnFireworks(FireworksJS fireworks) {
		minecraftLevel.addFreshEntity(fireworks.createFireworkRocket(minecraftLevel, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D));
	}

	@Nullable
	public InventoryJS getInventory(Direction facing) {
		var tileEntity = getEntity();

		if (tileEntity != null) {
			return getInventoryFromBlockEntity(tileEntity, facing);
		}

		return null;
	}

	@ExpectPlatform
	private static InventoryJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing) {
		throw new AssertionError();
	}

	public MaterialJS getMaterial() {
		return MaterialListJS.INSTANCE.get(getBlockState().getMaterial());
	}

	public ItemStackJS getItem() {
		var state = getBlockState();
		return ItemStackJS.of(state.getBlock().getCloneItemStack(minecraftLevel, pos, state));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof CharSequence || obj instanceof ResourceLocation) {
			return getId().equals(obj.toString());
		}

		return super.equals(obj);
	}

	private static boolean isReal(Player p) {
		return !PlayerHooks.isFake(p);
	}

	public EntityArrayList getPlayersInRadius(double radius) {
		var list = new EntityArrayList(getLevel(), 1);

		for (var player : minecraftLevel.getEntitiesOfClass(Player.class, new AABB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + 1D + radius, pos.getY() + 1D + radius, pos.getZ() + 1D + radius), BlockContainerJS::isReal)) {
			PlayerJS<?> p = getLevel().getPlayer(player);

			if (p != null) {
				list.add(p);
			}
		}

		return list;
	}

	public EntityArrayList getPlayersInRadius() {
		return getPlayersInRadius(8D);
	}

	public String getBiomeId() {
		var key = minecraftLevel.getBiomeName(pos);
		return key.isPresent() ? key.get().location().toString() : "";
	}

	@Override
	public boolean specialEquals(Object o, boolean shallow) {
		if (o instanceof CharSequence || o instanceof ResourceLocation) {
			return getId().equals(o.toString());
		}

		return equals(o);
	}
}