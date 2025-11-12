package eatawesome.inventoryexchange;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.config.InventoryExchangeConfig;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeArmorMaterials;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeCreativeTabs;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import eatawesome.inventoryexchange.impl.capability.AbilityEMCImpl;
import eatawesome.inventoryexchange.impl.capability.InfiniteContainerImpl;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import eatawesome.inventoryexchange.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(InventoryExchange.MODID)
public class InventoryExchange {

	public static final String MODID = "inventoryexchange";
	public static final String MODNAME = "InventoryExchange";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static ResourceLocation rl(String path) { return ResourceLocation.fromNamespaceAndPath(MODID, path); }

	public static void debugLog(String msg, Object... args) {
		if (!FMLEnvironment.production || InventoryExchangeConfig.isDebugLoggingEnabled()) {
			LOGGER.info(msg, args);
		} else {
			LOGGER.debug(msg, args);
		}
	}

	private final PacketHandler packetHandler;
	private static InventoryExchange instance;

	public InventoryExchange(IEventBus modEventBus, ModContainer modContainer) {
		instance = this;

		modContainer.registerConfig(ModConfig.Type.COMMON, InventoryExchangeConfig.COMMON_SPEC, MODID + "/" + MODID + ".toml");

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::registerCapabilities);
		
		InventoryExchangeArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
		InventoryExchangeAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
		InventoryExchangeContainerTypes.CONTAINER_TYPES.register(modEventBus);
		InventoryExchangeItems.ITEMS.register(modEventBus);
		InventoryExchangeCreativeTabs.CREATIVE_TABS.register(modEventBus);
		
		NeoForge.EVENT_BUS.addListener(InventoryExchangePermissions::registerPermissionNodes);

		this.packetHandler = new PacketHandler(modEventBus, modContainer.getModInfo().getVersion());
	}

	public static PacketHandler packetHandler() { return instance.packetHandler; }

	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerEntity(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY, EntityType.PLAYER, (player, context) -> new AbilityEMCImpl(player));
		event.registerEntity(InventoryExchangeCapabilities.INFINITE_CONTAINER_CAPABILITY, EntityType.PLAYER, (player, context) -> new InfiniteContainerImpl(player));
		event.registerEntity(InventoryExchangeCapabilities.PORTABLE_FURNACE_CAPABILITY, EntityType.PLAYER, (player, context) -> new PortableFurnaceImpl(player));
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		debugLog("Debug logging enabled = {}", InventoryExchangeConfig.isDebugLoggingEnabled());
		debugLog("Infinite container max rows = {}", InventoryExchangeConfig.getMaxInfiniteContainerRows());
	}
}