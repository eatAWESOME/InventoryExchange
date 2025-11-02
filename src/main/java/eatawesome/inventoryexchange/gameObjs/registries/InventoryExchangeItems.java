package eatawesome.inventoryexchange.gameObjs.registries;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.items.Tome;
import eatawesome.inventoryexchange.gameObjs.items.UltimaArmor;
import eatawesome.inventoryexchange.gameObjs.items.UltimaTool;
import eatawesome.inventoryexchange.gameObjs.items.UltimaWatch;
import eatawesome.inventoryexchange.gameObjs.registration.impl.ItemDeferredRegister;
import eatawesome.inventoryexchange.gameObjs.registration.impl.ItemRegistryObject;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class InventoryExchangeItems {

	public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(InventoryExchange.MODID);
	public static final ItemRegistryObject<Item> ULTIMA = ITEMS.registerUltima("ultima");
	public static final ItemRegistryObject<Item> ULTIMA_TOOL = ITEMS.register("ultima_tool", UltimaTool::new);
	public static final ItemRegistryObject<Item> ULTIMA_WATCH = ITEMS.register("ultima_watch", UltimaWatch::new);
	public static final ItemRegistryObject<Item> ULTIMA_HELMET = ITEMS.register("ultima_helmet", () -> new UltimaArmor(ArmorItem.Type.HELMET));
	public static final ItemRegistryObject<Item> ULTIMA_CHESTPLATE = ITEMS.register("ultima_chestplate", () -> new UltimaArmor(ArmorItem.Type.CHESTPLATE));
	public static final ItemRegistryObject<Item> ULTIMA_LEGGINGS = ITEMS.register("ultima_leggings", () -> new UltimaArmor(ArmorItem.Type.LEGGINGS));
	public static final ItemRegistryObject<Item> ULTIMA_BOOTS = ITEMS.register("ultima_boots", () -> new UltimaArmor(ArmorItem.Type.BOOTS));
	public static final ItemRegistryObject<Item> TOME_OF_KNOWLEDGE = ITEMS.registerSimple("tome_of_knowledge", properties -> new Tome(properties.rarity(Rarity.EPIC).stacksTo(1)));
}