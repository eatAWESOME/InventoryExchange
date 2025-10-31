package eatawesome.inventoryexchange.common.tag;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.InventoryExchangeTags;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryExchangeItemTagsProvider extends ItemTagsProvider {

	public InventoryExchangeItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, InventoryExchange.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		tag(ItemTags.BOOKSHELF_BOOKS).add(InventoryExchangeItems.TOME_OF_KNOWLEDGE.get());
		tag(InventoryExchangeTags.Items.MINERALS_PLUS).add(
				Items.COAL,
				Items.DIAMOND,
				Items.EMERALD,
				Items.IRON_INGOT,
				Items.GOLD_INGOT,
				Items.COPPER_INGOT,
				Items.REDSTONE,
				Items.LAPIS_LAZULI,
				Items.NETHERITE_INGOT,
				InventoryExchangeItems.ULTIMA.get(),
				Items.GLOWSTONE_DUST,
				Items.CHARCOAL
		);
	}
}