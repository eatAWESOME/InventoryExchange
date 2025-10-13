package eatawesome.inventoryexchange.gameObjs.registration.impl;

import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ContainerTypeRegistryObject<T extends AbstractContainerMenu> extends InventoryExchangeDeferredHolder<MenuType<?>, MenuType<T>> {
    public ContainerTypeRegistryObject(ResourceKey<MenuType<?>> key) {
        super(key);
    }
}