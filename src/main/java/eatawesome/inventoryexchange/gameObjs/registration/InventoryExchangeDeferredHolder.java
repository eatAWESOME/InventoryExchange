package eatawesome.inventoryexchange.gameObjs.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class InventoryExchangeDeferredHolder<R, T extends R> extends DeferredHolder<R, T> implements INamedEntry {

	public InventoryExchangeDeferredHolder(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName) {
		this(ResourceKey.create(registryKey, valueName));
	}

	public InventoryExchangeDeferredHolder(ResourceKey<R> key) {
		super(key);
	}

	@Override
	public String getName() {
		return getId().getPath();
	}
}