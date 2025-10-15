package eatawesome.inventoryexchange.gameObjs.gui;

import java.math.BigInteger;
import java.text.NumberFormat;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PortableFurnaceScreen extends AbstractContainerScreen<PortableFurnaceMenu> {

	// Textures
	private static final ResourceLocation FURNACE_TEX =
		ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
	private static final ResourceLocation LIT_PROGRESS_SPRITE =
		ResourceLocation.fromNamespaceAndPath("minecraft", "container/furnace/lit_progress");
	private static final ResourceLocation BURN_PROGRESS_SPRITE =
		ResourceLocation.fromNamespaceAndPath("minecraft", "container/furnace/burn_progress");

	public PortableFurnaceScreen(PortableFurnaceMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;

		// Base background
		gui.blit(FURNACE_TEX, x, y, 0, 0, this.imageWidth, this.imageHeight);

		// Flame: always lit and full
		gui.blitSprite(LIT_PROGRESS_SPRITE, x + 56, y + 36, 14, 14);

		// Burn progress: full when input present
		boolean hasInput = this.menu.getSlot(0) != null && this.menu.getSlot(0).hasItem();
		if (hasInput) {
			gui.blitSprite(BURN_PROGRESS_SPRITE, x + 79, y + 34, 24, 16);
		}
	}

	@Override
	protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
		int titleX = (this.imageWidth - this.font.width(this.title)) / 2;
		gui.drawString(this.font, this.title, titleX, 6, 0x404040, false);
		gui.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, 0x404040, false);

		BigInteger emc = getClientEmc();
		String formatted = NumberFormat.getIntegerInstance().format(emc);
		gui.drawString(this.font, Component.literal("EMC:"), 80, this.imageHeight - 106, 0x404040, false);
		gui.drawString(this.font, Component.literal(formatted), 80, this.imageHeight - 94, 0x404040, false);
	}

	private BigInteger getClientEmc() {
		IKnowledgeProvider kp = this.minecraft.player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (kp == null) return BigInteger.ZERO;
		try { return kp.getEmc(); } catch (Throwable t) { return BigInteger.ZERO; }
	}
}