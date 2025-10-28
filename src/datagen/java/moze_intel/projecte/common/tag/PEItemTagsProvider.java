package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
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

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;

public class PEItemTagsProvider extends ItemTagsProvider {

	public PEItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, PECore.PEID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		addIgnoreMissing();
	}

	private void addIgnoreMissing() {
		IntrinsicTagAppender<Item> ignoreMissingEMC = tag(PETags.Items.IGNORE_MISSING_EMC).add(
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