package eatawesome.inventoryexchange;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;

import eatawesome.inventoryexchange.api.InventoryExchangeRegistries;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.api.nss.AbstractNSSTag;
import eatawesome.inventoryexchange.config.InventoryExchangeCommonConfig;
import eatawesome.inventoryexchange.config.InventoryExchangeCustomEMCParser;
import eatawesome.inventoryexchange.config.InventoryExchangeServerConfig;
import eatawesome.inventoryexchange.config.InventoryExchangeEMCConfig;
import eatawesome.inventoryexchange.emc.EMCMappingHandler;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeArmorMaterials;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeCreativeTabs;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeNormalizedSimpleStacks;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeSoundEvents;
import eatawesome.inventoryexchange.impl.TransmutationOffline;
import eatawesome.inventoryexchange.impl.capability.AbilityEMCImpl;
import eatawesome.inventoryexchange.impl.capability.InfiniteContainerImpl;
import eatawesome.inventoryexchange.impl.capability.KnowledgeImpl;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import eatawesome.inventoryexchange.integration.IntegrationHelper;
import eatawesome.inventoryexchange.integration.pixelmon.PixelmonIntegration;
import eatawesome.inventoryexchange.network.PacketHandler;
import eatawesome.inventoryexchange.network.packets.to_client.SyncEmcPKT;
import eatawesome.inventoryexchange.utils.WorldHelper;
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
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
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

@Mod(InventoryExchange.MODID)
public class InventoryExchange {

	public static final String MODID = "inventoryexchange";
	public static final String MODNAME = "InventoryExchange";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[" + MODNAME + "]");
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final List<String> uuids = new ArrayList<>();
	public static ModContainer MOD_CONTAINER;

	public static void debugLog(String msg, Object... args) {
		if (!FMLEnvironment.production || InventoryExchangeCommonConfig.isDebugLoggingEnabled()) {
			LOGGER.info(msg, args);
		} else {
			LOGGER.debug(msg, args);
		}
	}
	
	public static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	
	private final PacketHandler packetHandler;
	private static InventoryExchange instance;
	@Nullable
	private EmcUpdateData emcUpdateResourceManager;
	
	
	public InventoryExchange(IEventBus modEventBus, ModContainer modContainer) {
		instance = this;
		MOD_CONTAINER = modContainer;

		modContainer.registerConfig(ModConfig.Type.COMMON, InventoryExchangeCommonConfig.COMMON_SPEC, MODID + "/common.toml");
		modContainer.registerConfig(ModConfig.Type.SERVER, InventoryExchangeServerConfig.SERVER_SPEC, MODID + "/server.toml");

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(IntegrationHelper::sendIMCMessages);
		modEventBus.addListener(this::registerCapabilities);
		modEventBus.addListener(this::registerRegistries);
		modEventBus.addListener(this::modifyRegistries);
		
		InventoryExchangeAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
		InventoryExchangeNormalizedSimpleStacks.NSS_SERIALIZERS.register(modEventBus);
		InventoryExchangeSoundEvents.SOUND_EVENTS.register(modEventBus);
		
		InventoryExchangeArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
		InventoryExchangeContainerTypes.CONTAINER_TYPES.register(modEventBus);
		InventoryExchangeItems.ITEMS.register(modEventBus);
		InventoryExchangeCreativeTabs.CREATIVE_TABS.register(modEventBus);
		
		NeoForge.EVENT_BUS.addListener(InventoryExchangePermissions::registerPermissionNodes);
		NeoForge.EVENT_BUS.addListener(this::addReloadListeners);
		NeoForge.EVENT_BUS.addListener(this::dataPackSync);
		NeoForge.EVENT_BUS.addListener(this::serverStarting);
		NeoForge.EVENT_BUS.addListener(this::serverQuit);
		
		InventoryExchangeEMCConfig.register(modContainer);
		modEventBus.addListener(InventoryExchangeEMCConfig::onConfigLoad);
		
		if (ModList.get().isLoaded("pixelmon")) {
			PixelmonIntegration.register();
		}
		
		this.packetHandler = new PacketHandler(modEventBus, modContainer.getModInfo().getVersion());
	}

	public static PacketHandler packetHandler() {
		return instance.packetHandler;
	}

	private void registerRegistries(NewRegistryEvent event) {
		event.register(InventoryExchangeRegistries.NSS_SERIALIZER);
	}
	
	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerEntity(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY, EntityType.PLAYER, (player, context) -> new KnowledgeImpl(player));
		event.registerEntity(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY, EntityType.PLAYER, (player, context) -> new AbilityEMCImpl(player));
		event.registerEntity(InventoryExchangeCapabilities.INFINITE_CONTAINER_CAPABILITY, EntityType.PLAYER, (player, context) -> new InfiniteContainerImpl(player));
		event.registerEntity(InventoryExchangeCapabilities.PORTABLE_FURNACE_CAPABILITY, EntityType.PLAYER, (player, context) -> new PortableFurnaceImpl(player));
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		EMCMappingHandler.loadMappers();
	}
	
	private void dataPackSync(OnDatapackSyncEvent event) {
		if (emcUpdateResourceManager != null) {
			long start = System.currentTimeMillis();
			//Clear the cached created tags
			AbstractNSSTag.clearCreatedTags();
			InventoryExchangeCustomEMCParser.init(emcUpdateResourceManager.registryAccess());
			try {
				EMCMappingHandler.map(emcUpdateResourceManager.serverResources(), emcUpdateResourceManager.registryAccess(), emcUpdateResourceManager.resourceManager());
				InventoryExchange.LOGGER.info("Registered {} EMC values. (took {} ms)", EMCMappingHandler.getEmcMapSize(), System.currentTimeMillis() - start);
			} catch (Throwable t) {
				InventoryExchange.LOGGER.error("Error calculating EMC values", t);
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
		InventoryExchangeCustomEMCParser.flush(event.getServer().registryAccess());
		TransmutationOffline.cleanAll();
		EMCMappingHandler.clearEmcMap();
	}

	private void modifyRegistries(ModifyRegistriesEvent event) {
		BuiltInRegistries.BLOCK.addCallback((ClearCallback<Block>) (registry, full) -> WorldHelper.clearCachedAgeProperties());
	}
	
	private record EmcUpdateData(ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
	}
}