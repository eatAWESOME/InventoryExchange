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
		register(InventoryExchangeItems.ULTIMA);
		register(InventoryExchangeItems.ULTIMA_TOOL, "handheld");
		register(InventoryExchangeItems.ULTIMA_HELMET);
		register(InventoryExchangeItems.ULTIMA_CHESTPLATE);
		register(InventoryExchangeItems.ULTIMA_LEGGINGS);
		register(InventoryExchangeItems.ULTIMA_BOOTS);
		register(InventoryExchangeItems.TOME_OF_KNOWLEDGE);
	}

	protected ResourceLocation itemTexture(INamedEntry itemProvider) {
		return modLoc("item/" + itemProvider.getName());
	}

	protected ResourceLocation itemTexture(moze_intel.projecte.gameObjs.registration.INamedEntry itemProvider) {
		return modLoc("item/" + itemProvider.getName());
	}

	protected void register(INamedEntry... itemProviders) {
		for (INamedEntry itemProvider : itemProviders) {
			register(itemProvider);
		}
	}

	protected void register(moze_intel.projecte.gameObjs.registration.INamedEntry... itemProviders) {
		for (moze_intel.projecte.gameObjs.registration.INamedEntry itemProvider : itemProviders) {
			register(itemProvider);
		}
	}
	
	protected ItemModelBuilder register(INamedEntry itemProvider, String type) {
		return withExistingParent(itemProvider.getName(), "item/" + type).texture("layer0", itemTexture(itemProvider));
	}
	
	protected ItemModelBuilder register(INamedEntry itemProvider) {
		return withExistingParent(itemProvider.getName(), "item/generated").texture("layer0", itemTexture(itemProvider));
	}

	protected ItemModelBuilder register(moze_intel.projecte.gameObjs.registration.INamedEntry itemProvider) {
		return withExistingParent(itemProvider.getName(), "item/generated").texture("layer0", itemTexture(itemProvider));
	}
}
