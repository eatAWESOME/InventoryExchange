package eatawesome.inventoryexchange.network.packets.to_client.knowledge;

import io.netty.buffer.ByteBuf;
import java.math.BigInteger;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.capabilities.IKnowledgeProvider;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.network.InventoryExchangeStreamCodecs;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;

public record KnowledgeSyncEmcPKT(BigInteger emc) implements IInventoryExchangePacket {

	public static final CustomPacketPayload.Type<KnowledgeSyncEmcPKT> TYPE = new CustomPacketPayload.Type<>(InventoryExchange.rl("knowledge_sync_emc"));
	public static final StreamCodec<ByteBuf, KnowledgeSyncEmcPKT> STREAM_CODEC = InventoryExchangeStreamCodecs.EMC_VALUE.map(
			KnowledgeSyncEmcPKT::new, KnowledgeSyncEmcPKT::emc
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<KnowledgeSyncEmcPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		Player player = context.player();
		IKnowledgeProvider knowledge = player.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge != null) {
			knowledge.setEmc(emc);
			if (player.containerMenu instanceof TransmutationContainerMenu container) {
				container.transmutationInventory.updateClientTargets(true);
			}
		}
		InventoryExchange.debugLog("** RECEIVED TRANSMUTATION EMC DATA CLIENTSIDE **");
	}
}