package eatawesome.inventoryexchange.network.packets.to_client;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record PortableFurnaceSyncPKT(PortableFurnaceImpl.PortableFurnaceAttachment attachment)
    implements IInventoryExchangePacket {

    public static final CustomPacketPayload.Type<PortableFurnaceSyncPKT> TYPE =
        new CustomPacketPayload.Type<>(InventoryExchange.rl("portable_furnace_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PortableFurnaceSyncPKT> STREAM_CODEC =
        PortableFurnaceImpl.streamCodec().map(PortableFurnaceSyncPKT::new, PortableFurnaceSyncPKT::attachment);

    @Override
    public @NotNull CustomPacketPayload.Type<PortableFurnaceSyncPKT> type() { return TYPE; }

    @Override
    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player == null) return;
            var clientAttachment = player.getData(InventoryExchangeAttachmentTypes.PORTABLE_FURNACE);
            if (clientAttachment == null) return;

            ItemStackHandler incoming = attachment().inventory;
            ItemStackHandler target = clientAttachment.inventory;

            int copySlots = Math.min(incoming.getSlots(), target.getSlots());
            for (int i = 0; i < copySlots; i++) {
                if (i == 1) continue;
                target.setStackInSlot(i, incoming.getStackInSlot(i).copy());
            }
        });
    }
}