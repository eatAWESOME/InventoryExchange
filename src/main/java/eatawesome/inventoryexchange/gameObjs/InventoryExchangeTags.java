package eatawesome.inventoryexchange.gameObjs;

import eatawesome.inventoryexchange.InventoryExchange;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class InventoryExchangeTags {

	private InventoryExchangeTags() {
	}

	public static class Items {

		private Items() {
		}

		public static final TagKey<Item> MINERALS_PLUS = tag("minerals_plus");
		public static final TagKey<Item> NO_INVENTORY_CONDENSING = tag("no_inventory_condensing");
		
		private static TagKey<Item> tag(String name) {
			return ItemTags.create(InventoryExchange.rl(name));
		}
	}
}