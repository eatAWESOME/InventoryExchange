package eatawesome.inventoryexchange.utils;

import java.math.BigInteger;
import java.util.Optional;

import eatawesome.inventoryexchange.api.capabilities.IKnowledgeProvider;
import eatawesome.inventoryexchange.api.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public final class PortableFurnaceHelper {
	private PortableFurnaceHelper() {}
	
	public static IItemHandlerModifiable getInventory(Player player) {
		return new PortableFurnaceImpl(player).getInventory();
	}
	
	public static void sync(ServerPlayer serverPlayer) {
		new PortableFurnaceImpl(serverPlayer).sync(serverPlayer);
	}
	
	// Portable Furnace Smelting
	public static boolean smeltOneIfPossible(ServerPlayer serverPlayer, IItemHandlerModifiable handler) {
		ItemStack in = handler.getStackInSlot(0);
        if (in.isEmpty()) return false;
        ItemStack tmpl = recipeFor(serverPlayer, in);
        if (tmpl.isEmpty()) return false;
        if (!canAfford(serverPlayer)) return false;

        ItemStack out = handler.getStackInSlot(2);
        if (out.isEmpty()) {
            ItemStack one = tmpl.copy();
            one.setCount(1);
            handler.setStackInSlot(2, one);
        } else {
            if (!ItemStack.isSameItemSameComponents(out, tmpl)) return false;
            if (out.getCount() >= out.getMaxStackSize()) return false;
            out.grow(1);
            handler.setStackInSlot(2, out);
        }

        in.shrink(1);
        if (in.isEmpty()) in = ItemStack.EMPTY;
        handler.setStackInSlot(0, in);
        withdrawEmc(serverPlayer, 1);
        sync(serverPlayer);
		return true;
    }
	
	// Inventory Smelting
	public static boolean smeltOneIfPossible(ServerPlayer serverPlayer) {
		if (!canAfford(serverPlayer)) return true;
		Level level = serverPlayer.level();
		// Inventory
		Inventory inv = serverPlayer.getInventory();
		for (int i = 0; i < inv.items.size(); i++) {
			ItemStack itemStack = inv.items.get(i);
			if (itemStack.isEmpty()) continue;
			if (IEMCProxy.INSTANCE.getValue(itemStack) > 0) continue;
			ItemStack one = itemStack.copyWithCount(1);
			Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(one), level);
			if (recipe.isEmpty()) continue;
			ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(one), level.registryAccess());
			if (result.isEmpty()) continue;
			if (IEMCProxy.INSTANCE.getValue(result) <= 0) continue;
			ItemStack remainderStack = PlayerHelper.insertIntoInventoryThenHandler(serverPlayer.getInventory(), InfiniteContainerHelper.getInventory(serverPlayer), result.copy());
			if (remainderStack.isEmpty()) {
				PortableFurnaceHelper.withdrawEmc(serverPlayer);
				itemStack.shrink(1);
				if (itemStack.isEmpty()) inv.items.set(i, ItemStack.EMPTY);
				NeoForge.EVENT_BUS.post(new PlayerEvent.ItemSmeltedEvent(serverPlayer, result.copy()));
				float exp = getSmeltingExperienceFromInput(level, one);
				if (exp > 0) {
					PlayerHelper.awardExp(serverPlayer, exp);
				}
				serverPlayer.awardStat(Stats.ITEM_CRAFTED.get(result.getItem()), result.getCount());
			}
			return true;
		}
		// Infinite Inventory
		IItemHandlerModifiable handler = InfiniteContainerHelper.getInventory(serverPlayer);
		int slots = handler.getSlots();
		for (int i = 0; i < slots; i++) {
            ItemStack itemStack = handler.getStackInSlot(i);
            if (itemStack.isEmpty()) continue;
			if (IEMCProxy.INSTANCE.getValue(itemStack) > 0) continue;
			ItemStack one = itemStack.copyWithCount(1);
			Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(one), level);
			if (recipe.isEmpty()) continue;
			ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(one), level.registryAccess());
			if (result.isEmpty()) continue;
			if (IEMCProxy.INSTANCE.getValue(result) <= 0) continue;
			ItemStack remainderStack = PlayerHelper.insertIntoInventoryThenHandler(serverPlayer.getInventory(), InfiniteContainerHelper.getInventory(serverPlayer), result.copy());
			if (remainderStack.isEmpty()) {
				PortableFurnaceHelper.withdrawEmc(serverPlayer);
				itemStack.shrink(1);
				if (itemStack.isEmpty()) handler.setStackInSlot(i, ItemStack.EMPTY);
				NeoForge.EVENT_BUS.post(new PlayerEvent.ItemSmeltedEvent(serverPlayer, result.copy()));
				float exp = getSmeltingExperienceFromInput(level, one);
				if (exp > 0) {
					PlayerHelper.awardExp(serverPlayer, exp);
				}
				serverPlayer.awardStat(Stats.ITEM_CRAFTED.get(result.getItem()), result.getCount());
			}
			return true;
        }
		return false;
    }

    private static ItemStack recipeFor(Player player, ItemStack in) {
        Level level = player.level();
        SingleRecipeInput input = new SingleRecipeInput(in);
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getServer().getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isPresent()) {
            ItemStack out = recipe.get().value().assemble(input, level.registryAccess());
            out.setCount(1);
            return out;
        }
        return ItemStack.EMPTY;
    }
    
    public static float getSmeltingExperienceFromInput(Level level, ItemStack inputStack) {
		ItemStack one = inputStack.copyWithCount(1);
		Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(one), level);
		if (!recipe.isEmpty()) {
			if (recipe.get().value() instanceof AbstractCookingRecipe cooking) {
				return cooking.getExperience() * inputStack.getCount();
			}
		}
		return 0F;
	}
    
    public static float getSmeltingExperienceFromOutput(Level level, ItemStack outputStack) {
	    if (!outputStack.isEmpty()) {
	    	ItemStack one = outputStack.copyWithCount(1);
		    for (RecipeHolder<? extends Recipe<?>> recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
		        if (recipe.value() instanceof AbstractCookingRecipe cooking) {
		            ItemStack out = cooking.getResultItem(level.registryAccess());
		            if (!out.isEmpty() && ItemStack.isSameItemSameComponents(out, one)) {
		                return cooking.getExperience() * outputStack.getCount();
		            }
		        }
		    }
	    }
	    return 0F;
	}

    public static boolean canAfford(Player player) {
        BigInteger have = getEmc(player);
        return have.compareTo(BigInteger.valueOf(PortableFurnaceMenu.EMC_PER)) >= 0;
    }

    public static boolean canAfford(Player player, int count) {
        BigInteger have = getEmc(player);
        return have.compareTo(BigInteger.valueOf((long) count * PortableFurnaceMenu.EMC_PER)) >= 0;
    }

    public static void withdrawEmc(ServerPlayer serverPlayer) {
        BigInteger cur = getEmc(serverPlayer);
        BigInteger cost = BigInteger.valueOf(PortableFurnaceMenu.EMC_PER);
        if (cur.compareTo(cost) >= 0) setEmc(serverPlayer, cur.subtract(cost));
    }

    public static void withdrawEmc(ServerPlayer serverPlayer, int count) {
        BigInteger cur = getEmc(serverPlayer);
        BigInteger cost = BigInteger.valueOf((long) count * PortableFurnaceMenu.EMC_PER);
        if (cur.compareTo(cost) >= 0) setEmc(serverPlayer, cur.subtract(cost));
    }

    private static BigInteger getEmc(Player player) {
        IKnowledgeProvider knowledge = player.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null) return BigInteger.ZERO;
        try { return knowledge.getEmc(); } catch (Throwable t) { return BigInteger.ZERO; }
    }

    private static void setEmc(ServerPlayer serverPlayer, BigInteger emc) {
        IKnowledgeProvider knowledge = serverPlayer.getCapability(InventoryExchangeCapabilities.KNOWLEDGE_CAPABILITY);
        if (knowledge == null) return;
        knowledge.setEmc(emc);
        knowledge.syncEmc(serverPlayer);
    }
}