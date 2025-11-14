package eatawesome.inventoryexchange.network;

import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import eatawesome.inventoryexchange.network.packets.to_client.AbilityEMCSyncPKT;
import eatawesome.inventoryexchange.network.packets.to_client.InfiniteContainerSyncPKT;
import eatawesome.inventoryexchange.network.packets.to_client.KnowledgeToastPKT;
import eatawesome.inventoryexchange.network.packets.to_client.PortableFurnaceSyncPKT;
import eatawesome.inventoryexchange.network.packets.to_server.ClientConfigSyncPKT;
import eatawesome.inventoryexchange.network.packets.to_server.InfiniteContainerScrollPKT;
import eatawesome.inventoryexchange.network.packets.to_server.InfiniteContainerSortPKT;
import eatawesome.inventoryexchange.network.packets.to_server.KeyPressPKT;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 * Heavily based off of Mekanism's packet handler
 */
public final class PacketHandler {

	public PacketHandler(IEventBus modEventBus, ArtifactVersion version) {
		modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
			PayloadRegistrar registrar = event.registrar(version.toString());
			registerClientToServer(new PacketRegistrar(registrar, true));
			registerServerToClient(new PacketRegistrar(registrar, false));
		});
	}

	private void registerClientToServer(PacketRegistrar registrar) {
		registrar.play(ClientConfigSyncPKT.TYPE, ClientConfigSyncPKT.STREAM_CODEC);
		registrar.play(InfiniteContainerScrollPKT.TYPE, InfiniteContainerScrollPKT.STREAM_CODEC);
		registrar.play(InfiniteContainerSortPKT.TYPE, InfiniteContainerSortPKT.STREAM_CODEC);
		registrar.play(KeyPressPKT.TYPE, KeyPressPKT.STREAM_CODEC);
	}
	
	private void registerServerToClient(PacketRegistrar registrar) {
		registrar.play(AbilityEMCSyncPKT.TYPE, AbilityEMCSyncPKT.STREAM_CODEC);
		registrar.play(InfiniteContainerSyncPKT.TYPE, InfiniteContainerSyncPKT.STREAM_CODEC);
		registrar.play(PortableFurnaceSyncPKT.TYPE, PortableFurnaceSyncPKT.STREAM_CODEC);
		registrar.play(KnowledgeToastPKT.TYPE, KnowledgeToastPKT.STREAM_CODEC);
	}


	protected record SimplePacketPayLoad(CustomPacketPayload.Type<CustomPacketPayload> type) implements CustomPacketPayload {

		private SimplePacketPayLoad(ResourceLocation id) {
			this(new CustomPacketPayload.Type<>(id));
		}
	}

	protected record PacketRegistrar(PayloadRegistrar registrar, boolean toServer) {

		public <MSG extends IInventoryExchangePacket> void play(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> reader) {
			if (toServer) {
				registrar.playToServer(type, reader, IInventoryExchangePacket::handle);
			} else {
				registrar.playToClient(type, reader, IInventoryExchangePacket::handle);
			}
		}

		public SimplePacketPayLoad playInstanced(ResourceLocation id, IPayloadHandler<CustomPacketPayload> handler) {
			SimplePacketPayLoad payload = new SimplePacketPayLoad(id);
			if (toServer) {
				registrar.playToServer(payload.type(), StreamCodec.unit(payload), handler);
			} else {
				registrar.playToClient(payload.type(), StreamCodec.unit(payload), handler);
			}
			return payload;
		}
	}
}