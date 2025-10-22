package eatawesome.inventoryexchange.gameObjs.registries;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredHolder;
import eatawesome.inventoryexchange.gameObjs.registration.impl.CreativeTabDeferredRegister;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class InventoryExchangeCreativeTabs {

	public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(InventoryExchange.MODID, InventoryExchangeCreativeTabs::addToExistingTabs);

	public static final InventoryExchangeDeferredHolder<CreativeModeTab, CreativeModeTab> INVENTORYEXCHANGE = CREATIVE_TABS.registerMain(InventoryExchangeLang.INVENTORYEXCHANGE, InventoryExchangeItems.ULTIMA, builder ->
			builder.displayItems((displayParameters, output) -> {
				output.accept(InventoryExchangeItems.ULTIMA);
				output.accept(InventoryExchangeItems.ULTIMA_TOOL);
				output.accept(InventoryExchangeItems.ULTIMA_HELMET);
				output.accept(InventoryExchangeItems.ULTIMA_CHESTPLATE);
				output.accept(InventoryExchangeItems.ULTIMA_LEGGINGS);
				output.accept(InventoryExchangeItems.ULTIMA_BOOTS);
				output.accept(PEItems.TOME_OF_KNOWLEDGE);
			})
	);

	private static void addToExistingTabs(BuildCreativeModeTabContentsEvent event) {
		ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
		if (tabKey == CreativeModeTabs.BUILDING_BLOCKS) {
		} else if (tabKey == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
		} else if (tabKey == CreativeModeTabs.REDSTONE_BLOCKS) {
		} else if (tabKey == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			addToExistingTab(event,
					PEItems.TOME_OF_KNOWLEDGE,
					InventoryExchangeItems.ULTIMA_TOOL
			);
		} else if (tabKey == CreativeModeTabs.COMBAT) {
			addToExistingTab(event,
					InventoryExchangeItems.ULTIMA_HELMET,
					InventoryExchangeItems.ULTIMA_CHESTPLATE,
					InventoryExchangeItems.ULTIMA_LEGGINGS,
					InventoryExchangeItems.ULTIMA_BOOTS
			);
		} else if (tabKey == CreativeModeTabs.INGREDIENTS) {
			addToExistingTab(event,
					InventoryExchangeItems.ULTIMA
			);
		}
	}

	private static void addToExistingTab(BuildCreativeModeTabContentsEvent event, ItemLike... items) {
		for (ItemLike item : items) {
			event.accept(item);
		}
	}
}