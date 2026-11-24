package eatawesome.inventoryexchange.gameObjs.registries;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.impl.SoundEventDeferredRegister;
import eatawesome.inventoryexchange.gameObjs.registration.impl.SoundEventRegistryObject;
import net.minecraft.sounds.SoundEvent;

public class InventoryExchangeSoundEvents {

	public static final SoundEventDeferredRegister SOUND_EVENTS = new SoundEventDeferredRegister(InventoryExchange.MODID);

	public static final SoundEventRegistryObject<SoundEvent> FLIGHT_START = SOUND_EVENTS.register("flight_start");
	public static final SoundEventRegistryObject<SoundEvent> FLIGHT_END = SOUND_EVENTS.register("flight_end");
	public static final SoundEventRegistryObject<SoundEvent> VEIN_MINE = SOUND_EVENTS.register("vein_mine");
}