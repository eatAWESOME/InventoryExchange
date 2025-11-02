package eatawesome.inventoryexchange.gameObjs.registries;

import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainerMenu;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.gameObjs.container.TransmutationContainerMenu;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.impl.ContainerTypeDeferredRegister;
import eatawesome.inventoryexchange.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;

public class InventoryExchangeContainerTypes {
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES =
        new ContainerTypeDeferredRegister(InventoryExchange.MODID);

    public static final ContainerTypeRegistryObject<InfiniteContainerMenu> INFINITE_CONTAINER =
        CONTAINER_TYPES.register("infinite_container", (IContainerFactory<InfiniteContainerMenu>) (id, inv, buf) -> new InfiniteContainerMenu(id, inv));


	public static final ContainerTypeRegistryObject<PortableFurnaceMenu> PORTABLE_FURNACE = 
		CONTAINER_TYPES.register("portable_furnace", (IContainerFactory<PortableFurnaceMenu>) (id, inv, buf) -> new PortableFurnaceMenu(id, inv));
    

    public static final ContainerTypeRegistryObject<TransmutationContainerMenu> TRANSMUTATION_CONTAINER =
        CONTAINER_TYPES.register("transmutation_container", (IContainerFactory<TransmutationContainerMenu>) TransmutationContainerMenu::fromNetwork);

	
    public static void register(IEventBus bus) {
        CONTAINER_TYPES.register(bus);
    }
}