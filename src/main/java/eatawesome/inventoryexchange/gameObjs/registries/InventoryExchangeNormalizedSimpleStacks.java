package eatawesome.inventoryexchange.gameObjs.registries;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.InventoryExchangeRegistries;
import eatawesome.inventoryexchange.api.nss.NSSFake;
import eatawesome.inventoryexchange.api.nss.NSSFluid;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.gameObjs.registration.DeferredCodecHolder;
import eatawesome.inventoryexchange.gameObjs.registration.DeferredCodecRegister;

public class InventoryExchangeNormalizedSimpleStacks {

	private InventoryExchangeNormalizedSimpleStacks() {
	}

	public static final DeferredCodecRegister<NormalizedSimpleStack> NSS_SERIALIZERS = new DeferredCodecRegister<>(InventoryExchangeRegistries.NSS_SERIALIZER_NAME, InventoryExchange.MODID);

	public static final DeferredCodecHolder<NormalizedSimpleStack, NSSItem> ITEM = NSS_SERIALIZERS.registerCodec("item", () -> NSSItem.CODEC);
	public static final DeferredCodecHolder<NormalizedSimpleStack, NSSFluid> FLUID = NSS_SERIALIZERS.registerCodec("fluid", () -> NSSFluid.CODEC);
	public static final DeferredCodecHolder<NormalizedSimpleStack, NSSFake> FAKE = NSS_SERIALIZERS.registerCodec("fake", () -> NSSFake.CODEC);
}