package eatawesome.inventoryexchange.gameObjs.gui;

import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InfiniteContainerScreen extends AbstractContainerScreen<InfiniteContainer> {
	
	private static final ResourceLocation texture = InventoryExchange.rl("textures/gui/infinitecontainer.png");
	
    public InfiniteContainerScreen(InfiniteContainer menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 255;
		this.imageHeight = 230;
    }

    @Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
		graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
    
    @Override
	protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
	}
}