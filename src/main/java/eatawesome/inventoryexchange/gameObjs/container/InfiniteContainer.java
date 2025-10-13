package eatawesome.inventoryexchange.gameObjs.container;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;

public class InfiniteContainer extends AbstractContainerMenu {
    public static final int ROWS = 8;
    public static final int COLS = 13;
    public static final int SLOT_COUNT = ROWS * COLS;

    private final SimpleContainer infiniteInventory;

    public InfiniteContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, null);
    }

    public InfiniteContainer(int id, Inventory playerInventory, SimpleContainer infiniteInventory) {
        super(InventoryExchangeContainerTypes.INFINITE_CONTAINER.get(), id);
        this.infiniteInventory = infiniteInventory == null ? new SimpleContainer(SLOT_COUNT) : infiniteInventory;

        // Chest slots: (12, 5) start, 18px spacing
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                this.addSlot(new Slot(this.infiniteInventory, j + i * COLS, 12 + j * 18, 5 + i * 18));
            }
        }

        // Player inventory: starts at (48, 152)
        int playerInvStartX = 48;
        int playerInvStartY = 152;
        // Main 3 rows
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, playerInvStartX + col * 18, playerInvStartY + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, playerInvStartX + col * 18, playerInvStartY + 58));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}