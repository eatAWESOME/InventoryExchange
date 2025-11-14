package eatawesome.inventoryexchange.gameObjs.container;

import eatawesome.inventoryexchange.config.InventoryExchangeCommonConfig;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeContainerTypes;
import eatawesome.inventoryexchange.handlers.ScrollingSlotItemHandler;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InfiniteContainerMenu extends AbstractContainerMenu {

    public static final int COLS = 13;
    public static final int VISIBLE_ROWS = 8;

    // layout
    public static final int CHEST_START_X = 12;
    public static final int CHEST_START_Y = 5;
    public static final int SLOT_SPACING = 18;

    public static final int PLAYER_INV_START_X = 48;
    public static final int PLAYER_INV_START_Y = 152;

    private final IItemHandlerModifiable handler;
    private final boolean serverSide;

    private int rowOffset = 0;
    private int clientPotentialRows = 10;

    public InfiniteContainerMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, InfiniteContainerHelper.getInventory(playerInventory.player));
    }

    public InfiniteContainerMenu(int id, Inventory playerInventory, IItemHandlerModifiable handler) {
        super(InventoryExchangeContainerTypes.INFINITE_CONTAINER.get(), id);
        this.handler = handler != null ? handler : new ItemStackHandler(totalSlotCount());
        this.serverSide = !playerInventory.player.level().isClientSide;

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() { return rowOffset; }
            @Override
            public void set(int value) { rowOffset = clampRowOffset(value); }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() { return serverSide ? computePotentialRowsServer() : clientPotentialRows; }
            @Override
            public void set(int value) {
                clientPotentialRows = Math.max(10, Math.min(configMaxRows(), value));
                rowOffset = clampRowOffset(rowOffset);
            }
        });

        boolean clientSide = playerInventory.player.level().isClientSide;
        for (int visibleRow = 0; visibleRow < VISIBLE_ROWS; visibleRow++) {
            for (int col = 0; col < COLS; col++) {
                int x = CHEST_START_X + col * SLOT_SPACING;
                int y = CHEST_START_Y + visibleRow * SLOT_SPACING;
                this.addSlot(new ScrollingSlotItemHandler(this, handler, col, visibleRow, x, y, clientSide));
            }
        }

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
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new net.minecraft.world.inventory.Slot(
                playerInventory,
                col,
                PLAYER_INV_START_X + col * SLOT_SPACING,
                PLAYER_INV_START_Y + 58
            ));
        }
    }

    public static int configMaxRows() {
        return InventoryExchangeCommonConfig.getMaxInfiniteContainerRows();
    }

    public static int totalSlotCount() {
        return configMaxRows() * COLS;
    }

    private int computePotentialRowsServer() {
        int highest = -1;
        int slots = handler.getSlots();
        for (int i = slots - 1; i >= 0; i--) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                highest = i;
                break;
            }
        }
        int highestRow = highest < 0 ? -1 : highest / COLS;
        int proposed = highestRow + 1 + 2;
        if (proposed < 10) proposed = 10;
        int max = configMaxRows();
        if (proposed > max) proposed = max;
        return proposed;
    }

    private int effectivePotentialRows() {
        return serverSide ? computePotentialRowsServer() : clientPotentialRows;
    }

    public int getClientPotentialRows() {
        return clientPotentialRows;
    }

    public int getMaxRowOffset() {
        return Math.max(0, effectivePotentialRows() - VISIBLE_ROWS);
    }

    public int clampRowOffset(int value) {
        if (value < 0) return 0;
        int max = getMaxRowOffset();
        return value > max ? max : value;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public void setRowOffset(int value) {
        this.rowOffset = clampRowOffset(value);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        final int chestVisibleSlots = VISIBLE_ROWS * COLS;
        final int playerInvStart = chestVisibleSlots;
        final int playerInvEnd = playerInvStart + 27;
        final int hotbarStart = playerInvEnd;
        final int hotbarEnd = hotbarStart + 9;

        ItemStack empty = ItemStack.EMPTY;
        if (index < 0 || index >= this.slots.size()) return empty;
        net.minecraft.world.inventory.Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return empty;

        ItemStack stackInSlot = slot.getItem();
        ItemStack original = stackInSlot.copy();

        if (index < chestVisibleSlots) {
            if (!this.moveItemStackTo(stackInSlot, playerInvStart, playerInvEnd, false)) {
                if (!this.moveItemStackTo(stackInSlot, hotbarStart, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
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

    private static boolean insertIntoHandler(IItemHandlerModifiable handler, ItemStack stack) {
        if (stack.isEmpty()) return false;
        boolean changed = false;
        int slots = handler.getSlots();

        for (int i = 0; i < slots && !stack.isEmpty(); i++) {
            ItemStack cur = handler.getStackInSlot(i);
            if (cur.isEmpty()) continue;
            if (!ItemStack.isSameItemSameComponents(cur, stack)) continue;

            int limit = Math.min(cur.getMaxStackSize(), handler.getSlotLimit(i));
            if (cur.getCount() >= limit) continue;

            int add = Math.min(limit - cur.getCount(), stack.getCount());
            if (add <= 0) continue;

            ItemStack merged = cur.copy();
            merged.grow(add);
            handler.setStackInSlot(i, merged);
            stack.shrink(add);
            changed = true;
        }

        for (int i = 0; i < slots && !stack.isEmpty(); i++) {
            ItemStack cur = handler.getStackInSlot(i);
            if (!cur.isEmpty()) continue;
            if (!handler.isItemValid(i, stack)) continue;

            int placeCount = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), handler.getSlotLimit(i)));
            if (placeCount <= 0) continue;

            ItemStack place = stack.copy();
            place.setCount(placeCount);
            handler.setStackInSlot(i, place);
            stack.shrink(placeCount);
            changed = true;
        }
        return changed;
    }
}