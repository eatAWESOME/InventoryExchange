package eatawesome.inventoryexchange.gameObjs.items;

import org.jetbrains.annotations.NotNull;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;

public final class UltimaArmor extends ArmorItem {

	public UltimaArmor(Type type) {
		super(InventoryExchangeArmorMaterials.ULTIMA, type, new Item.Properties()
			.stacksTo(1)
			.fireResistant()
			.rarity(Rarity.EPIC)
			.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
	}
	
	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repairCandidate) {
		return false;
	}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
		return false;
	}

	@Override
	public boolean isPrimaryItemFor(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
		return false;
	}

	@Override
	public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
		return false;
	}
}