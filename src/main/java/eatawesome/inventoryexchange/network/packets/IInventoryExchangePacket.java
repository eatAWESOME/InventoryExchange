package eatawesome.inventoryexchange.network.packets;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IInventoryExchangePacket extends CustomPacketPayload {

	void handle(IPayloadContext context);
}