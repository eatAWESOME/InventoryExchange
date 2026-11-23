package eatawesome.inventoryexchange.emc.mappers.recipe;

import eatawesome.inventoryexchange.api.mapper.recipe.RecipeTypeMapper;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import net.minecraft.world.item.crafting.RecipeType;

@RecipeTypeMapper
public class VanillaRecipeTypeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_VANILLA.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_VANILLA.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_VANILLA.tooltip();
	}

	@Override
	public boolean canHandle(RecipeType<?> recipeType) {
		return recipeType == RecipeType.CRAFTING || recipeType == RecipeType.SMELTING || recipeType == RecipeType.BLASTING || recipeType == RecipeType.SMOKING
			   || recipeType == RecipeType.CAMPFIRE_COOKING || recipeType == RecipeType.STONECUTTING;
	}
}