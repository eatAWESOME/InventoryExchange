package eatawesome.inventoryexchange.gameObjs.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;

public class TomeOfKnowledge extends Item {

	public TomeOfKnowledge(Properties props) {
		super(props);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltip, flags);
		tooltip.add(InventoryExchangeLang.TOOLTIP_TOME.translate());
	}
}