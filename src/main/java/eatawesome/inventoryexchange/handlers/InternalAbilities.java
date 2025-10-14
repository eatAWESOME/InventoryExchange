package eatawesome.inventoryexchange.handlers;

import com.google.common.base.Predicates;

import java.util.function.Predicate;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.NeoForgeMod;

public class InternalAbilities {

	private static final AttributeModifier FLIGHT = new AttributeModifier(InventoryExchange.rl("flight"), 1, Operation.ADD_VALUE);
	private static final AttributeModifier WATER_SPEED_BOOST = new AttributeModifier(InventoryExchange.rl("water_speed_boost"), 0.15, Operation.ADD_VALUE);
	private static final AttributeModifier LAVA_SPEED_BOOST = new AttributeModifier(InventoryExchange.rl("lava_speed_boost"), 0.15, Operation.ADD_VALUE);

	public static void tick(Player player) {
		Predicate<Player> applyWaterSpeed = Predicates.alwaysFalse();
		Predicate<Player> applyLavaSpeed = Predicates.alwaysFalse();
		WalkOnType waterWalkOnType = canWalkOnWater(player);
		WalkOnType lavaWalkOnType = canWalkOnLava(player);
		if (waterWalkOnType.canWalk() || lavaWalkOnType.canWalk()) {
			FluidState below = player.level().getFluidState(player.getOnPos());
			boolean water = waterWalkOnType.canWalk() && below.is(FluidTags.WATER);
			boolean lava = lavaWalkOnType.canWalk() && below.is(FluidTags.LAVA);
			if ((water || lava) && player.getInBlockState().isAir()) {
				if (!player.isShiftKeyDown()) {
					player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
					player.resetFallDistance();
					player.setOnGround(true);
				}
				applyWaterSpeed = waterWalkOnType.applySpeed(water);
				applyLavaSpeed = lavaWalkOnType.applySpeed(lava);
			} else if (!player.level().isClientSide) {
				if (waterWalkOnType.canWalk() && player.isInWater()) {
					player.setAirSupply(player.getMaxAirSupply());
				}
			}
		}
		if (!player.level().isClientSide) {
			updateAttribute(player, Attributes.MOVEMENT_SPEED, WATER_SPEED_BOOST, applyWaterSpeed);
			updateAttribute(player, Attributes.MOVEMENT_SPEED, LAVA_SPEED_BOOST, applyLavaSpeed);
			updateAttribute(player, NeoForgeMod.CREATIVE_FLIGHT, FLIGHT, InternalAbilities::shouldPlayerFly);
		}
	}

	private static void updateAttribute(Player player, Holder<Attribute> attribute, AttributeModifier modifier, Predicate<Player> applyAttribute) {
		AttributeInstance attributeInstance = player.getAttribute(attribute);
		if (attributeInstance != null) {
			boolean hasModifier = attributeInstance.hasModifier(modifier.id());
			if (applyAttribute.test(player)) {
				if (!hasModifier) {
					attributeInstance.addTransientModifier(modifier);
				}
			} else if (hasModifier) {
				attributeInstance.removeModifier(modifier.id());
			}
		}
	}

	private static boolean shouldPlayerFly(Player player) {
		IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		IAbilityEMCProvider abilityEMC = player.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
		if (abilityEMC.getEmc().intValue() >= 32 || knowledge.getEmc().intValue() >= 1) {
			return true;
		}
		return false;
	}

	private static WalkOnType canWalkOnWater(Player player) {
		if (false) {//PlayerHelper.checkHotbarCurios(player, (p, stack) -> stack.is(PEItems.EVERTIDE_AMULET))) {
			return WalkOnType.ABLE_WITH_SPEED;
		}
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		return WalkOnType.UNABLE;//!helmet.isEmpty() && helmet.is(PEItems.GEM_HELMET) ? WalkOnType.ABLE : WalkOnType.UNABLE;
	}

	private static WalkOnType canWalkOnLava(Player player) {
		if (false) {//PlayerHelper.checkHotbarCurios(player, (p, stack) -> stack.is(PEItems.VOLCANITE_AMULET))) {
			return WalkOnType.ABLE_WITH_SPEED;
		}
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		return WalkOnType.UNABLE;//!chestplate.isEmpty() && chestplate.is(PEItems.GEM_CHESTPLATE) ? WalkOnType.ABLE : WalkOnType.UNABLE;
	}

	private enum WalkOnType {
		ABLE,
		ABLE_WITH_SPEED,
		UNABLE;

		public boolean canWalk() {
			return this != UNABLE;
		}

		public Predicate<Player> applySpeed(boolean onType) {
			if (onType && this == ABLE_WITH_SPEED) {
				return Predicates.alwaysTrue();
			}
			return Predicates.alwaysFalse();
		}
	}
}