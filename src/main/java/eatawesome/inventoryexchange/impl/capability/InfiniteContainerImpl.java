package eatawesome.inventoryexchange.impl.capability;

import com.mojang.serialization.Codec;
import eatawesome.inventoryexchange.capabilities.IInfiniteContainerProvider;
import eatawesome.inventoryexchange.config.InventoryExchangeCommonConfig;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.impl.codec.InventoryExchangeCodecHelper;
import eatawesome.inventoryexchange.network.packets.to_client.InfiniteContainerSyncPKT;
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

    private static int currentSize() {
        return InventoryExchangeCommonConfig.getMaxInfiniteContainerRows() * 13;
    }

    public static final Codec<InfiniteContainerAttachment> CODEC =
        InventoryExchangeCodecHelper.MUTABLE_HANDLER_CODEC.xmap(
            InfiniteContainerAttachment::new,
            att -> att.inventory
        );

    public static StreamCodec<RegistryFriendlyByteBuf, ItemStackHandler> handlerStreamCodec() {
        return eatawesome.inventoryexchange.network.InventoryExchangeStreamCodecs.handlerStreamCodec(currentSize());
    }

    public static StreamCodec<RegistryFriendlyByteBuf, InfiniteContainerAttachment> streamCodec() {
        return handlerStreamCodec().map(InfiniteContainerAttachment::new, att -> att.inventory);
    }

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
            this.inventory = new ItemStackHandler(currentSize());
        }

        public InfiniteContainerAttachment(ItemStackHandler handler) {
            int expected = currentSize();
            if (handler == null || handler.getSlots() == expected) {
                this.inventory = handler == null ? new ItemStackHandler(expected) : handler;
            } else {
                ItemStackHandler resized = new ItemStackHandler(expected);
                int copy = Math.min(handler.getSlots(), expected);
                for (int i = 0; i < copy; i++) {
                    resized.setStackInSlot(i, handler.getStackInSlot(i).copy());
                }
                this.inventory = resized;
            }
        }

        @Nullable
        public InfiniteContainerAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
            ItemStackHandler copyHandler =
                InventoryExchangeAttachmentTypes.copyHandler(this.inventory, ItemStackHandler::new);
            return new InfiniteContainerAttachment(copyHandler);
        }
    }
}