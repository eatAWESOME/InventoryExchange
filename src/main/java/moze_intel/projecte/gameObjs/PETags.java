package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

public class PETags {

	private PETags() {
	}

	public static class Items {

		private Items() {
		}
		/**
		 * Items in this tag won't be listed by the dump missing emc command even if they are missing emc.
		 */
		public static final TagKey<Item> IGNORE_MISSING_EMC = tag("ignore_missing_emc");
		
		private static TagKey<Item> tag(String name) {
			return ItemTags.create(PECore.rl(name));
		}
	}

	public static class Potions {

		private Potions() {
		}

		/**
		 * Items that contain potions in this tag won't be listed by the dump missing emc command even if they are missing emc.
		 */
		public static final TagKey<Potion> IGNORE_MISSING_EMC = tag("ignore_missing_emc");

		private static TagKey<Potion> tag(String name) {
			return TagKey.create(Registries.POTION, PECore.rl(name));
		}
	}
}