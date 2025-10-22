package eatawesome.inventoryexchange.gameObjs.registries;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredHolder;
import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredRegister;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class InventoryExchangeArmorMaterials {

	private InventoryExchangeArmorMaterials() {
	}

	public static final InventoryExchangeDeferredRegister<ArmorMaterial> ARMOR_MATERIALS = new InventoryExchangeDeferredRegister<>(Registries.ARMOR_MATERIAL, InventoryExchange.MODID);
	private static final Map<ArmorItem.Type, Integer> DIAMOND_RESISTANCES = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 11);
	});

	public static final InventoryExchangeDeferredHolder<ArmorMaterial, ArmorMaterial> ULTIMA = ARMOR_MATERIALS.register("ultima", rl -> new ArmorMaterial(
			DIAMOND_RESISTANCES, 0, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(rl)), 2, 0.2F
	));
}