package eatawesome.inventoryexchange.client;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.INamedEntry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class InventoryExchangeItemModelProvider extends ItemModelProvider {

	public InventoryExchangeItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, InventoryExchange.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		registerGenerated(InventoryExchangeItems.ULTIMA);
		registerGenerated(InventoryExchangeItems.ULTIMA_TOOL);
		registerGenerated(InventoryExchangeItems.ULTIMA_HELMET);
		registerGenerated(InventoryExchangeItems.ULTIMA_CHESTPLATE);
		registerGenerated(InventoryExchangeItems.ULTIMA_LEGGINGS);
		registerGenerated(InventoryExchangeItems.ULTIMA_BOOTS);
		registerGenerated(InventoryExchangeItems.TOME_OF_KNOWLEDGE);
	}

	protected ResourceLocation itemTexture(INamedEntry itemProvider) {
		return modLoc("item/" + itemProvider.getName());
	}

	protected ResourceLocation itemTexture(moze_intel.projecte.gameObjs.registration.INamedEntry itemProvider) {
		return modLoc("item/" + itemProvider.getName());
	}

	protected void registerGenerated(INamedEntry... itemProviders) {
		for (INamedEntry itemProvider : itemProviders) {
			generated(itemProvider);
		}
	}

	protected void registerGenerated(moze_intel.projecte.gameObjs.registration.INamedEntry... itemProviders) {
		for (moze_intel.projecte.gameObjs.registration.INamedEntry itemProvider : itemProviders) {
			generated(itemProvider);
		}
	}

	protected ItemModelBuilder generated(INamedEntry itemProvider) {
		return generated(itemProvider, itemTexture(itemProvider));
	}

	protected ItemModelBuilder generated(moze_intel.projecte.gameObjs.registration.INamedEntry itemProvider) {
		return generated(itemProvider, itemTexture(itemProvider));
	}

	protected ItemModelBuilder generated(INamedEntry itemProvider, ResourceLocation texture) {
		return generated(itemProvider.getName(), texture);
	}

	protected ItemModelBuilder generated(moze_intel.projecte.gameObjs.registration.INamedEntry itemProvider, ResourceLocation texture) {
		return generated(itemProvider.getName(), texture);
	}

	protected ItemModelBuilder generated(String name, ResourceLocation texture) {
		return withExistingParent(name, "item/generated").texture("layer0", texture);
	}

	protected ItemModelBuilder handheld(INamedEntry itemProvider, ResourceLocation texture) {
		return handheld(itemProvider.getName(), texture);
	}

	protected ItemModelBuilder handheld(String name, ResourceLocation texture) {
		return withExistingParent(name, "item/handheld").texture("layer0", texture);
	}
}
