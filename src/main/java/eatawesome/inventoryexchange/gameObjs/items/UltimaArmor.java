package eatawesome.inventoryexchange.gameObjs.items;

import org.jetbrains.annotations.NotNull;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeArmorMaterials;
import eatawesome.inventoryexchange.utils.ItemHelper;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import eatawesome.inventoryexchange.utils.WorldHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public final class UltimaArmor extends ArmorItem {

	public UltimaArmor(Type type) {
		super(InventoryExchangeArmorMaterials.ULTIMA, type, new Item.Properties()
			.stacksTo(1)
			.fireResistant()
			.rarity(Rarity.EPIC)
			.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
	}
	
	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		super.inventoryTick(stack, level, entity, slot, isHeld);
		if (entity instanceof Player player && this.getType() == Type.CHESTPLATE && PlayerHelper.hasFullUltimaSet(player)) {
			for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(10))) {
				if (ItemHelper.simulateFit(player, item.getItem()) < item.getItem().getCount()) {
					WorldHelper.gravitateEntityTowards(item, player.position());
				}
			}
		}
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