package eatawesome.inventoryexchange.gameObjs.items;

import eatawesome.inventoryexchange.utils.UltimaWatchMode;
import java.util.List;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class UltimaWatch extends Item {

	public UltimaWatch(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player instanceof ServerPlayer serverPlayer) {
			if (!true) {//ProjectEConfig.server.items.enableTimeWatch.get()) {
				player.sendSystemMessage(PELang.TIME_WATCH_DISABLED.translate());
				return InteractionResultHolder.fail(stack);
			}
			UltimaWatchMode.nextValue(serverPlayer);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		super.inventoryTick(stack, level, entity, slot, isHeld);
		if (entity instanceof ServerPlayer serverPlayer && hotBarOrOffHand(slot) && level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {// || ProjectEConfig.server.items.enableTimeWatch.get()) {
			int ultimaWatchMode = UltimaWatchMode.getMode(serverPlayer);
			if (ultimaWatchMode != 0) {
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
				// 2 EMC/tick
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