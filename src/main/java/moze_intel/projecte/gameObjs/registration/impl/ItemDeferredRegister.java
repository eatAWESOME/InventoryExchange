package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.NotNull;

public class ItemDeferredRegister extends PEDeferredRegister<Item> {

	public ItemDeferredRegister(String modid) {
		super(Registries.ITEM, modid, ItemRegistryObject::new);
	}

	@Override
	public void register(@NotNull IEventBus bus) {
		super.register(bus);
	}

	public ItemRegistryObject<Item> register(String name) {
		return registerSimple(name, Item::new);
	}

	public ItemRegistryObject<Item> registerFireImmune(String name) {
		return registerFireImmune(name, Item::new);
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerSimple(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, UnaryOperator.identity());
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerFireImmune(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, Item.Properties::fireResistant);
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerNoStack(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, properties -> properties.stacksTo(1));
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerNoStackFireImmune(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, sup, properties -> properties.stacksTo(1).fireResistant());
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> registerTool(String name, Function<Item.Properties, ITEM> sup) {
		return register(name, () -> sup.apply(new NoDurabilityItemProperties().stacksTo(1).fireResistant()));
	}

	public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup, UnaryOperator<Item.Properties> propertyModifier) {
		return register(name, () -> sup.apply(propertyModifier.apply(new Item.Properties())));
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public <ITEM extends Item> ItemRegistryObject<ITEM> register(@NotNull String name, @NotNull Supplier<? extends ITEM> sup) {
		return (ItemRegistryObject<ITEM>) super.register(name, sup);
	}

	private static class NoDurabilityItemProperties extends Item.Properties {

		@NotNull
		@Override
		public Item.Properties durability(int maxDamage) {
			//NO-OP super setting durability components
			return this;
		}
	}
}