package eatawesome.inventoryexchange.emc.mappers.recipe.special;

import java.util.List;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.mapper.collector.IMappingCollector;
import eatawesome.inventoryexchange.api.mapper.recipe.INSSFakeGroupManager;
import eatawesome.inventoryexchange.api.mapper.recipe.RecipeTypeMapper;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.config.InventoryExchangeConfigTranslations;
import eatawesome.inventoryexchange.utils.EMCHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.TippedArrowRecipe;

@RecipeTypeMapper
public class TippedArrowMapper extends SpecialRecipeMapper<TippedArrowRecipe> {

	private static final ResourceLocation TIPPED_ARROW = BuiltInRegistries.ITEM.getKey(Items.TIPPED_ARROW);
	private static final ResourceLocation LINGERING_POTION = BuiltInRegistries.ITEM.getKey(Items.LINGERING_POTION);

	@Override
	protected Class<TippedArrowRecipe> getRecipeClass() {
		return TippedArrowRecipe.class;
	}

	@Override
	protected boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
		NSSItem nssArrow = NSSItem.createItem(Items.ARROW);
		List<Holder.Reference<Potion>> potions = BuiltInRegistries.POTION.holders().toList();
		for (Holder<Potion> potionType : potions) {
			PotionContents potionContents = new PotionContents(potionType);
			mapper.addConversion(8, createItem(TIPPED_ARROW, potionContents), EMCHelper.intMapOf(
					nssArrow, 8,
					createItem(LINGERING_POTION, potionContents), 1
			));
		}
		InventoryExchange.debugLog("{} Statistics:", getName());
		InventoryExchange.debugLog("Found {} Tipped Arrow Recipes", potions.size());
		return true;
	}

	private NSSItem createItem(ResourceLocation item, PotionContents contents) {
		return NSSItem.createItem(item, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, contents).build());
	}

	@Override
	public String getName() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_TIPPED_ARROW.title();
	}

	@Override
	public String getTranslationKey() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_TIPPED_ARROW.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return InventoryExchangeConfigTranslations.MAPPING_CRAFTING_MAPPER_TIPPED_ARROW.tooltip();
	}
}