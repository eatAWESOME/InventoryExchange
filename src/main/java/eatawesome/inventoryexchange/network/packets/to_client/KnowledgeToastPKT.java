package eatawesome.inventoryexchange.network.packets.to_client;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.network.packets.IInventoryExchangePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record KnowledgeToastPKT(ItemStack stack) implements IInventoryExchangePacket {

	public static final CustomPacketPayload.Type<KnowledgeToastPKT> TYPE =
		new CustomPacketPayload.Type<>(InventoryExchange.rl("knowledge_toast"));

	public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeToastPKT> STREAM_CODEC =
		ItemStack.STREAM_CODEC.map(KnowledgeToastPKT::new, KnowledgeToastPKT::stack);

	@Override
	public @NotNull CustomPacketPayload.Type<KnowledgeToastPKT> type() { return TYPE; }

	@Override
	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (isClientPresent()) {
				try {
					Class<?> hooks = Class.forName("eatawesome.inventoryexchange.client.ClientHooks");
					hooks.getMethod("showKnowledgeToast", ItemStack.class)
						.invoke(null, stack.copyWithCount(1));
				} catch (Throwable ignored) {
				}
			}
		});
	}

	private static boolean isClientPresent() {
		try {
			Class.forName("net.minecraft.client.Minecraft");
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}