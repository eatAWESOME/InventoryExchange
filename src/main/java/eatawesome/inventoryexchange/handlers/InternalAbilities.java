package eatawesome.inventoryexchange.handlers;

import java.util.function.Predicate;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

public class InternalAbilities {

	private static final AttributeModifier FLIGHT = new AttributeModifier(InventoryExchange.rl("flight"), 1, Operation.ADD_VALUE);
	
	public static void tick(Player player) {
		if (!player.level().isClientSide) {
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
}