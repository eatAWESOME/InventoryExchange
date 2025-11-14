package eatawesome.inventoryexchange.network.packets.to_server;

import io.netty.buffer.ByteBuf;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.impl.capability.InfiniteContainerImpl;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record InfiniteContainerSortPKT() implements IInventoryExchangePacket {

    public static final CustomPacketPayload.Type<InfiniteContainerSortPKT> TYPE =
        new CustomPacketPayload.Type<>(InventoryExchange.rl("infinite_container_sort"));

    public static final StreamCodec<ByteBuf, InfiniteContainerSortPKT> STREAM_CODEC =
        StreamCodec.of((buf, pkt) -> {}, buf -> new InfiniteContainerSortPKT());

    @Override
    public CustomPacketPayload.Type<InfiniteContainerSortPKT> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
        if (player instanceof ServerPlayer serverPlayer) {
        	IItemHandlerModifiable handler = InfiniteContainerHelper.getInventory(serverPlayer);
            if (handler == null) {
                return;
            }

            // Compact and sort
            List<ItemStack> all = new ArrayList<>();
            for (int i = 0, slots = handler.getSlots(); i < slots; i++) {
                ItemStack s = handler.getStackInSlot(i);
                if (!s.isEmpty()) {
                    all.add(s.copy());
                }
            }

            // Clear
            for (int i = 0, slots = handler.getSlots(); i < slots; i++) {
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }

            List<ItemStack> compacted = compactStacks(all);
            compacted.sort(stackComparator());

            // Refill
            int idx = 0;
            int totalSlots = handler.getSlots();
            for (ItemStack s : compacted) {
                if (idx >= totalSlots) break;
                handler.setStackInSlot(idx++, s);
            }

            // Sync back to client
            new InfiniteContainerImpl(serverPlayer).sync(serverPlayer);
        }
        
    }

    private static Comparator<ItemStack> stackComparator() {
        return Comparator
            // primary: registry name (stable, non-localized)
            .comparing((ItemStack s) -> BuiltInRegistries.ITEM.getKey(s.getItem()).toString())
            // secondary: description id (e.g., item.modid.name)
            .thenComparing(ItemStack::getDescriptionId)
            // tertiary: count descending so fuller stacks come first within same item
            .thenComparing((ItemStack s) -> -s.getCount());
    }

    private static List<ItemStack> compactStacks(List<ItemStack> input) {
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack src : input) {
            if (src.isEmpty()) continue;
            ItemStack work = src.copy();

            for (ItemStack existing : out) {
                if (ItemStack.isSameItemSameComponents(existing, work)) {
                    int limit = existing.getMaxStackSize();
                    int canAdd = Math.min(limit - existing.getCount(), work.getCount());
                    if (canAdd > 0) {
                        existing.grow(canAdd);
                        work.shrink(canAdd);
                        if (work.isEmpty()) break;
                    }
                }
            }

            while (!work.isEmpty()) {
                int limit = work.getMaxStackSize();
                int toMove = Math.min(limit, work.getCount());
                ItemStack part = work.copy();
                part.setCount(toMove);
                out.add(part);
                work.shrink(toMove);
            }
        }
        return out;
    }
}