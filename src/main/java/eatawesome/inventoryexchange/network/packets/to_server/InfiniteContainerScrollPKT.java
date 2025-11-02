package eatawesome.inventoryexchange.network.packets.to_server;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainerMenu;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record InfiniteContainerScrollPKT(int windowId, int rowOffset) implements IInventoryExchangePacket {

    public static final CustomPacketPayload.Type<InfiniteContainerScrollPKT> TYPE =
        new CustomPacketPayload.Type<>(InventoryExchange.rl("infinite_container_scroll"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfiniteContainerScrollPKT> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT, InfiniteContainerScrollPKT::windowId,
            ByteBufCodecs.VAR_INT, InfiniteContainerScrollPKT::rowOffset,
            InfiniteContainerScrollPKT::new
        );

    @Override
    public CustomPacketPayload.Type<InfiniteContainerScrollPKT> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        var player = context.player();
        if (player == null || player.level().isClientSide) return;
        if (player.containerMenu instanceof InfiniteContainerMenu menu && player.containerMenu.containerId == windowId) {
            menu.setRowOffset(rowOffset);
            menu.broadcastChanges();
        }
    }
}