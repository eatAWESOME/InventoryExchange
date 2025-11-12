package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;

import eatawesome.inventoryexchange.InventoryExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import moze_intel.projecte.api.ProjectERegistries;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.nss.AbstractNSSTag;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PENormalizedSimpleStacks;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_client.SyncEmcPKT;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.callback.ClearCallback;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(InventoryExchange.MODID)
public class PECore {

	public static final String MODID = InventoryExchange.MODID;
	public static final String PEID = "projecte";
	public static final String MODNAME = "InventoryExchange";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[" + MODNAME + "]");
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final List<String> uuids = new ArrayList<>();

	public static ModContainer MOD_CONTAINER;

	public static void debugLog(String msg, Object... args) {
		if (!FMLEnvironment.production || ProjectEConfig.common.debugLogging.get()) {
			LOGGER.info(msg, args);
		} else {
			LOGGER.debug(msg, args);
		}
	}

	public static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(PEID, path);
	}

	private static PECore instance;

	@Nullable
	private EmcUpdateData emcUpdateResourceManager;
	private final PacketHandler packetHandler;

	public PECore(ModContainer modContainer, IEventBus modEventBus) {
		instance = this;
		MOD_CONTAINER = modContainer;

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(IntegrationHelper::sendIMCMessages);
		modEventBus.addListener(this::registerCapabilities);
		modEventBus.addListener(this::registerRegistries);
		modEventBus.addListener(this::modifyRegistries);
		PEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
		PENormalizedSimpleStacks.NSS_SERIALIZERS.register(modEventBus);
		PESoundEvents.SOUND_EVENTS.register(modEventBus);
		NeoForge.EVENT_BUS.addListener(this::addReloadListeners);
		NeoForge.EVENT_BUS.addListener(this::dataPackSync);
		NeoForge.EVENT_BUS.addListener(this::serverStarting);
		NeoForge.EVENT_BUS.addListener(this::serverQuit);

		//Register our config files
		ProjectEConfig.register(modContainer);
		modEventBus.addListener(ProjectEConfig::onConfigLoad);

		this.packetHandler = new PacketHandler(modEventBus, modContainer.getModInfo().getVersion());
	}

	public static PacketHandler packetHandler() {
		return instance.packetHandler;
	}

	private void registerRegistries(NewRegistryEvent event) {
		event.register(ProjectERegistries.NSS_SERIALIZER);
	}

	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerEntity(PECapabilities.KNOWLEDGE_CAPABILITY, EntityType.PLAYER, (player, context) -> new KnowledgeImpl(player));
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		EMCMappingHandler.loadMappers();
	}

	private void dataPackSync(OnDatapackSyncEvent event) {
		if (emcUpdateResourceManager != null) {
			long start = System.currentTimeMillis();
			//Clear the cached created tags
			AbstractNSSTag.clearCreatedTags();
			CustomEMCParser.init(emcUpdateResourceManager.registryAccess());
			try {
				EMCMappingHandler.map(emcUpdateResourceManager.serverResources(), emcUpdateResourceManager.registryAccess(), emcUpdateResourceManager.resourceManager());
				PECore.LOGGER.info("Registered {} EMC values. (took {} ms)", EMCMappingHandler.getEmcMapSize(), System.currentTimeMillis() - start);
			} catch (Throwable t) {
				PECore.LOGGER.error("Error calculating EMC values", t);
			}
			emcUpdateResourceManager = null;
		}
		if (event.getPlayer() == null) {
			List<ServerPlayer> players = event.getPlayerList().getPlayers();
			if (players.isEmpty()) {
				return;
			}
			SyncEmcPKT pkt = SyncEmcPKT.serializeEmcData(players.getFirst().registryAccess());
			for (ServerPlayer player : players) {
				if (!player.connection.getConnection().isMemoryConnection()) {
					PacketDistributor.sendToPlayer(player, pkt);
				}
			}
		} else {
			ServerPlayer player = event.getPlayer();
			if (!player.connection.getConnection().isMemoryConnection()) {
				PacketDistributor.sendToPlayer(player, SyncEmcPKT.serializeEmcData(player.registryAccess()));
			}
		}
	}

	private void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener((ResourceManagerReloadListener) manager -> emcUpdateResourceManager = new EmcUpdateData(event.getServerResources(), event.getRegistryAccess(), manager));
	}

	private void serverStarting(ServerStartingEvent event) {
	}

	private void serverQuit(ServerStoppedEvent event) {
		//Ensure we save any changes to the custom emc file
		CustomEMCParser.flush(event.getServer().registryAccess());
		TransmutationOffline.cleanAll();
		EMCMappingHandler.clearEmcMap();
	}

	private void modifyRegistries(ModifyRegistriesEvent event) {
		BuiltInRegistries.BLOCK.addCallback((ClearCallback<Block>) (registry, full) -> WorldHelper.clearCachedAgeProperties());
	}

	private record EmcUpdateData(ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
	}
}