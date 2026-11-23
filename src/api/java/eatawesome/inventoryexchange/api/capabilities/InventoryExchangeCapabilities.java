package eatawesome.inventoryexchange.api.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

import eatawesome.inventoryexchange.api.InventoryExchangeAPI;

public class InventoryExchangeCapabilities {

	private InventoryExchangeCapabilities() {
	}

	private static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(InventoryExchangeAPI.MODID, path);
	}
	
	public static final EntityCapability<IKnowledgeProvider, Void> KNOWLEDGE_CAPABILITY = EntityCapability.createVoid(rl("knowledge"), IKnowledgeProvider.class);

	public static final EntityCapability<IAbilityEMCProvider, Void> ABILITY_EMC_CAPABILITY = EntityCapability.createVoid(rl("ability_emc"), IAbilityEMCProvider.class);

	public static final EntityCapability<IInfiniteContainerProvider, Void> INFINITE_CONTAINER_CAPABILITY = EntityCapability.createVoid(rl("infinite_container"), IInfiniteContainerProvider.class);
	
	public static final EntityCapability<IPortableFurnaceProvider, Void> PORTABLE_FURNACE_CAPABILITY = EntityCapability.createVoid(rl("portable_furnace"), IPortableFurnaceProvider.class);
}