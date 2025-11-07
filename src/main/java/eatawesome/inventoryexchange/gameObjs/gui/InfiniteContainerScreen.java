package eatawesome.inventoryexchange.gameObjs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainerMenu;
import eatawesome.inventoryexchange.network.packets.to_server.InfiniteContainerScrollPKT;
import eatawesome.inventoryexchange.network.packets.to_server.InfiniteContainerSortPKT;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class InfiniteContainerScreen extends AbstractContainerScreen<InfiniteContainerMenu> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InventoryExchange.MODID, "textures/gui/infinitecontainer.png");

    private int localRowOffset;

    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_RIGHT_MARGIN = 4;

    private int scrollbarX;
    private int scrollbarTopY;
    private int scrollbarBottomY;

    // Controls
    private Button sortButton;

    public InfiniteContainerScreen(InfiniteContainerMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = Math.max(256,
            InfiniteContainerMenu.CHEST_START_X + InfiniteContainerMenu.COLS * InfiniteContainerMenu.SLOT_SPACING + 8);
        this.imageHeight = 256;
        this.localRowOffset = menu.getRowOffset();
    }
    
    @Override
    protected void init() {
        super.init();
        this.scrollbarX = this.imageWidth - SCROLLBAR_RIGHT_MARGIN - SCROLLBAR_WIDTH;
        this.scrollbarTopY = InfiniteContainerMenu.CHEST_START_Y;
        this.scrollbarBottomY = InfiniteContainerMenu.CHEST_START_Y +
            (InfiniteContainerMenu.VISIBLE_ROWS * InfiniteContainerMenu.SLOT_SPACING) - 2;

        // Button
        int btnBase = InfiniteContainerMenu.SLOT_SPACING;
        int btnWidth = btnBase + 15;
        int btnHeight = btnBase;
        int btnRight = this.leftPos + this.imageWidth - 4;
        int btnX = (btnRight - btnWidth) - 2;
        int btnY = (this.topPos + InfiniteContainerMenu.PLAYER_INV_START_Y) - 1;
        this.sortButton = Button.builder(InventoryExchangeLang.INFINITE_CONTAINER_SORT.translate(), b -> {
            PacketDistributor.sendToServer(new InfiniteContainerSortPKT());
            this.setFocused(null);
            b.setFocused(false);
        }).bounds(btnX, btnY, btnWidth, btnHeight).build();
        this.addRenderableWidget(this.sortButton);

        clampLocalOffset();
    }

    private int potentialRows() {
        int rows = menu.getClientPotentialRows();
        if (rows < 10) rows = 10;
        int configured = InfiniteContainerMenu.configMaxRows();
        if (rows > configured) rows = configured;
        return rows;
    }

    private int maxOffset() {
        return Math.max(0, potentialRows() - InfiniteContainerMenu.VISIBLE_ROWS);
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
        if (this.sortButton != null) this.sortButton.setFocused(false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        boolean overGrid =
            mouseX >= this.leftPos + InfiniteContainerMenu.CHEST_START_X &&
            mouseX < this.leftPos + InfiniteContainerMenu.CHEST_START_X +
                InfiniteContainerMenu.COLS * InfiniteContainerMenu.SLOT_SPACING &&
            mouseY >= this.topPos + InfiniteContainerMenu.CHEST_START_Y &&
            mouseY < this.topPos + InfiniteContainerMenu.CHEST_START_Y +
                InfiniteContainerMenu.VISIBLE_ROWS * InfiniteContainerMenu.SLOT_SPACING;

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
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.render(gui, mouseX, mouseY, partialTicks);
        this.renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        gui.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int trackX = this.leftPos + scrollbarX;
        int trackY = this.topPos + scrollbarTopY;
        int trackH = scrollbarBottomY - scrollbarTopY;

        int rows = potentialRows();
        int handleH = Math.max(10,
            Math.min(trackH, Math.round(trackH * (InfiniteContainerMenu.VISIBLE_ROWS / (float) rows))));
        int handleY = trackY + Math.round((trackH - handleH) * scrollFraction());

        gui.fill(trackX, trackY, trackX + SCROLLBAR_WIDTH, trackY + trackH, 0x60202020);
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
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean r = super.mouseReleased(mouseX, mouseY, button);
        if (this.sortButton != null) this.sortButton.setFocused(false);
        this.setFocused(null);
        return r;
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