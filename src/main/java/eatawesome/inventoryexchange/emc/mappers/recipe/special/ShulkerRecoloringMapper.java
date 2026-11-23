package eatawesome.inventoryexchange.emc.mappers.recipe.special;

import eatawesome.inventoryexchange.api.mapper.collector.IMappingCollector;
import eatawesome.inventoryexchange.api.mapper.recipe.INSSFakeGroupManager;
import eatawesome.inventoryexchange.api.mapper.recipe.RecipeTypeMapper;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import eatawesome.inventoryexchange.utils.Constants;
import eatawesome.inventoryexchange.utils.EMCHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraft.world.level.block.ShulkerBoxBlock;

@RecipeTypeMapper
public class ShulkerRecoloringMapper extends SpecialRecipeMapper<ShulkerBoxColoring> {

	@Override
	protected Class<ShulkerBoxColoring> getRecipeClass() {
		return ShulkerBoxColoring.class;
	}

	@Override
	protected boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
		NSSItem nssShulker = NSSItem.createItem(Items.SHULKER_BOX);
		for (DyeColor color : Constants.COLORS) {
			mapper.addConversion(1, NSSItem.createItem(ShulkerBoxBlock.getBlockByColor(color)), EMCHelper.intMapOf(
					nssShulker, 1,
					NSSItem.createTag(color.getTag()), 1
			));
		}
		return true;
	}

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_SHULKER_RECOLORING.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_SHULKER_RECOLORING.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_SHULKER_RECOLORING.tooltip();
	}
}