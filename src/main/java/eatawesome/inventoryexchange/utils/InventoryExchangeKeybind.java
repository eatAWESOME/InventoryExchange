package eatawesome.inventoryexchange.utils;

import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.function.IntFunction;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.utils.text.IHasTranslationKey.IHasEnumNameTranslationKey;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum InventoryExchangeKeybind implements IHasEnumNameTranslationKey {
	CRAFTING,
	CONDENSE_TOGGLE,
	FEED_TOGGLE,
	FURNACE,
	INFINITE_CONTAINER,
	REPAIR_TOGGLE,
	SMELT_TOGGLE,
	TRANSMUTATION,
	ULTIMA_TOOL_MODE;

	public static final IntFunction<InventoryExchangeKeybind> BY_ID = ByIdMap.continuous(InventoryExchangeKeybind::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
	public static final StreamCodec<ByteBuf, InventoryExchangeKeybind> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, InventoryExchangeKeybind::ordinal);

	private final String translationKey;

	InventoryExchangeKeybind() {
		this.translationKey = Util.makeDescriptionId("key", InventoryExchange.rl(name().toLowerCase(Locale.ROOT)));
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}
}