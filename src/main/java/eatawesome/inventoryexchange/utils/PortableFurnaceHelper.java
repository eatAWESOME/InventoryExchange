package eatawesome.inventoryexchange.utils;

import java.math.BigInteger;

import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public final class PortableFurnaceHelper {
	private PortableFurnaceHelper() {}
	
	public static IItemHandlerModifiable getInventory(Player player) {
		return new PortableFurnaceImpl(player).getInventory();
	}
	
	public static void sync(Player player) {
		if (player instanceof ServerPlayer sp) new PortableFurnaceImpl(player).sync(sp);
	}
	
	public static boolean smeltOneIfPossible(Player player, IItemHandlerModifiable handler) {
        ItemStack in = handler.getStackInSlot(0);
        if (in.isEmpty()) return false;
        ItemStack tmpl = recipeFor(player, in);
        if (tmpl.isEmpty()) return false;
        if (!canAfford(player, 1)) return false;

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
        return true;
    }

    private static ItemStack recipeFor(Player player, ItemStack in) {
        var level = player.level();
        var input = new SingleRecipeInput(in);
        var opt = level.getServer().getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level);
        if (opt.isPresent()) {
            RecipeHolder<? extends Recipe<SingleRecipeInput>> holder = (RecipeHolder<? extends Recipe<SingleRecipeInput>>) (RecipeHolder<?>) opt.get();
            ItemStack out = holder.value().assemble(input, level.registryAccess());
            out.setCount(1);
            return out;
        }
        return ItemStack.EMPTY;
    }

    private static boolean canAfford(Player player, int count) {
        BigInteger have = getEmc(player);
        return have.compareTo(BigInteger.valueOf((long) count * PortableFurnaceMenu.EMC_PER)) >= 0;
    }

    private static void withdrawEmc(Player player, int count) {
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