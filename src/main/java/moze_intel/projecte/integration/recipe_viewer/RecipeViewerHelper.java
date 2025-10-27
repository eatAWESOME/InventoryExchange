package moze_intel.projecte.integration.recipe_viewer;

import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public class RecipeViewerHelper {

	private RecipeViewerHelper() {
	}

	public static String stripForSynthetic(Holder<?> holder) {
		ResourceKey<?> key = holder.getKey();
		return key == null ? "unregistered" : key.location().toString().replace(':', '_');
	}

	public static Component getTransmuteDescription() {
		return PELang.WORLD_TRANSMUTE_DESCRIPTION.translate(Component.keybind("key.use"), Component.keybind("key.sneak"));
	}
}