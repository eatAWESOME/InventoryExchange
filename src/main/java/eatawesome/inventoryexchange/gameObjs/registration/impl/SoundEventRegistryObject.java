package eatawesome.inventoryexchange.gameObjs.registration.impl;

import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredHolder;
import eatawesome.inventoryexchange.utils.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class SoundEventRegistryObject<SOUND extends SoundEvent> extends InventoryExchangeDeferredHolder<SoundEvent, SOUND> implements ILangEntry {

	private final String translationKey;

	public SoundEventRegistryObject(ResourceKey<SoundEvent> key) {
		super(key);
		translationKey = Util.makeDescriptionId("sound_event", getId());
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}
}