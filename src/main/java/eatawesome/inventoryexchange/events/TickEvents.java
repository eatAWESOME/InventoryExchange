package eatawesome.inventoryexchange.events;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.utils.FeedToggle;
import eatawesome.inventoryexchange.utils.PlayerHelper;
import eatawesome.inventoryexchange.utils.PortableFurnaceHelper;
import eatawesome.inventoryexchange.utils.RepairToggle;
import eatawesome.inventoryexchange.utils.SmeltToggle;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import eatawesome.inventoryexchange.handlers.InternalAbilities;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class TickEvents {
	
	private static final Map<UUID, Boolean> wasFlyingMap = new HashMap<>();
	private static final int FLYING_COST = 32;
	private static final int FEED_COST = 128;
	private static final int FEED_POINTS = 4;
	private static final float FEED_SATURATION = 2.4F;
	
	
	@SubscribeEvent
	public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		boolean full = PlayerHelper.hasFullUltimaSet(player);
		if (full) {
			PlayerHelper.updateUltimaEffects(player);
		}
	}
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		
		if (player instanceof ServerPlayer serverPlayer) {
			boolean isFlying = player.getAbilities().flying;
			boolean wasFlying = wasFlyingMap.getOrDefault(player.getUUID(), false);
	
			InternalAbilities.tick(player);
			
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			IAbilityEMCProvider abilityEMC = player.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
			
			// Flying via EMC
			if (isFlying && !player.isCreative() && !player.isSpectator()) {
				if (abilityEMC.getEmc().intValue() >= FLYING_COST) {
					int emc = abilityEMC.getEmc().intValue() - FLYING_COST;
					abilityEMC.setEmc(BigInteger.valueOf(emc));
					abilityEMC.sync(serverPlayer);
				} else if (knowledge.getEmc().intValue() >= 1) {
					int emc = abilityEMC.getEmc().intValue() + 100 - FLYING_COST;
					abilityEMC.setEmc(BigInteger.valueOf(emc));
					abilityEMC.sync(serverPlayer);
					int totalEmc = knowledge.getEmc().intValue() - 1;
					knowledge.setEmc(BigInteger.valueOf(totalEmc));
					knowledge.syncEmc(serverPlayer);
				}
			}
			if (isFlying && !wasFlying) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				wasFlyingMap.put(player.getUUID(), isFlying);
			} else if (wasFlying && !isFlying) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.UNCHARGE.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
				wasFlyingMap.put(player.getUUID(), isFlying);
			}
		
			// Eating via EMC
			if (FeedToggle.isEnabled(player) && player.getFoodData().getFoodLevel() <= 16 && !player.isCreative() && !player.isSpectator()) {
				if (knowledge.getEmc().intValue() >= FEED_COST) {
					player.getFoodData().eat(FEED_POINTS, FEED_SATURATION);
					player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 1.0F);
					int emc = knowledge.getEmc().intValue() - FEED_COST;
					knowledge.setEmc(BigInteger.valueOf(emc));
					knowledge.syncEmc(serverPlayer);
				}
			}
			
			// Repairing via EMC
			if (RepairToggle.isEnabled(player)) {
				double availableEMC = (double) knowledge.getEmc().intValue() + ((double) abilityEMC.getEmc().intValue()) / 100;
				double spentEMC = 0;

				for (ItemStack itemStack : player.getInventory().items) spentEMC += repairItem(player, itemStack, availableEMC - spentEMC);
				for (ItemStack itemStack : player.getInventory().armor) spentEMC += repairItem(player, itemStack, availableEMC - spentEMC);
				for (ItemStack itemStack : player.getInventory().offhand) spentEMC += repairItem(player, itemStack, availableEMC - spentEMC);

				if (spentEMC > 0) {
					int totalEmc = (int) Math.floor(availableEMC - spentEMC);
					int emc = (int) Math.floor((availableEMC - spentEMC - totalEmc) * 100);
					knowledge.setEmc(BigInteger.valueOf(totalEmc));
					knowledge.syncEmc(serverPlayer);
					abilityEMC.setEmc(BigInteger.valueOf(emc));
					abilityEMC.sync(serverPlayer);
				}
			}
			
			// Smelting via EMC
			if (!player.isSpectator()) {
		        if (player.containerMenu instanceof PortableFurnaceMenu) return;
	
		        // Portable Furnace Smelting
		        IItemHandlerModifiable handler = new PortableFurnaceImpl(player).getInventory();
		        if (PortableFurnaceHelper.smeltOneIfPossible(player, handler)) {
		            PortableFurnaceHelper.sync(player);
		        }
		        
		        // Auto Smelting in Inventory
		        if (SmeltToggle.isEnabled(player) && PortableFurnaceHelper.canAfford(player) && !player.isCreative()) {
		        	Level level = player.level();
		    		Inventory inv = player.getInventory();

		    		for (int i = 0; i < inv.items.size(); i++) {
		    			ItemStack itemStack = inv.items.get(i);
		    			if (itemStack.isEmpty()) continue;
		    			if (IEMCProxy.INSTANCE.getValue(itemStack) > 0) continue;
		    			ItemStack one = itemStack.copyWithCount(1);
		    			var opt = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(one), level);
		    			if (opt.isEmpty()) continue;
		    			ItemStack result = opt.get().value().assemble(new SingleRecipeInput(one), level.registryAccess());
		    			if (result.isEmpty()) continue;
		    			if (IEMCProxy.INSTANCE.getValue(result) <= 0) continue;
		    			ItemStack toInsert = result.copy();
		    			boolean fullyAdded = inv.add(toInsert);
		    			if (fullyAdded) {
			    			PortableFurnaceHelper.withdrawEmc(player);
		    				itemStack.shrink(1);
		    				if (itemStack.isEmpty()) inv.items.set(i, ItemStack.EMPTY);
		    			}
		    			break;
		    		}
		        }
			}
			
		}
	}
	
	private static double repairItem(Player player, ItemStack itemStack, double availableEMC) {
		if (availableEMC <= 0 || itemStack.isEmpty() || !itemStack.isDamageableItem() || !itemStack.isDamaged()) return 0;

		ItemStack stackCopy = itemStack.copy();
		stackCopy.setDamageValue(0);
		double cost = IEMCProxy.INSTANCE.getValue(stackCopy) / (double) itemStack.getMaxDamage();

		if (cost <= availableEMC) {
			itemStack.setDamageValue(Math.max(0, itemStack.getDamageValue() - 1));
			return cost;
		}
		return 0;
	}
}