package eatawesome.inventoryexchange.integration.recipe_viewer;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public class RecipeViewerHelper {

	private RecipeViewerHelper() {
	}

	public static String stripForSynthetic(Holder<?> holder) {
		ResourceKey<?> key = holder.getKey();
		return key == null ? "unregistered" : key.location().toString().replace(':', '_');
	}
}