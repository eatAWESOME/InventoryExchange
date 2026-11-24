package eatawesome.inventoryexchange.client.sound;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.impl.SoundEventRegistryObject;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class InventoryExchangeSoundProvider extends SoundDefinitionsProvider {

	public InventoryExchangeSoundProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, InventoryExchange.MODID, existingFileHelper);
	}

	protected void addSoundEventWithSubtitle(SoundEventRegistryObject<?> soundEventRO, ResourceLocation location) {
		add(soundEventRO.get(), SoundDefinition.definition().subtitle(soundEventRO.getTranslationKey()).with(sound(location)));
	}

	@Override
	public void registerSounds() {
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.FLIGHT_START, InventoryExchange.rl("item/flight_start"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.FLIGHT_END, InventoryExchange.rl("item/flight_end"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.VEIN_MINE, InventoryExchange.rl("item/vein_mine"));
	}
}