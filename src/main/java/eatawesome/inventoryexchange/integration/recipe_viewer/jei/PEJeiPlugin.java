package eatawesome.inventoryexchange.integration.recipe_viewer.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eatawesome.inventoryexchange.InventoryExchange;

@JeiPlugin
public class PEJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = InventoryExchange.rl("main");

	private static final ISubtypeInterpreter<ItemStack> INVENTORYEXCHANGE_INTERPRETER = new ISubtypeInterpreter<>() {
		@Nullable
		@Override
		public Object getSubtypeData(@NotNull ItemStack stack, @NotNull UidContext context) {
			return null;
		}

		@NotNull
		@Override
		public String getLegacyStringSubtypeInfo(@NotNull ItemStack stack, @NotNull UidContext context) {
			return "";
		}
	};

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}
}