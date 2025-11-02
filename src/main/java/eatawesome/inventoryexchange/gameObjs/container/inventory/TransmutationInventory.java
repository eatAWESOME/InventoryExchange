package eatawesome.inventoryexchange.gameObjs.container.inventory;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import eatawesome.inventoryexchange.gameObjs.InventoryExchangeTags;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.text.SearchQueryParser;
import moze_intel.projecte.utils.text.SearchQueryParser.ISearchQuery;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public class TransmutationInventory extends CombinedInvWrapper {

	public final Player player;
	public final IKnowledgeProvider provider;
	private final IItemHandlerModifiable learning;
	public final IItemHandlerModifiable outputs;

	private static final int MAX_MATTER_DISPLAY = 17;
	private static final int MAX_MINERALS_DISPLAY = 12;

	private static final int MINERALS_START = 0;
	private static final int MATTER_START = MINERALS_START + MAX_MINERALS_DISPLAY;
	public int learnFlag = 0;
	public ISearchQuery filter = ISearchQuery.INVALID;
	private int searchPage = 0;
	private boolean hasNextPage;
	private long lastAvailableEmc;

	public TransmutationInventory(Player player) {
		super((IItemHandlerModifiable) Objects.requireNonNull(player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)).getInputAndLocks(),
				new ItemStackHandler(2), new ItemStackHandler(30));
		this.player = player;
		this.provider = Objects.requireNonNull(player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY));
		this.learning = itemHandler[1];
		this.outputs = itemHandler[2];
		if (isClient()) {
			//Update all targets so that we display the items to the player
			updateClientTargets(false);
		}
	}

	public boolean isServer() {
		return !isClient();
	}

	public boolean isClient() {
		return player.level().isClientSide;
	}

	/**
	 * @apiNote Call on server only
	 * @implNote The passed stack will not be directly modified by this method.
	 */
	public void handleKnowledge(ItemStack stack) {
		if (!stack.isEmpty()) {
			handleKnowledge(ItemInfo.fromStack(stack));
		}
	}

	/**
	 * @apiNote Call on server only
	 */
	public void handleKnowledge(ItemInfo info) {
		ItemInfo cleanedInfo = IEMCProxy.INSTANCE.getPersistentInfo(info);
		//Pass both stacks to the Attempt Learn Event in case a mod cares about the data component/damage difference when comparing
		if (!provider.hasKnowledge(cleanedInfo) && !NeoForge.EVENT_BUS.post(new PlayerAttemptLearnEvent(player, info, cleanedInfo)).isCanceled()) {
			if (provider.addKnowledge(cleanedInfo)) {
				//Only sync the knowledge changed if the provider successfully added it
				provider.syncKnowledgeChange((ServerPlayer) player, cleanedInfo, true);
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	public void itemLearned(ItemInfo learnedItem) {
		learnFlag = 300;
		if (isServer()) {
			return;
		}
		long learnedItemEmc = IEMCProxy.INSTANCE.getValue(learnedItem);
		if (learnedItemEmc == 0 || learnedItem.getItem().is(InventoryExchangeItems.TOME_OF_KNOWLEDGE.getKey())) {
			//The learned item has no EMC. As the most likely case that would happen is if it was a tome of knowledge
			// we want to just update the client targets to be safe
			// Note: We also reset the search page, as while the page we are on should still exist, it is likely now meaningless
			resetSearchPage();
			updateClientTargets(false);
		} else if (doesItemMatchFilter(learnedItem)) {
			//Ensure the item matches the filter we currently are displaying, as if it doesn't then we don't have to update the display
			boolean learnedMineral = learnedItem.getItem().is(InventoryExchangeTags.Items.MINERALS_PLUS);
			//Check if our newly learned item is on a later page than the one we currently are on. We don't have to update it,
			// but we do update the fact that we have a next page, in case we previously were on the last page
			if (learnedMineral) {
				long lastMineralEmc = IEMCProxy.INSTANCE.getValue(outputs.getStackInSlot(outputs.getSlots() - 1));
				if (learnedItemEmc < lastMineralEmc) {
					hasNextPage = true;
					return;
				}
			} else if (learnedItemEmc < IEMCProxy.INSTANCE.getValue(outputs.getStackInSlot(MAX_MATTER_DISPLAY - 1))) {
				hasNextPage = true;
				return;
			}
			final long availableEmc = getAvailableEmcAsLong();
			//Ensure the learned item is in the emc range that we are even trying to display. If it requires more emc than we have available,
			// we can't possibly have it end up in the targets after an update
			if (learnedItemEmc <= availableEmc) {
				updateClientTargets(availableEmc);
			}
		}
	}
	
	/**
	 * @apiNote Call on client only
	 */
	public void checkForUpdates() {
		long availableEmc = getAvailableEmcAsLong();
		if (getMaxDisplayedEmc() > availableEmc) {
			//Available EMC is lower than what we are displaying, we need to update the targets
			updateClientTargets(availableEmc);
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	private long getMaxDisplayedEmc() {
		long matterEmc = IEMCProxy.INSTANCE.getValue(outputs.getStackInSlot(MATTER_START));
		long mineralEmc = IEMCProxy.INSTANCE.getValue(outputs.getStackInSlot(MINERALS_START));
		return Math.max(matterEmc, mineralEmc);
	}

	public void updateClientTargets(boolean checkForEmcChange) {
		if (isClient()) {
			long availableEmc = getAvailableEmcAsLong();
			if (!checkForEmcChange) {
				updateClientTargets(availableEmc);
			} else if (lastAvailableEmc != availableEmc) {
				//If the amount of emc we have available has changed, we want to recheck to see if any of the targets have shifted
				//TODO: Can we do any other checks for emc value changes to further optimize if we are updating the targets?
				updateClientTargets(availableEmc);
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	private void updateClientTargets(long availableEMC) {
		lastAvailableEmc = availableEMC;
		for (int i = 0, slots = outputs.getSlots(); i < slots; i++) {
			outputs.setStackInSlot(i, ItemStack.EMPTY);
		}
		record EmcData(ItemInfo info, long emc) {
		}
		Predicate<EmcData> filterPredicate;
		filterPredicate = data -> data.emc() > 0 && data.emc() <= availableEMC;

		List<ItemInfo> allItemInfo = provider.getKnowledge().stream()
				.map(info -> new EmcData(info, IEMCProxy.INSTANCE.getValue(info)))
				.filter(filterPredicate)
				.sorted(Comparator.comparingLong(EmcData::emc).reversed())
				.map(EmcData::info)
			    .filter(info -> doesItemMatchFilter(info))
				.toList();
		List<ItemInfo> mineralsItemInfo = allItemInfo.stream()
			    .filter(info -> info.getItem().is(InventoryExchangeTags.Items.MINERALS_PLUS))
			    .collect(Collectors.toList());
		List<ItemInfo> matterItemInfo = allItemInfo.stream()
			    .filter(info -> !info.getItem().is(InventoryExchangeTags.Items.MINERALS_PLUS))
			    .collect(Collectors.toList());

		int pageCount = 0;
		int desiredPageCount = searchPage * (MAX_MATTER_DISPLAY);
		int mineralsSlot = MINERALS_START;
		int matterSlot = MATTER_START;
		hasNextPage = false;

		for (ItemInfo info : mineralsItemInfo) {
			if (mineralsSlot < MINERALS_START + MAX_MINERALS_DISPLAY) {
				outputs.setStackInSlot(mineralsSlot++, info.createStack());
			}
		}
		for (ItemInfo info : matterItemInfo) {
			if (matterSlot < MATTER_START + MAX_MATTER_DISPLAY) {
				if (pageCount == desiredPageCount) {
					outputs.setStackInSlot(matterSlot++, info.createStack());
				} else {
					pageCount++;
				}
			} else {
				hasNextPage = true;
			}
		}
	}

	/**
	 * @apiNote Call on client only
	 */
	private boolean doesItemMatchFilter(ItemInfo info) {
		return filter.isInvalid() || filter.test(player.level(), player, info.createStack());
	}

	/**
	 * @apiNote Call on server only
	 */
	public void writeIntoOutputSlot(int slot, ItemStack item) {
		long emcValue = IEMCProxy.INSTANCE.getValue(item);
		if (emcValue > 0 && emcValue <= getAvailableEmcAsLong() && provider.hasKnowledge(item)) {
			outputs.setStackInSlot(slot, item);
		} else {
			outputs.setStackInSlot(slot, ItemStack.EMPTY);
		}
	}

	/**
	 * @apiNote Call on server only
	 */
	public void addEmc(BigInteger value) {
		if (value.signum() == 0) {//value == 0
			//Optimization to not look at the items if nothing will happen anyway
			return;
		} else if (value.signum() == -1) {//value < 0
			removeEmc(value.negate());
			return;
		}
		IntList inputLocksChanged = new IntArrayList();
		syncChangedSlots(inputLocksChanged, TargetUpdateType.NONE);
		//Note: We act as if there is no "max" EMC for the player given we use a BigInteger
		// This means we don't have to try to put the overflow into the lock slot if there is an EMC storage item there
		updateEmcAndSync(provider.getEmc().add(value));
	}

	/**
	 * @apiNote Call on server only
	 */
	public void removeEmc(BigInteger value) {
		if (value.signum() == 0) {//value == 0
			//Optimization to not look at the items if nothing will happen anyway
			return;
		} else if (value.signum() == -1) {//value < 0
			addEmc(value.negate());
			return;
		}
		BigInteger currentEmc = provider.getEmc();
		//Note: We act as if there is no "max" EMC for the player given we use a BigInteger
		// This means we don't need to first try removing it from the lock slot as it will auto drain from the lock slot
		if (value.compareTo(currentEmc) > 0) {
			//Remove from provider first
			//This code runs first to simplify the logic
			//But it simulates removal first by extracting the amount from value and then removing that excess from items
			IntList inputLocksChanged = new IntArrayList();
			value = currentEmc;
			
			//Sync the changed slots if any have changed
			syncChangedSlots(inputLocksChanged, TargetUpdateType.NONE);
		}
		updateEmcAndSync(currentEmc.subtract(value));
	}

	/**
	 * @apiNote Call on server only
	 */
	public void syncChangedSlots(IntList slotsChanged, TargetUpdateType updateTargets) {
		provider.syncInputAndLocks((ServerPlayer) player, slotsChanged, updateTargets);
	}

	/**
	 * @apiNote Call on server only
	 */
	private void updateEmcAndSync(BigInteger emc) {
		if (emc.signum() == -1) {//emc < 0
			//Clamp the emc, should never be less than zero but just in case make sure to fix it
			emc = BigInteger.ZERO;
		}
		provider.setEmc(emc);
		provider.syncEmc((ServerPlayer) player);
		PlayerHelper.updateScore((ServerPlayer) player, PlayerHelper.SCOREBOARD_EMC, emc);
	}

	public IItemHandlerModifiable getHandlerForSlot(int slot) {
		return super.getHandlerFromIndex(super.getIndexForSlot(slot));
	}

	public int getIndexFromSlot(int slot) {
		for (IItemHandlerModifiable h : itemHandler) {
			if (slot >= h.getSlots()) {
				slot -= h.getSlots();
			}
		}
		return slot;
	}

	/**
	 * @return EMC available from the Provider
	 */
	public long getAvailableEmcAsLong() {
		return MathUtils.clampToLong(provider.getEmc());
	}

	/**
	 * @return EMC available from the Provider
	 */
	public BigInteger getAvailableEmc() {
		return provider.getEmc();
	}

	public void updateFilter(String text) {
		String search = text.trim().toLowerCase(Locale.ROOT);
		ISearchQuery query = SearchQueryParser.parse(search);
		if (!filter.equals(query)) {
			filter = query;
			resetSearchPage();
			updateClientTargets(false);
		}
	}

	public boolean hasPreviousPage() {
		return searchPage > 0;
	}

	public boolean hasNextPage() {
		return hasNextPage;
	}

	private void resetSearchPage() {
		searchPage = 0;
	}

	public void previousPage() {
		if (hasPreviousPage()) {
			searchPage--;
			//TODO: Can we optimize updating the targets based on what are currently displaying? Probably not in a way that is worth it
			updateClientTargets(false);
		}
	}

	public void nextPage() {
		if (hasNextPage()) {
			searchPage++;
			//TODO: Can we optimize updating the targets based on what are currently displaying? Probably not in a way that is worth it
			updateClientTargets(false);
		}
	}
}