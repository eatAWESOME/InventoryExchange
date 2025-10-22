package eatawesome.inventoryexchange.gameObjs.registration.impl;

import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredHolder;
import eatawesome.inventoryexchange.utils.text.IHasTranslationKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class ItemRegistryObject<ITEM extends Item> extends InventoryExchangeDeferredHolder<Item, ITEM> implements ItemLike, IHasTranslationKey {

	public ItemRegistryObject(ResourceKey<Item> key) {
		super(key);
	}

	@NotNull
	@Override
	public ITEM asItem() {
		return get();
	}

	@Override
	public String getTranslationKey() {
		return get().getDescriptionId();
	}

	public ItemStack asStack() {
		return asStack(1);
	}

	public ItemStack asStack(int count) {
		return new ItemStack(asItem(), count);
	}
}