package eatawesome.inventoryexchange.handlers;

import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * Slot that proxies a dynamic handler index computed from the menu's rowOffset.
 * On the client, avoid writing to the handler during network slot updates to prevent
 * "invisible before replaced" flicker. We keep a clientStack cache for display.
 * On the server, we forward reads/writes to the handler.
 */
public class ScrollingSlotItemHandler extends Slot {

    private final InfiniteContainer menu;
    private final IItemHandler handler;
    private final int col;
    private final int visibleRow;
    private final boolean clientSide;

    public ScrollingSlotItemHandler(InfiniteContainer menu, IItemHandler handler, int col, int visibleRow, int x, int y, boolean clientSide) {
        super(new OneSlotContainer(), 0, x, y);
        this.menu = menu;
        this.handler = handler;
        this.col = col;
        this.visibleRow = visibleRow;
        this.clientSide = clientSide;
        ((OneSlotContainer) this.container).clientStack = ItemStack.EMPTY;
    }

    private int realIndex() {
        int row = menu.getRowOffset() + visibleRow;
        return col + row * InfiniteContainer.COLS;
    }

    private boolean inBounds(int idx) {
        return idx >= 0 && idx < handler.getSlots();
    }

    private static ItemStack copyWithCount(ItemStack stack, int count) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        int idx = realIndex();
        return inBounds(idx) && handler.isItemValid(idx, stack);
    }

    @Override
    public ItemStack getItem() {
        if (clientSide) {
            return ((OneSlotContainer) this.container).clientStack;
        }
        int idx = realIndex();
        return inBounds(idx) ? handler.getStackInSlot(idx) : ItemStack.EMPTY;
    }

    @Override
    public void set(ItemStack stack) {
        if (clientSide) {
            ((OneSlotContainer) this.container).clientStack = stack.copy();
            setChanged();
            return;
        }
        int idx = realIndex();
        if (inBounds(idx) && handler instanceof IItemHandlerModifiable mod) {
            mod.setStackInSlot(idx, stack);
            setChanged();
        }
    }

    @Override
    public void setByPlayer(ItemStack stack) {
        set(stack);
    }

    @Override
    public int getMaxStackSize() {
        int idx = realIndex();
        return inBounds(idx) ? handler.getSlotLimit(idx) : 0;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(getMaxStackSize(), stack.getMaxStackSize());
    }

    @Override
    public ItemStack remove(int amount) {
        if (clientSide) {
            ItemStack cur = ((OneSlotContainer) this.container).clientStack;
            if (cur.isEmpty() || amount <= 0) return ItemStack.EMPTY;
            int toRemove = Math.min(amount, cur.getCount());
            ItemStack res = copyWithCount(cur, toRemove);
            ItemStack newStack = cur.copy();
            newStack.shrink(toRemove);
            ((OneSlotContainer) this.container).clientStack = newStack;
            setChanged();
            return res;
        }
        int idx = realIndex();
        if (!inBounds(idx) || amount <= 0) return ItemStack.EMPTY;
        ItemStack current = handler.getStackInSlot(idx);
        if (current.isEmpty()) return ItemStack.EMPTY;
        int toRemove = Math.min(amount, current.getCount());
        ItemStack extracted = copyWithCount(current, toRemove);
        if (handler instanceof IItemHandlerModifiable mod) {
            ItemStack newStack = current.copy();
            newStack.shrink(toRemove);
            mod.setStackInSlot(idx, newStack);
            setChanged();
        }
        return extracted;
    }

    @Override
    public boolean mayPickup(Player player) {
        return !getItem().isEmpty();
    }

    @Override
    public void setChanged() {
        this.container.setChanged();
    }

    /**
     * Minimal container used only to carry a client-side stack cache for rendering.
     * On the server, all reads/writes go to the handler directly; we don't use this container's storage.
     */
    private static class OneSlotContainer implements Container {
        private ItemStack clientStack = ItemStack.EMPTY;

        @Override public int getContainerSize() { return 1; }
        @Override public boolean isEmpty() { return clientStack.isEmpty(); }
        @Override public ItemStack getItem(int slot) { return clientStack; }
        @Override public ItemStack removeItem(int slot, int amount) {
            if (clientStack.isEmpty() || amount <= 0) return ItemStack.EMPTY;
            int toRemove = Math.min(amount, clientStack.getCount());
            ItemStack res = clientStack.copy();
            res.setCount(toRemove);
            ItemStack newStack = clientStack.copy();
            newStack.shrink(toRemove);
            clientStack = newStack;
            return res;
        }
        @Override public ItemStack removeItemNoUpdate(int slot) {
            ItemStack cur = clientStack;
            clientStack = ItemStack.EMPTY;
            return cur;
        }
        @Override public void setItem(int slot, ItemStack stack) { clientStack = stack; }
        @Override public void setChanged() {}
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() { clientStack = ItemStack.EMPTY; }
    }
}