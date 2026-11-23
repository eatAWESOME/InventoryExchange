package eatawesome.inventoryexchange.network.packets.to_client;

import io.netty.buffer.ByteBuf;
import java.math.BigInteger;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.network.InventoryExchangeStreamCodecs;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;

public record AbilityEMCSyncPKT(BigInteger emc) implements IInventoryExchangePacket {

	public static final CustomPacketPayload.Type<AbilityEMCSyncPKT> TYPE = new CustomPacketPayload.Type<>(InventoryExchange.rl("ability_emc_sync"));
	public static final StreamCodec<ByteBuf, AbilityEMCSyncPKT> STREAM_CODEC = InventoryExchangeStreamCodecs.EMC_VALUE.map(
			AbilityEMCSyncPKT::new, AbilityEMCSyncPKT::emc
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<AbilityEMCSyncPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		Player player = context.player();
		IAbilityEMCProvider knowledge = player.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
		if (knowledge != null) {
			knowledge.setEmc(emc);
			if (player.containerMenu instanceof TransmutationContainerMenu container) {
				container.transmutationInventory.updateClientTargets(true);
			}
		}
		InventoryExchange.debugLog("** RECEIVED ABILITY EMC DATA CLIENTSIDE **");
	}
}