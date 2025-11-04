package eatawesome.inventoryexchange.client;

import eatawesome.inventoryexchange.client.toast.KnowledgeToast;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public final class ClientHooks {

	private ClientHooks() {}

	public static void showKnowledgeToast(ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		if (mc != null) {
			mc.getToasts().addToast(new KnowledgeToast(stack.copyWithCount(1)));
		}
	}
}