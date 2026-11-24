package eatawesome.inventoryexchange.api;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class InventoryExchangeSounds {

	public static final Holder<SoundEvent> FLIGHT_START = get("flight_start");
	public static final Holder<SoundEvent> FLIGHT_END = get("flight_end");
	public static final Holder<SoundEvent> VEIN_MINE = get("vein_mine");

	private InventoryExchangeSounds() {
	}

	private static Holder<SoundEvent> get(String name) {
		return DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.fromNamespaceAndPath(InventoryExchangeAPI.MODID, name));
	}
}