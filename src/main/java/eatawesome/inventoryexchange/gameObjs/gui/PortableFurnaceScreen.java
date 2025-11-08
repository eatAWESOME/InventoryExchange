package eatawesome.inventoryexchange.gameObjs.gui;

import java.math.BigInteger;
import java.text.NumberFormat;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.utils.PortableFurnaceHelper;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

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
		
		// Flame
		boolean canAfford = PortableFurnaceHelper.canAfford(this.minecraft.player);
		if (canAfford) {
			gui.blitSprite(LIT_PROGRESS_SPRITE, x + 56, y + 36, 14, 14);
		}
		
		// Coal
		gui.renderItem(new ItemStack(Items.COAL), this.leftPos + 56, this.topPos + 53);
		gui.renderItemDecorations(this.font, ItemStack.EMPTY, this.leftPos + 56, this.topPos + 53, "");

		// Burn progress: full when input present
		boolean hasInput = this.menu.getSlot(0) != null && this.menu.getSlot(0).hasItem();
		if (hasInput && canAfford) {
			boolean inputSmeltable = this.minecraft.level.getRecipeManager()
					.getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(this.menu.getSlot(0).getItem().copyWithCount(1)), this.minecraft.level)
					.isPresent();
			if (inputSmeltable) {
				gui.blitSprite(BURN_PROGRESS_SPRITE, x + 79, y + 34, 24, 16);
			}
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
		IKnowledgeProvider knowledge = this.minecraft.player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge == null) return BigInteger.ZERO;
		try {
			return knowledge.getEmc();
		} catch (Throwable t) {
			return BigInteger.ZERO;
		}
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
		int fuelX = this.leftPos + 55;
		int fuelY = this.topPos + 52;
		if (mouseX >= fuelX && mouseX < fuelX + 18 && mouseY >= fuelY && mouseY < fuelY + 18) {
			return;
		}
		super.renderTooltip(graphics, mouseX, mouseY);
	}
}