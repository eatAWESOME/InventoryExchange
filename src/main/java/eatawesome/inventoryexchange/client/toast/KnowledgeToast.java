package eatawesome.inventoryexchange.client.toast;

import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class KnowledgeToast implements Toast {

	private static final MutableComponent TITLE = InventoryExchangeLang.KNOWLEDGE_LEARNED.translate();
	private static final ResourceLocation BG_SPRITE = ResourceLocation.fromNamespaceAndPath("minecraft", "toast/recipe");
	
	private final ItemStack icon;
	private long start = -1L;

	public KnowledgeToast(ItemStack icon) {
		this.icon = icon.copyWithCount(1);
	}

	@Override
	public Visibility render(GuiGraphics gui, ToastComponent toasts, long time) {
		if (start == -1L) start = time;

		gui.blitSprite(BG_SPRITE, 0, 0, width(), height());
		gui.renderItem(this.icon, 8, 8);

		var font = toasts.getMinecraft().font;
		int textX = 30;
		int textY = 12;
		int available = Math.max(1, width() - (textX + 8));
		int titleWidth = Math.max(1, font.width(TITLE));
		float scale = Math.min(1.0F, available / (float) titleWidth);

		var pose = gui.pose();
		pose.pushPose();
		pose.translate(textX, textY, 0);
		pose.scale(scale, scale, 1.0F);
		gui.drawString(font, TITLE, 0, 0, 0x3F3F3F, false);
		pose.popPose();

		return time - start >= 3000L ? Visibility.HIDE : Visibility.SHOW;
	}

	@Override
	public int width() { return 160; }

	@Override
	public int height() { return 32; }
}