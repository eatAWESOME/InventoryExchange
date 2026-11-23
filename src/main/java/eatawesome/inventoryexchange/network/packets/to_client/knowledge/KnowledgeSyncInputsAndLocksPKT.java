package eatawesome.inventoryexchange.network.packets.to_client.knowledge;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.capabilities.IKnowledgeProvider;
import eatawesome.inventoryexchange.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.gameObjs.container.inventory.TransmutationInventory;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;

public record KnowledgeSyncInputsAndLocksPKT(Int2ObjectMap<ItemStack> stacksToSync, TargetUpdateType updateTargets) implements IInventoryExchangePacket {

	public static final CustomPacketPayload.Type<KnowledgeSyncInputsAndLocksPKT> TYPE = new CustomPacketPayload.Type<>(InventoryExchange.rl("knowledge_sync_inputs_and_locks"));
	public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeSyncInputsAndLocksPKT> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(Int2ObjectOpenHashMap::new, ByteBufCodecs.VAR_INT, ItemStack.OPTIONAL_STREAM_CODEC), KnowledgeSyncInputsAndLocksPKT::stacksToSync,
			TargetUpdateType.STREAM_CODEC, KnowledgeSyncInputsAndLocksPKT::updateTargets,
			KnowledgeSyncInputsAndLocksPKT::new
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<KnowledgeSyncInputsAndLocksPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		Player player = context.player();
		IKnowledgeProvider knowledge = player.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge != null) {
			knowledge.receiveInputsAndLocks(stacksToSync);
			if (updateTargets != TargetUpdateType.NONE && player.containerMenu instanceof TransmutationContainerMenu container) {
				//Update targets in case total available EMC is now different
				TransmutationInventory transmutationInventory = container.transmutationInventory;
				if (updateTargets == TargetUpdateType.ALL) {
					//TODO: Re-evaluate when the update type is all, and see if we can optimize this to not have to process?
					// Also figure out the need for the difference between this and the check for updates
					transmutationInventory.updateClientTargets(false);
				} else {//If needed
					transmutationInventory.checkForUpdates();
				}
			}
		}
		InventoryExchange.debugLog("** RECEIVED TRANSMUTATION INPUT AND LOCK DATA CLIENTSIDE **");
	}
}