package eatawesome.inventoryexchange.gameObjs.container;

import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import eatawesome.inventoryexchange.handlers.ScrollingSlotItemHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InfiniteContainer extends AbstractContainerMenu {
    // Grid dimensions
    public static final int COLS = 13;
    public static final int TOTAL_ROWS = 15;
    public static final int VISIBLE_ROWS = 8;
    public static final int SLOT_COUNT = COLS * TOTAL_ROWS;

    // Chest grid origin (relative to GUI area)
    public static final int CHEST_START_X = 12;
    public static final int CHEST_START_Y = 5;
    public static final int SLOT_SPACING = 18;

    // Player inventory origin
    public static final int PLAYER_INV_START_X = 48;
    public static final int PLAYER_INV_START_Y = 152;

    private final IItemHandlerModifiable handler;

    // Current scroll offset in rows
    private int rowOffset = 0;

    public InfiniteContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, new ItemStackHandler(SLOT_COUNT));
    }

    public InfiniteContainer(int id, Inventory playerInventory, IItemHandlerModifiable handler) {
        super(InventoryExchangeContainerTypes.INFINITE_CONTAINER.get(), id);
        this.handler = handler;

        // Sync rowOffset (server -> client) using a DataSlot
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return rowOffset;
            }

            @Override
            public void set(int value) {
                rowOffset = clampRowOffset(value);
            }
        });

        final boolean clientSide = playerInventory.player.level().isClientSide;

        // Add only the visible window of chest slots
        for (int visibleRow = 0; visibleRow < VISIBLE_ROWS; visibleRow++) {
            for (int col = 0; col < COLS; col++) {
                int x = CHEST_START_X + col * SLOT_SPACING;
                int y = CHEST_START_Y + visibleRow * SLOT_SPACING;
                this.addSlot(new ScrollingSlotItemHandler(this, handler, col, visibleRow, x, y, clientSide));
            }
        }

        // Player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new net.minecraft.world.inventory.Slot(
                    playerInventory,
                    col + row * 9 + 9,
                    PLAYER_INV_START_X + col * SLOT_SPACING,
                    PLAYER_INV_START_Y + row * SLOT_SPACING
                ));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new net.minecraft.world.inventory.Slot(
                playerInventory,
                col,
                PLAYER_INV_START_X + col * SLOT_SPACING,
                PLAYER_INV_START_Y + 58
            ));
        }
    }

    public int getMaxRowOffset() {
        return Math.max(0, TOTAL_ROWS - VISIBLE_ROWS);
    }

    public int clampRowOffset(int value) {
        if (value < 0) return 0;
        int max = getMaxRowOffset();
        if (value > max) return max;
        return value;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    /**
     * Updates the current row offset on this side (client or server).
     * Call broadcastChanges() from the server after modifying, so clients receive slot contents updates.
     */
    public void setRowOffset(int value) {
        this.rowOffset = clampRowOffset(value);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Shift-click behavior:
     * - From chest (visible window) -> to player inventory and hotbar
     * - From player inventory/hotbar -> insert across the entire handler (all 195 slots), not only visible ones
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        final int chestVisibleSlots = VISIBLE_ROWS * COLS;
        final int playerInvStart = chestVisibleSlots;
        final int playerInvEnd = playerInvStart + 27;
        final int hotbarStart = playerInvEnd;
        final int hotbarEnd = hotbarStart + 9;

        ItemStack empty = ItemStack.EMPTY;
        if (index < 0 || index >= this.slots.size()) {
            return empty;
        }
        net.minecraft.world.inventory.Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return empty;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack original = stackInSlot.copy();

        if (index < chestVisibleSlots) {
            // From chest visible window -> player inventory first, then hotbar
            if (!this.moveItemStackTo(stackInSlot, playerInvStart, playerInvEnd, false)) {
                if (!this.moveItemStackTo(stackInSlot, hotbarStart, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
            // From player -> insert into entire backing handler
            if (!insertIntoHandler(handler, stackInSlot)) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stackInSlot);
        return original;
    }

    /**
     * Insert stack into the entire handler range:
     * - Merge pass over matching stacks (respect slot limits)
     * - Fill pass over empty slots (respect isItemValid and limits)
     */
    private static boolean insertIntoHandler(IItemHandlerModifiable handler, ItemStack stack) {
        if (stack.isEmpty()) return false;
        boolean changed = false;
        int slots = handler.getSlots();

        // Merge into existing stacks
        for (int i = 0; i < slots && !stack.isEmpty(); i++) {
            ItemStack cur = handler.getStackInSlot(i);
            if (cur.isEmpty()) continue;
            if (!ItemStack.isSameItemSameComponents(cur, stack)) continue;

            int limit = Math.min(cur.getMaxStackSize(), handler.getSlotLimit(i));
            if (cur.getCount() >= limit) continue;

            int canAdd = Math.min(limit - cur.getCount(), stack.getCount());
            if (canAdd <= 0) continue;

            ItemStack newStack = cur.copy();
            newStack.grow(canAdd);
            handler.setStackInSlot(i, newStack);
            stack.shrink(canAdd);
            changed = true;
        }

        // Fill empty slots
        for (int i = 0; i < slots && !stack.isEmpty(); i++) {
            ItemStack cur = handler.getStackInSlot(i);
            if (!cur.isEmpty()) continue;
            if (!handler.isItemValid(i, stack)) continue;

            int toMove = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), handler.getSlotLimit(i)));
            if (toMove <= 0) continue;

            ItemStack place = stack.copy();
            place.setCount(toMove);
            handler.setStackInSlot(i, place);
            stack.shrink(toMove);
            changed = true;
        }
        return changed;
    }
}