package moze_intel.projecte.network;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.network.packets.IPEPacket;
import moze_intel.projecte.network.packets.to_client.SyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.container.SyncOffhandPkt;
import moze_intel.projecte.network.packets.to_client.container.UpdateWindowLongPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncChangePKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncInputsAndLocksPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncPKT;
import moze_intel.projecte.network.packets.to_server.SearchUpdatePKT;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;

/**
 * Heavily based off of Mekanism's packet handler
 */
public final class PacketHandler {

	//Client to server instanced packets

	//Server to client instanced packets
	private SimplePacketPayLoad clearKnowledge;
	private SimplePacketPayLoad updateTransmutationTargets;

	public PacketHandler(IEventBus modEventBus, ArtifactVersion version) {
		modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
			PayloadRegistrar registrar = event.registrar(version.toString());
			registerClientToServer(new PacketRegistrar(registrar, true));
			registerServerToClient(new PacketRegistrar(registrar, false));
		});
	}

	private void registerClientToServer(PacketRegistrar registrar) {
		registrar.play(SearchUpdatePKT.TYPE, SearchUpdatePKT.STREAM_CODEC);
	}

	private void registerServerToClient(PacketRegistrar registrar) {
		clearKnowledge = registrar.playInstanced(PECore.rl("clear_knowledge"), (ignored, context) -> {
			Player player = context.player();
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge != null) {
				knowledge.clearKnowledge();
				if (player.containerMenu instanceof TransmutationContainerMenu container) {
					container.transmutationInventory.updateClientTargets(false);
				}
			}
		});
		registrar.play(KnowledgeSyncPKT.TYPE, KnowledgeSyncPKT.STREAM_CODEC);
		registrar.play(KnowledgeSyncEmcPKT.TYPE, KnowledgeSyncEmcPKT.STREAM_CODEC);
		registrar.play(KnowledgeSyncInputsAndLocksPKT.TYPE, KnowledgeSyncInputsAndLocksPKT.STREAM_CODEC);
		registrar.play(KnowledgeSyncChangePKT.TYPE, KnowledgeSyncChangePKT.STREAM_CODEC);
		registrar.play(SyncEmcPKT.TYPE, SyncEmcPKT.STREAM_CODEC);
		registrar.play(SyncOffhandPkt.TYPE, SyncOffhandPkt.STREAM_CODEC);
		updateTransmutationTargets = registrar.playInstanced(PECore.rl("update_transmutation_targets"), (ignored, context) -> {
			if (context.player().containerMenu instanceof TransmutationContainerMenu container) {
				container.transmutationInventory.updateClientTargets(false);
			}
		});
		registrar.play(UpdateWindowLongPKT.TYPE, UpdateWindowLongPKT.STREAM_CODEC);
	}

	public void clearKnowledge(ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, clearKnowledge);
	}

	public void updateTransmutationTargets(ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, updateTransmutationTargets);
	}

	protected record SimplePacketPayLoad(CustomPacketPayload.Type<CustomPacketPayload> type) implements CustomPacketPayload {

		private SimplePacketPayLoad(ResourceLocation id) {
			this(new CustomPacketPayload.Type<>(id));
		}
	}

	protected record PacketRegistrar(PayloadRegistrar registrar, boolean toServer) {

		public <MSG extends IPEPacket> void play(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> reader) {
			if (toServer) {
				registrar.playToServer(type, reader, IPEPacket::handle);
			} else {
				registrar.playToClient(type, reader, IPEPacket::handle);
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