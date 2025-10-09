package eatawesome.inventoryexchange.impl.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import moze_intel.projecte.api.ItemInfo;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.event.PlayerAbilityEMCChangeEvent;
import eatawesome.inventoryexchange.codec.IInventoryExchangeCodecHelper;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.EMCMappingHandler;
import eatawesome.inventoryexchange.impl.codec.InventoryExchangeCodecHelper;
import eatawesome.inventoryexchange.network.InventoryExchangeStreamCodecs;
import eatawesome.inventoryexchange.network.packets.to_client.abilityemc.AbilityEMCSyncPKT;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbilityEMCImpl implements IAbilityEMCProvider {

	public static IAbilityEMCProvider wrapAttachment(AbilityEMCAttachment attachment) {
		return new AbilityEMCImpl(null) {
			@Override
			protected AbilityEMCAttachment attachment() {
				return attachment;
			}
		};
	}

	private final Player player;

	public AbilityEMCImpl(Player player) {
		this.player = player;
	}

	protected AbilityEMCAttachment attachment() {
		//Force overriding if player is null
		Objects.requireNonNull(this.player);
		return this.player.getData(InventoryExchangeAttachmentTypes.ABILITY_EMC);
	}

	protected void fireChangedEvent() {
		if (player != null && !player.level().isClientSide) {
			NeoForge.EVENT_BUS.post(new PlayerAbilityEMCChangeEvent(player));
		}
	}
	@Override
	public BigInteger getEmc() {
		return attachment().emc;
	}

	@Override
	public void setEmc(BigInteger emc) {
		attachment().emc = emc;
	}

	@Override
	public void sync(@NotNull ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new AbilityEMCSyncPKT(attachment().emc));
	}

	public static class AbilityEMCAttachment {

		private static final int LOCK_SLOTS = 9;

		private static final Codec<Set<ItemInfo>> MUTABLE_ABILITY_EMC_CODEC = ItemInfo.CODEC.listOf()
			.promotePartial(error -> InventoryExchange.LOGGER.error("Failed to load stored AbilityEMC: {}", error))
			.xmap(HashSet::new, List::copyOf);
		public static final Codec<AbilityEMCAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				IInventoryExchangeCodecHelper.INSTANCE.nonNegativeBigInt().optionalFieldOf("emc", BigInteger.ZERO).forGetter(attachment -> attachment.emc)
		).apply(instance, AbilityEMCAttachment::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, AbilityEMCAttachment> STREAM_CODEC = StreamCodec.composite(
				InventoryExchangeStreamCodecs.EMC_VALUE, attachment -> attachment.emc,
				AbilityEMCAttachment::new
		);

		private BigInteger emc;

		public AbilityEMCAttachment() {
			this(BigInteger.ZERO);
		}

		private AbilityEMCAttachment(BigInteger emc) {
			this.emc = emc;
		}

		@Nullable
		public AbilityEMCAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
			//Note: ItemInfo and BigInteger are both immutable, so we can just add them directly
			return new AbilityEMCAttachment(emc);
		}
	}
}