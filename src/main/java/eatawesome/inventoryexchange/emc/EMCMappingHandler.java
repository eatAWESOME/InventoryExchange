package eatawesome.inventoryexchange.emc;

import com.mojang.logging.LogUtils;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.ItemInfo;
import eatawesome.inventoryexchange.api.capabilities.IKnowledgeProvider;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.api.event.EMCRemapEvent;
import eatawesome.inventoryexchange.api.mapper.IEMCMapper;
import eatawesome.inventoryexchange.api.mapper.arithmetic.IValueArithmetic;
import eatawesome.inventoryexchange.api.mapper.collector.IExtendedMappingCollector;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.config.InventoryExchangeEMCMappingConfig;
import eatawesome.inventoryexchange.config.InventoryExchangeEMCConfig;
import eatawesome.inventoryexchange.emc.arithmetic.HiddenBigFractionArithmetic;
import eatawesome.inventoryexchange.emc.collector.DumpToFileCollector;
import eatawesome.inventoryexchange.emc.collector.LongToBigFractionCollector;
import eatawesome.inventoryexchange.emc.components.DataComponentManager;
import eatawesome.inventoryexchange.emc.generator.BigFractionToLongGenerator;
import eatawesome.inventoryexchange.emc.mappers.TagMapper;
import eatawesome.inventoryexchange.emc.pregenerated.PregeneratedEMC;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.impl.capability.KnowledgeImpl;
import eatawesome.inventoryexchange.network.packets.to_client.SyncEmcPKT;
import eatawesome.inventoryexchange.utils.AnnotationHelper;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.math3.fraction.BigFraction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public final class EMCMappingHandler {

	private static final List<IEMCMapper<NormalizedSimpleStack, Long>> mappers = new ArrayList<>();
	@Nullable
	private static Object2LongMap<ItemInfo> emc;
	private static int loadIndex = -1;

	public static void loadMappers() {
		//If we don't have any mappers loaded try to load them
		if (mappers.isEmpty()) {
			//Add all the EMC mappers we have encountered
			mappers.addAll(AnnotationHelper.getEMCMappers());
			//Manually register the Tag Mapper to ensure that it is registered last so that it can "fix" all the tags used in any of the other mappers
			// This also has the side effect to make sure that we can use EMC_MAPPERS.isEmpty to check if we have attempted to initialize our cache yet
			mappers.add(new TagMapper());
			//Set up the config for the Mappers and processors
			InventoryExchangeEMCMappingConfig.setup(mappers, DataComponentManager.loadProcessors());
		}
	}

	public static void map(ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
		//Start by clearing the cached map so if values are removed say by setting EMC to zero then we respect the change
		clearEmcMap();
		SimpleGraphMapper<NormalizedSimpleStack, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		BigFractionToLongGenerator<NormalizedSimpleStack> valueGenerator = new BigFractionToLongGenerator<>(mapper);
		IExtendedMappingCollector<NormalizedSimpleStack, Long, IValueArithmetic<BigFraction>> mappingCollector = new LongToBigFractionCollector<>(mapper);

		if (InventoryExchangeEMCMappingConfig.dumpToFile()) {
			mappingCollector = new DumpToFileCollector<>(InventoryExchangeEMCConfig.CONFIG_DIR.resolve("mapping_dump.json"), mappingCollector);
		}

		boolean usePregenerated = InventoryExchangeEMCMappingConfig.usePregenerated();
		Path pregeneratedEmcFile = InventoryExchangeEMCConfig.CONFIG_DIR.resolve("pregenerated_emc.json");
		Optional<Object2LongMap<ItemInfo>> readPregeneratedValues = PregeneratedEMC.read(registryAccess, pregeneratedEmcFile, usePregenerated);
		if (readPregeneratedValues.isPresent()) {
			int values = updateEmcValues(readPregeneratedValues.get());
			InventoryExchange.debugLog("Loaded {} values from pregenerated EMC File", values);
		} else {
			SimpleGraphMapper.setLogFoundExploits(InventoryExchangeEMCMappingConfig.logExploits());

			InventoryExchange.debugLog("Starting to collect Mappings...");
			for (IEMCMapper<NormalizedSimpleStack, Long> emcMapper : mappers) {
				if (InventoryExchangeEMCMappingConfig.isEnabled(emcMapper)) {
					DumpToFileCollector.currentGroupName = emcMapper.getName();
					try {
						emcMapper.addMappings(mappingCollector, serverResources, registryAccess, resourceManager);
						InventoryExchange.debugLog("Collected Mappings from " + emcMapper.getClass().getName());
					} catch (Exception e) {
						InventoryExchange.LOGGER.error(LogUtils.FATAL_MARKER, "Exception during Mapping Collection from Mapper {}. PLEASE REPORT THIS! EMC VALUES MIGHT BE INCONSISTENT!",
								emcMapper.getClass().getName(), e);
					}
				}
			}
			DumpToFileCollector.currentGroupName = "NSSHelper";

			InventoryExchange.debugLog("Mapping Collection finished");
			mappingCollector.finishCollection(registryAccess);

			InventoryExchange.debugLog("Starting to generate Values:");
			Object2LongMap<NormalizedSimpleStack> graphMapperValues = valueGenerator.generateValues();
			InventoryExchange.debugLog("Generated Values...");

			updateEmcValues(filterEMCMap(graphMapperValues));
			InventoryExchange.debugLog("Filtered Values...");

			if (usePregenerated && emc != null) {//Note: It should never be null here as we just set it
				//Should have used pregenerated, but the file was not read => regenerate.
				PregeneratedEMC.write(registryAccess, pregeneratedEmcFile, emc);
				InventoryExchange.debugLog("Wrote Pregen-file!");
			}
		}

		fireEmcRemapEvent();
	}

	private static void fireEmcRemapEvent() {
		//Start by doing our implementations
		loadIndex++;
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				IKnowledgeProvider knowledge = player.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
				if (knowledge != null) {
					if (knowledge instanceof KnowledgeImpl impl && impl.pruneStaleKnowledge()) {
						knowledge.sync(player);
					} else if (player.containerMenu instanceof TransmutationContainerMenu) {
						//If knowledge didn't get trimmed due to pruning, tell clients that have the transmutation gui open
						// that they should update targets anyway, as it is possible EMC values changed and the order things
						// are drawn needs to be changed
						InventoryExchange.packetHandler().updateTransmutationTargets(player);
					}
				}
			}
		}
		NeoForge.EVENT_BUS.post(new EMCRemapEvent());
	}

	public static int getLoadIndex() {
		return loadIndex;
	}

	private static Object2LongMap<ItemInfo> filterEMCMap(Object2LongMap<NormalizedSimpleStack> map) {
		Object2LongMap<ItemInfo> resultMap = new Object2LongOpenHashMap<>(map.size());
		for (Iterator<Object2LongMap.Entry<NormalizedSimpleStack>> iterator = Object2LongMaps.fastIterator(map); iterator.hasNext(); ) {
			Object2LongMap.Entry<NormalizedSimpleStack> entry = iterator.next();
			if (entry.getKey() instanceof NSSItem nssItem) {
				//Note: We don't need to check if the value is greater than zero, as our generated values filter out any non positive values
				ItemInfo info = ItemInfo.fromNSS(nssItem);
				if (info != null) {//Ensure the item actually exists and is not a tag
					resultMap.put(info, entry.getLongValue());
				}
			}
		}
		return resultMap;
	}

	public static int getEmcMapSize() {
		return emc == null ? 0 : emc.size();
	}

	public static boolean hasEmcValue(@NotNull ItemInfo info) {
		return emc != null && emc.containsKey(info);
	}

	/**
	 * Gets the stored emc value or zero if there is no entry in the map for the given value.
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	public static long getStoredEmcValue(@NotNull ItemInfo info) {
		return emc == null ? 0 : emc.getLong(info);
	}

	public static void clearEmcMap() {
		emc = null;
		DataComponentManager.updateCachedValues(null);
	}

	/**
	 * Returns a modifiable set of all the mapped {@link ItemInfo}
	 */
	public static Set<ItemInfo> getMappedItems() {
		if (emc == null) {
			return new HashSet<>();
		}
		return new HashSet<>(emc.keySet());
	}

	@ApiStatus.Internal
	public static int updateEmcValues(Object2LongMap<ItemInfo> data) {
		emc = data;
		DataComponentManager.updateCachedValues(emc::getLong);
		return emc.size();
	}

	public static SyncEmcPKT createPacketData() {
		return new SyncEmcPKT(emc == null ? Object2LongMaps.emptyMap() : Object2LongMaps.unmodifiable(emc));
	}
}