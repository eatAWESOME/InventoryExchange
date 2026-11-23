package eatawesome.inventoryexchange.gameObjs;

import eatawesome.inventoryexchange.InventoryExchange;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

public class InventoryExchangeTags {

	private InventoryExchangeTags() {
	}

	public static class Items {

		private Items() {
		}

		public static final TagKey<Item> MINERALS_PLUS = tag("minerals_plus");
		public static final TagKey<Item> NO_INVENTORY_CONDENSING = tag("no_inventory_condensing");
		
		// Items in this tag won't be listed by the dump missing emc command even if they are missing emc.
		public static final TagKey<Item> IGNORE_MISSING_EMC = tag("ignore_missing_emc");
		
		private static TagKey<Item> tag(String name) {
			return ItemTags.create(InventoryExchange.rl(name));
		}
	}
	
	public static class Potions {

		private Potions() {
		}

		// Items that contain potions in this tag won't be listed by the dump missing emc command even if they are missing emc.
		public static final TagKey<Potion> IGNORE_MISSING_EMC = tag("ignore_missing_emc");

		private static TagKey<Potion> tag(String name) {
			return TagKey.create(Registries.POTION, InventoryExchange.rl(name));
		}
	}
}