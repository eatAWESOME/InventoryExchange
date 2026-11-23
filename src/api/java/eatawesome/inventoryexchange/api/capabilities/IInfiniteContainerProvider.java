package eatawesome.inventoryexchange.api.capabilities;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface IInfiniteContainerProvider {
    @NotNull
    IItemHandlerModifiable getInventory();

    void sync(@NotNull ServerPlayer player);
}