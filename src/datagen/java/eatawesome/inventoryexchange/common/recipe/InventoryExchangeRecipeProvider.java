package eatawesome.inventoryexchange.common.recipe;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class InventoryExchangeRecipeProvider extends RecipeProvider {

	private final ExistingFileHelper existingFileHelper;

	public InventoryExchangeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
		super(output, registries);
		this.existingFileHelper = existingFileHelper;
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		buildRecipesInternal(new RecipeOutput() {
			@NotNull
			@Override
			public Advancement.Builder advancement() {
				return recipeOutput.advancement();
			}

			@Override
			public void accept(@NotNull ResourceLocation recipeId, @NotNull Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder, ICondition @NotNull ... conditions) {
				recipeOutput.accept(recipeId, recipe, advancementHolder, conditions);
				existingFileHelper.trackGenerated(recipeId, PackType.SERVER_DATA, ".json", "recipes");
			}
		});
	}

	private void buildRecipesInternal(@NotNull RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, InventoryExchangeItems.ULTIMA.get())
			.define('B', Items.DIAMOND_BLOCK)
			.define('D', Items.DIAMOND)
			.define('O', Items.OBSIDIAN)
			.pattern("DOD")
			.pattern("OBO")
			.pattern("DOD")
			.unlockedBy(getHasName(Items.DIAMOND_BLOCK), has(Items.DIAMOND_BLOCK))
			.unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
			.unlockedBy(getHasName(Items.OBSIDIAN), has(Items.OBSIDIAN))
			.save(recipeOutput);
		
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, InventoryExchangeItems.ULTIMA_TOOL.get())
			.define('D', Items.DIAMOND)
			.define('U', InventoryExchangeItems.ULTIMA.get())
			.pattern("UUU")
			.pattern("UDU")
			.pattern(" D ")
			.unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
			.unlockedBy(getHasName(InventoryExchangeItems.ULTIMA.get()), has(InventoryExchangeItems.ULTIMA.get()))
			.save(recipeOutput);
		
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InventoryExchangeItems.ULTIMA_HELMET.get())
			.define('U', InventoryExchangeItems.ULTIMA.get())
			.pattern("UUU")
			.pattern("U U")
			.unlockedBy(getHasName(InventoryExchangeItems.ULTIMA.get()), has(InventoryExchangeItems.ULTIMA.get()))
			.save(recipeOutput);
	
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InventoryExchangeItems.ULTIMA_CHESTPLATE.get())
			.define('U', InventoryExchangeItems.ULTIMA.get())
			.pattern("U U")
			.pattern("UUU")
			.pattern("UUU")
			.unlockedBy(getHasName(InventoryExchangeItems.ULTIMA.get()), has(InventoryExchangeItems.ULTIMA.get()))
			.save(recipeOutput);
	
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InventoryExchangeItems.ULTIMA_LEGGINGS.get())
			.define('U', InventoryExchangeItems.ULTIMA.get())
			.pattern("UUU")
			.pattern("U U")
			.pattern("U U")
			.unlockedBy(getHasName(InventoryExchangeItems.ULTIMA.get()), has(InventoryExchangeItems.ULTIMA.get()))
			.save(recipeOutput);
	
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InventoryExchangeItems.ULTIMA_BOOTS.get())
			.define('U', InventoryExchangeItems.ULTIMA.get())
			.pattern("U U")
			.pattern("U U")
			.unlockedBy(getHasName(InventoryExchangeItems.ULTIMA.get()), has(InventoryExchangeItems.ULTIMA.get()))
			.save(recipeOutput);
		
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, InventoryExchangeItems.ULTIMA_WATCH.get())
			.define('C', Items.CLOCK)
			.define('U', InventoryExchangeItems.ULTIMA.get())
			.pattern(" U ")
			.pattern("UCU")
			.pattern(" U ")
			.unlockedBy(getHasName(Items.CLOCK), has(Items.CLOCK))
			.unlockedBy(getHasName(InventoryExchangeItems.ULTIMA.get()), has(InventoryExchangeItems.ULTIMA.get()))
			.save(recipeOutput);
	}
}