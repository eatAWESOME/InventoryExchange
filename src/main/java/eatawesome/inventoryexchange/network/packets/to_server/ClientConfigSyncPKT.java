package eatawesome.inventoryexchange.network.packets.to_server;

import io.netty.buffer.ByteBuf;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientConfigSyncPKT(boolean condensingLimited, boolean feedingLimited, boolean repairingLimited, boolean smeltingLimited) implements IInventoryExchangePacket {

    public static final CustomPacketPayload.Type<ClientConfigSyncPKT> TYPE =
        new CustomPacketPayload.Type<>(InventoryExchange.rl("client_prefs_sync"));

    public static final StreamCodec<ByteBuf, ClientConfigSyncPKT> STREAM_CODEC =
        StreamCodec.of((buf, pkt) -> {
            buf.writeBoolean(pkt.condensingLimited);
            buf.writeBoolean(pkt.feedingLimited);
            buf.writeBoolean(pkt.repairingLimited);
            buf.writeBoolean(pkt.smeltingLimited);
        }, buf -> new ClientConfigSyncPKT(
            buf.readBoolean(),
            buf.readBoolean(),
            buf.readBoolean(),
            buf.readBoolean()
        ));

    @Override
    public CustomPacketPayload.Type<ClientConfigSyncPKT> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
        	serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).condensingLimited = condensingLimited;
            serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).feedingLimited = feedingLimited;
            serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).repairingLimited = repairingLimited;
            serverPlayer.getData(InventoryExchangeAttachmentTypes.PLAYER_SETTINGS).smeltingLimited = smeltingLimited;
        }
    }
}