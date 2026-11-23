package eatawesome.inventoryexchange.emc.mappers.recipe.special;

import eatawesome.inventoryexchange.api.mapper.collector.IMappingCollector;
import eatawesome.inventoryexchange.api.mapper.recipe.INSSFakeGroupManager;
import eatawesome.inventoryexchange.api.mapper.recipe.IRecipeTypeMapper;
import eatawesome.inventoryexchange.api.mapper.recipe.RecipeTypeMapper;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.config.EMCMappingConfig;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import eatawesome.inventoryexchange.emc.components.processor.DamageProcessor;
import eatawesome.inventoryexchange.emc.components.processor.DecoratedPotProcessor;
import eatawesome.inventoryexchange.emc.components.processor.DecoratedShieldProcessor;
import eatawesome.inventoryexchange.emc.components.processor.FireworkProcessor;
import eatawesome.inventoryexchange.emc.components.processor.FireworkStarProcessor;
import eatawesome.inventoryexchange.emc.components.processor.MapScaleProcessor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.FireworkStarFadeRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.MapCloningRecipe;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;

@RecipeTypeMapper
public class SpecialRecipeMarkHandledMapper implements IRecipeTypeMapper {

	@Override
	public final boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RecipeHolder<?> recipeHolder, RegistryAccess registryAccess,
			INSSFakeGroupManager fakeGroupManager) {
		Recipe<?> recipe = recipeHolder.value();
		if (recipe instanceof CustomRecipe) {
			if (recipe instanceof ShieldDecorationRecipe) {
				return EMCMappingConfig.isEnabled(DecoratedShieldProcessor.INSTANCE);
			} else if (recipe instanceof DecoratedPotRecipe) {
				return EMCMappingConfig.isEnabled(DecoratedPotProcessor.INSTANCE);
			} else if (recipe instanceof RepairItemRecipe) {
				return EMCMappingConfig.isEnabled(DamageProcessor.INSTANCE);
			} else if (recipe instanceof FireworkStarRecipe || recipe instanceof FireworkStarFadeRecipe) {
				return EMCMappingConfig.isEnabled(FireworkStarProcessor.INSTANCE);
			} else if (recipe instanceof FireworkRocketRecipe) {
				return EMCMappingConfig.isEnabled(FireworkProcessor.INSTANCE);
			}
			return recipe instanceof BookCloningRecipe || recipe instanceof MapCloningRecipe || recipe instanceof BannerDuplicateRecipe;
		} else if (recipe instanceof MapExtendingRecipe) {
			return EMCMappingConfig.isEnabled(MapScaleProcessor.INSTANCE);
		}
		return false;
	}

	@Override
	public final boolean canHandle(RecipeType<?> recipeType) {
		return recipeType == RecipeType.CRAFTING;
	}

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_MARK_HANDLED.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_MARK_HANDLED.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_MARK_HANDLED.tooltip();
	}
}