package eatawesome.inventoryexchange.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

import eatawesome.inventoryexchange.InventoryExchange;

public class InventoryExchangeCapabilities {

	private InventoryExchangeCapabilities() {
	}

	private static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(InventoryExchange.MODID, path);
	}

	/**
	 * The capability object for IAbilityEMCProvider
	 */
	public static final EntityCapability<IAbilityEMCProvider, Void> ABILITY_EMC_CAPABILITY =
		EntityCapability.createVoid(rl("ability_emc"), IAbilityEMCProvider.class);

	/**
	 * The capability object for IInfiniteContainerProvider
	 */
	public static final EntityCapability<IInfiniteContainerProvider, Void> INFINITE_CONTAINER_CAPABILITY =
		EntityCapability.createVoid(rl("infinite_container"), IInfiniteContainerProvider.class);
}