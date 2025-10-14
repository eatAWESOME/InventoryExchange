package eatawesome.inventoryexchange.network.packets.to_client.infinite_container;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.impl.capability.InfiniteContainerImpl;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Server -> client packet: carries the InfiniteContainerAttachment and copies its contents
 * into the client's attachment instance.
 *
 * Implements IInventoryExchangePacket so it integrates with your CustomPacketPayload handling.
 */
public record InfiniteContainerSyncPKT(InfiniteContainerImpl.InfiniteContainerAttachment attachment) implements IInventoryExchangePacket {

    public static final CustomPacketPayload.Type<InfiniteContainerSyncPKT> TYPE =
        new CustomPacketPayload.Type<>(InventoryExchange.rl("infinite_container_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfiniteContainerSyncPKT> STREAM_CODEC =
        InfiniteContainerImpl.STREAM_CODEC.map(InfiniteContainerSyncPKT::new, InfiniteContainerSyncPKT::attachment);

    @NotNull
    @Override
    public CustomPacketPayload.Type<InfiniteContainerSyncPKT> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player clientPlayer = context.player();
            if (clientPlayer == null) {
                return;
            }
            var clientAttachment = clientPlayer.getData(InventoryExchangeAttachmentTypes.INFINITE_CONTAINER);
            if (clientAttachment == null) {
                return;
            }
            ItemStackHandler incoming = attachment().inventory;
            ItemStackHandler target = clientAttachment.inventory;
            int min = Math.min(incoming.getSlots(), target.getSlots());
            for (int i = 0; i < min; i++) {
                ItemStack stack = incoming.getStackInSlot(i);
                target.setStackInSlot(i, stack == null ? net.minecraft.world.item.ItemStack.EMPTY : stack.copy());
            }
        });
        // IPayloadContext does not expose setPacketHandled; handled by the network infra
    }
}