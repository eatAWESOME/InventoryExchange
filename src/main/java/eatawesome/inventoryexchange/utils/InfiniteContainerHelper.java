package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.capabilities.IInfiniteContainerProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;

public class InfiniteContainerHelper {

    public static IItemHandlerModifiable getInventory(Player player) {
        IInfiniteContainerProvider provider = player.getCapability(InventoryExchangeCapabilities.INFINITE_CONTAINER_CAPABILITY);
        if (provider != null) {
            return provider.getInventory();
        }
        return player.getData(InventoryExchangeAttachmentTypes.INFINITE_CONTAINER).inventory;
    }

    public static void openForPlayer(ServerPlayer player) {
        IItemHandlerModifiable inv = getInventory(player);
        player.openMenu(new SimpleMenuProvider((id, playerInv, serverPlayer) ->
            new InfiniteContainer(id, playerInv, inv),
            Component.translatable("container.inventoryexchange.infinite")
        ));
        IInfiniteContainerProvider provider = player.getCapability(InventoryExchangeCapabilities.INFINITE_CONTAINER_CAPABILITY);
        if (provider != null) {
            provider.sync(player);
        }
    }
}