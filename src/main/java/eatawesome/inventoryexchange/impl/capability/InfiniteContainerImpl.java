package eatawesome.inventoryexchange.impl.capability;

import com.mojang.serialization.Codec;
import eatawesome.inventoryexchange.capabilities.IInfiniteContainerProvider;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.impl.codec.InventoryExchangeCodecHelper;
import eatawesome.inventoryexchange.network.packets.to_client.infinite_container.InfiniteContainerSyncPKT;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class InfiniteContainerImpl implements IInfiniteContainerProvider {

    public static IInfiniteContainerProvider wrapAttachment(InfiniteContainerAttachment attachment) {
        return new InfiniteContainerImpl(null) {
            @Override
            protected InfiniteContainerAttachment attachment() {
                return attachment;
            }
        };
    }

    private final Player player;

    public InfiniteContainerImpl(@Nullable Player player) {
        this.player = player;
    }

    protected InfiniteContainerAttachment attachment() {
        Objects.requireNonNull(this.player, "Player must not be null for attachment()");
        return this.player.getData(InventoryExchangeAttachmentTypes.INFINITE_CONTAINER);
    }

    public static final int SIZE = 500 * 13;

    public static final Codec<InfiniteContainerAttachment> CODEC =
        InventoryExchangeCodecHelper.MUTABLE_HANDLER_CODEC.xmap(
            InfiniteContainerAttachment::new,
            attachment -> attachment.inventory
        );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackHandler> HANDLER_STREAM_CODEC =
        eatawesome.inventoryexchange.network.InventoryExchangeStreamCodecs.handlerStreamCodec(SIZE);
    public static final StreamCodec<RegistryFriendlyByteBuf, InfiniteContainerAttachment> STREAM_CODEC =
        HANDLER_STREAM_CODEC.map(InfiniteContainerAttachment::new, attachment -> attachment.inventory);

    @NotNull
    @Override
    public IItemHandlerModifiable getInventory() {
        return attachment().inventory;
    }

    @Override
    public void sync(@NotNull ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new InfiniteContainerSyncPKT(attachment()));
    }

    public static class InfiniteContainerAttachment {
        public final ItemStackHandler inventory;

        public InfiniteContainerAttachment() {
            this.inventory = new ItemStackHandler(SIZE);
        }

        public InfiniteContainerAttachment(ItemStackHandler handler) {
            if (handler == null || handler.getSlots() == SIZE) {
                this.inventory = handler == null ? new ItemStackHandler(SIZE) : handler;
            } else {
                ItemStackHandler resized = new ItemStackHandler(SIZE);
                int copy = Math.min(handler.getSlots(), SIZE);
                for (int i = 0; i < copy; i++) {
                    resized.setStackInSlot(i, handler.getStackInSlot(i).copy());
                }
                this.inventory = resized;
            }
        }

        @Nullable
        public InfiniteContainerAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
            ItemStackHandler copyHandler = InventoryExchangeAttachmentTypes.copyHandler(this.inventory, ItemStackHandler::new);
            return new InfiniteContainerAttachment(copyHandler);
        }
    }
}