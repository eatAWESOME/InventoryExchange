package eatawesome.inventoryexchange.utils;

import java.math.BigInteger;
import java.util.Optional;

import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.server.level.ServerPlayer;
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
	
	public static void sync(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			new PortableFurnaceImpl(player).sync(serverPlayer);
		}
	}
	
	// Portable Furnace Smelting
	public static boolean smeltOneIfPossible(Player player, IItemHandlerModifiable handler) {
		if (player instanceof ServerPlayer) {
			ItemStack in = handler.getStackInSlot(0);
	        if (in.isEmpty()) return false;
	        ItemStack tmpl = recipeFor(player, in);
	        if (tmpl.isEmpty()) return false;
	        if (!canAfford(player)) return false;

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
	        withdrawEmc(player, 1);
	        sync(player);
		}
		return true;
    }
	
	// Inventory Smelting
	public static void smeltOneIfPossible(Player player) {
		if (player instanceof ServerPlayer) {
			if (!canAfford(player)) return;
			Level level = player.level();
			Inventory inv = player.getInventory();
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
				ItemStack toInsert = result.copy();
				boolean fullyAdded = inv.add(toInsert);
				if (fullyAdded) {
					PortableFurnaceHelper.withdrawEmc(player);
					itemStack.shrink(1);
					if (itemStack.isEmpty()) inv.items.set(i, ItemStack.EMPTY);
					NeoForge.EVENT_BUS.post(new PlayerEvent.ItemSmeltedEvent(player, result.copy()));
					float exp = getSmeltingExperienceFromInput(level, one);
					if (exp > 0) {
						PlayerHelper.awardExp(player, exp);
					}
				}
				break;
			}
		}
    }

    private static ItemStack recipeFor(Player player, ItemStack in) {
        Level level = player.level();
        SingleRecipeInput input = new SingleRecipeInput(in);
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getServer().getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isPresent()) {
            RecipeHolder<? extends Recipe<SingleRecipeInput>> holder = (RecipeHolder<? extends Recipe<SingleRecipeInput>>) (RecipeHolder<?>) recipe.get();
            ItemStack out = holder.value().assemble(input, level.registryAccess());
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

    public static void withdrawEmc(Player player) {
        BigInteger cur = getEmc(player);
        BigInteger cost = BigInteger.valueOf(PortableFurnaceMenu.EMC_PER);
        if (cur.compareTo(cost) >= 0) setEmc(player, cur.subtract(cost));
    }

    public static void withdrawEmc(Player player, int count) {
        BigInteger cur = getEmc(player);
        BigInteger cost = BigInteger.valueOf((long) count * PortableFurnaceMenu.EMC_PER);
        if (cur.compareTo(cost) >= 0) setEmc(player, cur.subtract(cost));
    }

    private static BigInteger getEmc(Player player) {
        IKnowledgeProvider kp = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
        if (kp == null) return BigInteger.ZERO;
        try { return kp.getEmc(); } catch (Throwable t) { return BigInteger.ZERO; }
    }

    private static void setEmc(Player player, BigInteger emc) {
        IKnowledgeProvider kp = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
        if (kp == null) return;
        try {
            kp.setEmc(emc);
            if (player instanceof net.minecraft.server.level.ServerPlayer sp) kp.syncEmc(sp);
        } catch (Throwable ignored) {}
    }
}