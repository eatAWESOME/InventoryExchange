package eatawesome.inventoryexchange.network.packets.to_client.knowledge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.impl.capability.KnowledgeImpl.KnowledgeAttachment;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;

public record KnowledgeSyncPKT(KnowledgeAttachment data) implements IInventoryExchangePacket {

	public static final CustomPacketPayload.Type<KnowledgeSyncPKT> TYPE = new CustomPacketPayload.Type<>(InventoryExchange.rl("sync_knowledge"));
	public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeSyncPKT> STREAM_CODEC = KnowledgeAttachment.STREAM_CODEC.map(
			KnowledgeSyncPKT::new, KnowledgeSyncPKT::data
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<KnowledgeSyncPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		//We have to use the client's player instance rather than context#player as the first usage of this packet is sent during player login
		// which is before the player exists on the client so the context does not contain it.
		//Note: This must stay LocalPlayer to not cause classloading issues
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.setData(InventoryExchangeAttachmentTypes.KNOWLEDGE, data);
			if (player.containerMenu instanceof TransmutationContainerMenu container) {
				container.transmutationInventory.updateClientTargets(false);
			}
		}
		InventoryExchange.debugLog("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");
	}
}