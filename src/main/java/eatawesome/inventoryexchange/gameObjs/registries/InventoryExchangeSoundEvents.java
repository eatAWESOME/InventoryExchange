package eatawesome.inventoryexchange.gameObjs.registries;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.impl.SoundEventDeferredRegister;
import eatawesome.inventoryexchange.gameObjs.registration.impl.SoundEventRegistryObject;
import net.minecraft.sounds.SoundEvent;

public class InventoryExchangeSoundEvents {

	public static final SoundEventDeferredRegister SOUND_EVENTS = new SoundEventDeferredRegister(InventoryExchange.MODID);

	public static final SoundEventRegistryObject<SoundEvent> WIND_MAGIC = SOUND_EVENTS.register("windmagic");
	public static final SoundEventRegistryObject<SoundEvent> WATER_MAGIC = SOUND_EVENTS.register("watermagic");
	public static final SoundEventRegistryObject<SoundEvent> POWER = SOUND_EVENTS.register("power");
	public static final SoundEventRegistryObject<SoundEvent> HEAL = SOUND_EVENTS.register("heal");
	public static final SoundEventRegistryObject<SoundEvent> DESTRUCT = SOUND_EVENTS.register("destruct");
	public static final SoundEventRegistryObject<SoundEvent> CHARGE = SOUND_EVENTS.register("charge");
	public static final SoundEventRegistryObject<SoundEvent> UNCHARGE = SOUND_EVENTS.register("uncharge");
	public static final SoundEventRegistryObject<SoundEvent> TRANSMUTE = SOUND_EVENTS.register("transmute");
}