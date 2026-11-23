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
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.WIND_MAGIC, InventoryExchange.rl("item/pewindmagic"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.WATER_MAGIC, InventoryExchange.rl("item/pewatermagic"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.POWER, InventoryExchange.rl("item/pepower"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.HEAL, InventoryExchange.rl("item/peheal"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.DESTRUCT, InventoryExchange.rl("item/pedestruct"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.CHARGE, InventoryExchange.rl("item/pecharge"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.UNCHARGE, InventoryExchange.rl("item/peuncharge"));
		addSoundEventWithSubtitle(InventoryExchangeSoundEvents.TRANSMUTE, InventoryExchange.rl("item/petransmute"));
		//TODO: Evaluate the remaining sounds that we don't actually use anywhere
	}
}