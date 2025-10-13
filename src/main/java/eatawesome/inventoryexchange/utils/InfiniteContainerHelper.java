package eatawesome.inventoryexchange.utils;

import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InfiniteContainerHelper {
    // On a real mod, use capabilities or persistent player data!
    private static final ConcurrentHashMap<UUID, SimpleContainer> playerInventories = new ConcurrentHashMap<>();

    public static SimpleContainer getInventory(Player player) {
        return playerInventories.computeIfAbsent(player.getUUID(), uuid -> new SimpleContainer(InfiniteContainer.SLOT_COUNT));
    }

    public static void openForPlayer(ServerPlayer player) {
        SimpleContainer inv = getInventory(player);
        player.openMenu(new SimpleMenuProvider((id, playerInv, serverPlayer) ->
            new InfiniteContainer(id, playerInv, inv),
            net.minecraft.network.chat.Component.translatable("container.inventoryexchange.infinite")
        ));
    }
}