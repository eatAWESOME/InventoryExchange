package eatawesome.inventoryexchange.common.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.InventoryExchangeTags;

public class InventoryExchangePotionsTagsProvider extends TagsProvider<Potion> {

	public InventoryExchangePotionsTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.POTION, lookupProvider, InventoryExchange.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		tag(InventoryExchangeTags.Potions.IGNORE_MISSING_EMC).add(
				Potions.LUCK.getKey()
		);
	}

	@NotNull
	@Override
	public String getName() {
		return "Potion Tags";
	}
}