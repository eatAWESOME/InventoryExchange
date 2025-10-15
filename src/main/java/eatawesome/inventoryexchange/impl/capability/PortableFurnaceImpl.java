package eatawesome.inventoryexchange.impl.capability;

import com.mojang.serialization.Codec;
import eatawesome.inventoryexchange.capabilities.IPortableFurnaceProvider;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import eatawesome.inventoryexchange.impl.codec.InventoryExchangeCodecHelper;
import eatawesome.inventoryexchange.network.packets.to_client.PortableFurnaceSyncPKT;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PortableFurnaceImpl implements IPortableFurnaceProvider {

    public static IPortableFurnaceProvider wrapAttachment(PortableFurnaceAttachment attachment) {
        return new PortableFurnaceImpl(null) {
            @Override protected PortableFurnaceAttachment attachment() { return attachment; }
        };
    }

    private final Player player;

    public PortableFurnaceImpl(@Nullable Player player) {
        this.player = player;
    }

    protected PortableFurnaceAttachment attachment() {
        Objects.requireNonNull(this.player, "Player must not be null for attachment()");
        return this.player.getData(InventoryExchangeAttachmentTypes.PORTABLE_FURNACE);
    }

    private static int currentSize() { return 3; }

    public static final Codec<PortableFurnaceAttachment> CODEC =
        InventoryExchangeCodecHelper.MUTABLE_HANDLER_CODEC.xmap(
            PortableFurnaceAttachment::new,
            att -> att.inventory
        );

    public static StreamCodec<RegistryFriendlyByteBuf, ItemStackHandler> handlerStreamCodec() {
        return eatawesome.inventoryexchange.network.InventoryExchangeStreamCodecs.handlerStreamCodec(currentSize());
    }

    public static StreamCodec<RegistryFriendlyByteBuf, PortableFurnaceAttachment> streamCodec() {
        return handlerStreamCodec().map(PortableFurnaceAttachment::new, att -> att.inventory);
    }

    @NotNull
    @Override
    public IItemHandlerModifiable getInventory() {
        return attachment().inventory;
    }

    @Override
    public void sync(@NotNull ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new PortableFurnaceSyncPKT(attachment()));
    }

    public static class PortableFurnaceAttachment {
        public final PortableFurnaceHandler inventory;

        public PortableFurnaceAttachment() {
            this.inventory = new PortableFurnaceHandler(currentSize());
        }

        public PortableFurnaceAttachment(ItemStackHandler handler) {
            int expected = currentSize();
            if (handler instanceof PortableFurnaceHandler h && h.getSlots() == expected) {
                this.inventory = h;
            } else {
                PortableFurnaceHandler converted = new PortableFurnaceHandler(expected);
                int copy = Math.min(handler == null ? 0 : handler.getSlots(), expected);
                for (int i = 0; i < copy; i++) {
                    if (i == 1) continue;
                    converted.setStackInSlot(i, handler.getStackInSlot(i).copy());
                }
                this.inventory = converted;
            }
        }

        @Nullable
        public PortableFurnaceAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
            ItemStackHandler copyHandler =
                InventoryExchangeAttachmentTypes.copyHandler(this.inventory, PortableFurnaceHandler::new);
            return new PortableFurnaceAttachment(copyHandler);
        }
    }

    public static class PortableFurnaceHandler extends ItemStackHandler implements IItemHandlerModifiable {
        public PortableFurnaceHandler(int slots) { super(slots); }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot == 1) return new ItemStack(Items.COAL);
            return super.getStackInSlot(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) { return slot == 0; }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            if (slot == 1) return;
            super.setStackInSlot(slot, stack);
        }

        @Override
        public @NotNull net.minecraft.nbt.CompoundTag serializeNBT(HolderLookup.Provider registries) {
            net.minecraft.nbt.CompoundTag tag = super.serializeNBT(registries);
            tag.remove("1");
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registries, @NotNull net.minecraft.nbt.CompoundTag nbt) {
            super.deserializeNBT(registries, nbt);
        }
    }
}