package eatawesome.inventoryexchange.network.packets.to_client.knowledge;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.ItemInfo;
import eatawesome.inventoryexchange.api.capabilities.IKnowledgeProvider;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;

public record KnowledgeSyncChangePKT(ItemInfo change, boolean learned) implements IInventoryExchangePacket {

	public static final CustomPacketPayload.Type<KnowledgeSyncChangePKT> TYPE = new CustomPacketPayload.Type<>(InventoryExchange.rl("knowledge_sync_change"));
	public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeSyncChangePKT> STREAM_CODEC = StreamCodec.composite(
			ItemInfo.STREAM_CODEC, KnowledgeSyncChangePKT::change,
			ByteBufCodecs.BOOL, KnowledgeSyncChangePKT::learned,
			KnowledgeSyncChangePKT::new
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<KnowledgeSyncChangePKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		Player player = context.player();
		IKnowledgeProvider knowledge = player.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge != null) {
			if (learned) {
				if (!knowledge.hasKnowledge(change) && knowledge.addKnowledge(change) && player.containerMenu instanceof TransmutationContainerMenu container) {
					container.transmutationInventory.itemLearned(change);
				}
			}
		}
		InventoryExchange.debugLog("** RECEIVED TRANSMUTATION KNOWLEDGE CHANGE DATA CLIENTSIDE **");
	}
}