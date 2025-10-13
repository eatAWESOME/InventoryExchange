package eatawesome.inventoryexchange.gameObjs.registries;

import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.impl.ContainerTypeDeferredRegister;
import eatawesome.inventoryexchange.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;

public class InventoryExchangeContainerTypes {
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES =
        new ContainerTypeDeferredRegister(InventoryExchange.MODID);

    public static final ContainerTypeRegistryObject<InfiniteContainer> INFINITE_CONTAINER =
        CONTAINER_TYPES.register("infinite_container", (IContainerFactory<InfiniteContainer>) (id, inv, buf) -> new InfiniteContainer(id, inv));

    public static void register(IEventBus bus) {
        CONTAINER_TYPES.register(bus);
    }
}