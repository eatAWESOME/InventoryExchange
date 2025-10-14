package eatawesome.inventoryexchange.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import eatawesome.inventoryexchange.network.packets.to_server.InfiniteContainerScrollPKT;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class InfiniteContainerScreen extends AbstractContainerScreen<InfiniteContainer> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InventoryExchange.MODID, "textures/gui/infinitecontainer.png");

    private int localRowOffset;

    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_RIGHT_MARGIN = 4;

    private int scrollbarX;
    private int scrollbarTopY;
    private int scrollbarBottomY;

    public InfiniteContainerScreen(InfiniteContainer menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = Math.max(256, InfiniteContainer.CHEST_START_X + InfiniteContainer.COLS * InfiniteContainer.SLOT_SPACING + 8);
        this.imageHeight = 256;
        this.localRowOffset = menu.getRowOffset();
    }

    @Override
    protected void init() {
        super.init();
        this.scrollbarX = this.imageWidth - SCROLLBAR_RIGHT_MARGIN - SCROLLBAR_WIDTH;
        this.scrollbarTopY = InfiniteContainer.CHEST_START_Y;
        this.scrollbarBottomY = InfiniteContainer.CHEST_START_Y + (InfiniteContainer.VISIBLE_ROWS * InfiniteContainer.SLOT_SPACING) - 2;
        clampLocalOffset();
    }

    private int potentialRows() {
        return Math.max(10, Math.min(InfiniteContainer.MAX_ROWS, this.menu.getClientPotentialRows()));
    }

    private int maxOffset() {
        return Math.max(0, potentialRows() - InfiniteContainer.VISIBLE_ROWS);
    }

    private void clampLocalOffset() {
        if (localRowOffset < 0) localRowOffset = 0;
        int max = maxOffset();
        if (localRowOffset > max) localRowOffset = max;
    }

    private void sendOffsetToServer() {
        PacketDistributor.sendToServer(new InfiniteContainerScrollPKT(menu.containerId, localRowOffset));
    }

    private float scrollFraction() {
        int max = Math.max(1, maxOffset());
        return localRowOffset / (float) max;
    }

    private boolean overScrollbar(double mouseX, double mouseY) {
        int left = this.leftPos + scrollbarX;
        int top = this.topPos + scrollbarTopY;
        int height = scrollbarBottomY - scrollbarTopY;
        return mouseX >= left && mouseX < left + SCROLLBAR_WIDTH && mouseY >= top && mouseY < top + height;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.localRowOffset = this.menu.getRowOffset();
        clampLocalOffset();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        boolean overGrid =
            mouseX >= this.leftPos + InfiniteContainer.CHEST_START_X &&
            mouseX < this.leftPos + InfiniteContainer.CHEST_START_X + InfiniteContainer.COLS * InfiniteContainer.SLOT_SPACING &&
            mouseY >= this.topPos + InfiniteContainer.CHEST_START_Y &&
            mouseY < this.topPos + InfiniteContainer.CHEST_START_Y + InfiniteContainer.VISIBLE_ROWS * InfiniteContainer.SLOT_SPACING;

        if (overScrollbar(mouseX, mouseY) || overGrid) {
            int step = deltaY > 0 ? -1 : 1;
            localRowOffset += step;
            clampLocalOffset();
            sendOffsetToServer();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    private void scrollToFraction(float frac) {
        frac = Math.max(0F, Math.min(1F, frac));
        localRowOffset = Math.round(frac * maxOffset());
        clampLocalOffset();
        sendOffsetToServer();
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        gui.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Scrollbar
        int trackX = this.leftPos + scrollbarX;
        int trackY = this.topPos + scrollbarTopY;
        int trackH = (scrollbarBottomY - scrollbarTopY);

        int rows = potentialRows();
        // Scale handle height
        int handleH = Math.max(10, Math.min(trackH, Math.round(trackH * (InfiniteContainer.VISIBLE_ROWS / (float) rows))));
        int handleY = trackY + Math.round((trackH - handleH) * scrollFraction());

        // Track
        gui.fill(trackX, trackY, trackX + SCROLLBAR_WIDTH, trackY + trackH, 0x60202020);
        // Handle
        gui.fill(trackX + 1, handleY, trackX + SCROLLBAR_WIDTH - 1, handleY + handleH, 0xFF9A9A9A);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && overScrollbar(mouseX, mouseY)) {
            int top = this.topPos + scrollbarTopY;
            int bottom = this.topPos + scrollbarBottomY;
            float frac = (float) ((mouseY - top) / (bottom - top));
            scrollToFraction(frac);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && overScrollbar(mouseX, mouseY)) {
            int top = this.topPos + scrollbarTopY;
            int bottom = this.topPos + scrollbarBottomY;
            float frac = (float) ((mouseY - top) / (bottom - top));
            scrollToFraction(frac);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}