package moze_intel.projecte.integration.recipe_viewer.jei;

import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class PEJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = PECore.rl("main");

	private static final ISubtypeInterpreter<ItemStack> PROJECTE_INTERPRETER = new ISubtypeInterpreter<>() {
		@Nullable
		@Override
		public Object getSubtypeData(@NotNull ItemStack stack, @NotNull UidContext context) {
			if (context == UidContext.Ingredient) {
				Object mode = null;
				Long stored = stack.get(PEDataComponentTypes.STORED_EMC);
				if (stored != null && stored > 0) {
					return mode == null ? stored : List.of(mode, stored);
				}
				return mode;
			}
			return null;
		}

		@NotNull
		@Override
		public String getLegacyStringSubtypeInfo(@NotNull ItemStack stack, @NotNull UidContext context) {
			if (context == UidContext.Ingredient) {
				Object mode = null;
				Long stored = stack.get(PEDataComponentTypes.STORED_EMC);
				if (stored != null && stored > 0) {
					return mode == null ? stored.toString() : mode + ";" + stored;
				}
			}
			return "";
		}
	};

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}
}