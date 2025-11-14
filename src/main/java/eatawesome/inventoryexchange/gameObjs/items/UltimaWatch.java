package eatawesome.inventoryexchange.gameObjs.items;

import eatawesome.inventoryexchange.utils.UltimaWatchMode;
import eatawesome.inventoryexchange.config.InventoryExchangeCommonConfig;
import java.math.BigInteger;
import java.util.List;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class UltimaWatch extends Item {
	
	private static final int TIME_CHANGE_COST = 4;

	public UltimaWatch() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player instanceof ServerPlayer serverPlayer) {
			if (!InventoryExchangeCommonConfig.isUltimaWatchAllowed()) {
				serverPlayer.sendSystemMessage(PELang.TIME_WATCH_DISABLED.translate());
				return InteractionResultHolder.fail(stack);
			}
			UltimaWatchMode.nextValue(serverPlayer);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		super.inventoryTick(stack, level, entity, slot, isHeld);
		if (entity instanceof ServerPlayer serverPlayer && hotBarOrOffHand(slot) && level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT) && InventoryExchangeCommonConfig.isUltimaWatchAllowed()) {
			int ultimaWatchMode = UltimaWatchMode.getMode(serverPlayer);
			if (ultimaWatchMode != 0) {
				IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
				if (knowledge.getEmc().intValue() >= TIME_CHANGE_COST) {
					int emc = knowledge.getEmc().intValue() - TIME_CHANGE_COST;
					knowledge.setEmc(BigInteger.valueOf(emc));
					knowledge.syncEmc(serverPlayer);
					ServerLevel serverWorld = (ServerLevel) level;
					long timeDelta = 10L;
					if (ultimaWatchMode == 1) {// Fast forward
						if (level.getDayTime() > Long.MAX_VALUE - timeDelta) { // (would go past max long)
							serverWorld.setDayTime(Long.MAX_VALUE);
						} else {
							serverWorld.setDayTime(level.getDayTime() + timeDelta);
						}
					} else if (ultimaWatchMode == 2) { // Rewind
						serverWorld.setDayTime(Math.max(level.getDayTime() - timeDelta, 0));
					}
				}
			}
		}
	}


	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltip, flags);
		tooltip.add(PELang.TOOLTIP_TIME_WATCH_1.translate());
		tooltip.add(PELang.TOOLTIP_TIME_WATCH_2.translate(Component.keybind("key.use")));
		//tooltip.add(PELang.TIME_WATCH_MODE.translate(UltimaWatchMode.getModeName(player)));
	}


	public static boolean hotBarOrOffHand(int slot) {
		return slot < Inventory.getSelectionSize() || slot == Inventory.SLOT_OFFHAND;
	}
}