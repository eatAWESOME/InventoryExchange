package eatawesome.inventoryexchange.common.tag;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.InventoryExchangeTags;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryExchangeItemTagsProvider extends ItemTagsProvider {

	public InventoryExchangeItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, InventoryExchange.MODID, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		tag(ItemTags.BOOKSHELF_BOOKS).add(InventoryExchangeItems.TOME_OF_KNOWLEDGE.get());
		tag(ItemTags.HEAD_ARMOR).add(InventoryExchangeItems.ULTIMA_HELMET.get());
		tag(ItemTags.CHEST_ARMOR).add(InventoryExchangeItems.ULTIMA_CHESTPLATE.get());
		tag(ItemTags.LEG_ARMOR).add(InventoryExchangeItems.ULTIMA_LEGGINGS.get());
		tag(ItemTags.FOOT_ARMOR).add(InventoryExchangeItems.ULTIMA_BOOTS.get());
		tag(ItemTags.AXES).add(InventoryExchangeItems.ULTIMA_TOOL.get());
		tag(ItemTags.PICKAXES).add(InventoryExchangeItems.ULTIMA_TOOL.get());
		tag(ItemTags.SHOVELS).add(InventoryExchangeItems.ULTIMA_TOOL.get());
		
		tag(Tags.Items.TOOLS).add(
			InventoryExchangeItems.ULTIMA_TOOL.get()
		);
		tag(Tags.Items.ARMORS).add(
			InventoryExchangeItems.ULTIMA_HELMET.get(),
			InventoryExchangeItems.ULTIMA_CHESTPLATE.get(),
			InventoryExchangeItems.ULTIMA_LEGGINGS.get(),
			InventoryExchangeItems.ULTIMA_BOOTS.get()
		);
		
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
		tag(InventoryExchangeTags.Items.NO_INVENTORY_CONDENSING).add(
			InventoryExchangeItems.ULTIMA_WATCH.get(),
			Items.CLOCK,
			Items.COMPASS,
			Items.ELYTRA,
			Items.FILLED_MAP,
			Items.MAP,
			Items.MINECART,
			Items.SPYGLASS,
			Items.WRITABLE_BOOK,
			Items.WRITTEN_BOOK
		).addOptional(
			ResourceLocation.fromNamespaceAndPath("pixelmon", "poke_ball")
		).addTags(
			ItemTags.ARROWS,
			ItemTags.BOATS,
			ItemTags.BOOKSHELF_BOOKS,
			ItemTags.COMPASSES,
			Tags.Items.ARMORS,
			Tags.Items.BUCKETS,
			Tags.Items.ENCHANTABLES,
			Tags.Items.MELEE_WEAPON_TOOLS,
			Tags.Items.RANGED_WEAPON_TOOLS,
			Tags.Items.POTIONS,
			Tags.Items.TOOLS
		);
		
		IntrinsicTagAppender<Item> ignoreMissingEMC = tag(InventoryExchangeTags.Items.IGNORE_MISSING_EMC).add(
				Items.DEBUG_STICK, Items.KNOWLEDGE_BOOK, Items.STRUCTURE_VOID, Items.FROGSPAWN, Items.PETRIFIED_OAK_SLAB, Items.REINFORCED_DEEPSLATE,
				Items.SPAWNER, Items.TRIAL_SPAWNER, Items.VAULT, Items.TRIAL_KEY, Items.OMINOUS_TRIAL_KEY,
				Items.ELYTRA, Items.TOTEM_OF_UNDYING,
				Items.EXPERIENCE_BOTTLE, Items.OMINOUS_BOTTLE,
				Items.DRAGON_HEAD, Items.PLAYER_HEAD, Items.WITHER_SKELETON_SKULL,
				Items.BEE_NEST, Items.FARMLAND,
				Items.COMMAND_BLOCK_MINECART,
				Items.BUDDING_AMETHYST, Items.SMALL_AMETHYST_BUD, Items.MEDIUM_AMETHYST_BUD, Items.LARGE_AMETHYST_BUD,
				//Blocks that have no emc because it is less than one:
				Items.STONE_SLAB, Items.COBBLESTONE_SLAB, Items.SMOOTH_STONE_SLAB, Items.STONE_BRICK_SLAB, Items.END_STONE_BRICK_SLAB,
				Items.GLASS_PANE, Items.CYAN_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.LIME_STAINED_GLASS_PANE, Items.MAGENTA_STAINED_GLASS_PANE,
				Items.PINK_STAINED_GLASS_PANE
		).addTags(Tags.Items.CLUSTERS, Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS);
		for (Item item : BuiltInRegistries.ITEM) {
			if (item instanceof SpawnEggItem || item instanceof MobBucketItem) {
				ignoreMissingEMC.add(item);
			} else if (item instanceof BlockItem blockItem) {
				Block block = blockItem.getBlock();
				if (block instanceof InfestedBlock || block instanceof HugeMushroomBlock) {
					ignoreMissingEMC.add(item);
				}
			}
		}
	}
}