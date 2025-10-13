package eatawesome.inventoryexchange.gameObjs.registration.impl;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import java.util.function.Supplier;

import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredRegister;

public class ContainerTypeDeferredRegister extends InventoryExchangeDeferredRegister<MenuType<?>> {
    public ContainerTypeDeferredRegister(String modid) {
        super(Registries.MENU, modid, (ResourceKey<MenuType<?>> key) -> new ContainerTypeRegistryObject<>(key));
    }

    public <T extends AbstractContainerMenu> ContainerTypeRegistryObject<T> register(String name, IContainerFactory<T> factory) {
        return registerMenu(name, () -> new MenuType<>(factory, net.minecraft.world.flag.FeatureFlags.VANILLA_SET));
    }

    public <T extends AbstractContainerMenu> ContainerTypeRegistryObject<T> registerMenu(String name, Supplier<MenuType<T>> supplier) {
        return (ContainerTypeRegistryObject<T>) super.register(name, supplier);
    }
}