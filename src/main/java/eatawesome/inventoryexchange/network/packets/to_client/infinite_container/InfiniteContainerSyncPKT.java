package eatawesome.inventoryexchange.network.packets.to_client.infinite_container;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.impl.capability.InfiniteContainerImpl;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record InfiniteContainerSyncPKT(InfiniteContainerImpl.InfiniteContainerAttachment attachment)
    implements IInventoryExchangePacket {

    public static final CustomPacketPayload.Type<InfiniteContainerSyncPKT> TYPE =
        new CustomPacketPayload.Type<>(InventoryExchange.rl("infinite_container_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfiniteContainerSyncPKT> STREAM_CODEC =
        InfiniteContainerImpl.streamCodec().map(InfiniteContainerSyncPKT::new, InfiniteContainerSyncPKT::attachment);

    @Override
    public @NotNull CustomPacketPayload.Type<InfiniteContainerSyncPKT> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player == null) return;
            var clientAttachment = player.getData(InventoryExchangeAttachmentTypes.INFINITE_CONTAINER);
            if (clientAttachment == null) return;

            ItemStackHandler incoming = attachment().inventory;
            ItemStackHandler target = clientAttachment.inventory;

            int copySlots = Math.min(incoming.getSlots(), target.getSlots());
            for (int i = 0; i < copySlots; i++) {
                target.setStackInSlot(i, incoming.getStackInSlot(i).copy());
            }
        });
    }
}