package eatawesome.inventoryexchange.utils;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiPredicate;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IAbilityEMCProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.InventoryExchangeTags;
import eatawesome.inventoryexchange.gameObjs.registries.InventoryExchangeItems;
import eatawesome.inventoryexchange.network.packets.to_client.KnowledgeToastPKT;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.ReadOnlyScoreCriteria;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityMultiPlaceEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Helper class for player-related methods. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class PlayerHelper {
    
    public final static ObjectiveCriteria SCOREBOARD_EMC = new ReadOnlyScoreCriteria(PECore.PEID + ":emc_score");
    public final static ObjectiveCriteria SCOREBOARD_ABILITY_EMC = new ReadOnlyScoreCriteria(InventoryExchange.MODID + ":ability_emc_score");

    /**
     * Tries placing a block and fires an event for it.
     *
     * @return Whether the block was successfully placed
     */
    public static boolean checkedPlaceBlock(Player player, Level level, BlockPos pos, BlockState state) {
        return hasEditPermission(player, level, pos) && partiallyCheckedPlaceBlock(player, level, pos, state);
    }

    private static boolean partiallyCheckedPlaceBlock(Player player, Level level, BlockPos pos, BlockState state) {
        SignBlockEntity oldSign = null;
        if (state.getBlock() instanceof SignBlock && level.getBlockEntity(pos) instanceof SignBlockEntity sign) {
            oldSign = sign;
        }
        level.captureBlockSnapshots = true;
        level.setBlockAndUpdate(pos, state);
        level.captureBlockSnapshots = false;

        @SuppressWarnings("unchecked")
        List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>) level.capturedBlockSnapshots.clone();
        level.capturedBlockSnapshots.clear();

        boolean eventResult = false;
        if (blockSnapshots.size() > 1) {
            eventResult = NeoForge.EVENT_BUS.post(new EntityMultiPlaceEvent(blockSnapshots, Blocks.AIR.defaultBlockState(), player)).isCanceled();
        } else if (blockSnapshots.size() == 1) {
            eventResult = NeoForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(blockSnapshots.getFirst(), Blocks.AIR.defaultBlockState(), player)).isCanceled();
        }

        if (eventResult) {
            level.restoringBlockSnapshots = true;
            for (BlockSnapshot snapshot : blockSnapshots.reversed()) {
                snapshot.restore(snapshot.getFlags() | Block.UPDATE_CLIENTS);
            }
            level.restoringBlockSnapshots = false;
        } else {
            for (BlockSnapshot snap : blockSnapshots) {
                BlockState oldBlock = snap.getState();
                BlockPos snapPos = snap.getPos();
                BlockState newBlock = level.getBlockState(snapPos);
                newBlock.onPlace(level, snapPos, oldBlock, false);
                level.markAndNotifyBlock(snapPos, level.getChunkAt(snapPos), oldBlock, newBlock, snap.getFlags(), Block.UPDATE_LIMIT);
                if (oldSign != null && snapPos.equals(pos) && newBlock.hasBlockEntity()) {
                    WorldHelper.copySignData(level, pos, oldSign);
                }
            }
        }
        level.capturedBlockSnapshots.clear();
        return !eventResult;
    }

    public static boolean checkedReplaceBlock(ServerPlayer serverPlayer, Level level, BlockPos pos, BlockState state) {
        return hasBreakPermission(serverPlayer, level, pos) && partiallyCheckedPlaceBlock(serverPlayer, level, pos, state);
    }

    public static ItemStack findFirstItem(Player player, Holder<Item> consumeFrom) {
        for (ItemStack itemStack : player.getInventory().items) {
            if (!itemStack.isEmpty() && itemStack.is(consumeFrom)) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean checkHotbar(Player player, BiPredicate<Player, ItemStack> checker) {
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (!itemStack.isEmpty() && checker.test(player, itemStack)) {
                return true;
            }
        }
        return false;
    }

    public static BlockHitResult getBlockLookingAt(Player player, double maxDistance) {
        return (BlockHitResult) player.pick(maxDistance, 1.0F, false);
    }

    /**
     * Returns a vec representing where the player is looking, capped at maxDistance away.
     */
    public static Vec3 getLookTarget(Player player, double maxDistance) {
        Vec3 lookAngle = player.getLookAngle();
        return player.getEyePosition().add(lookAngle.x * maxDistance, lookAngle.y * maxDistance, lookAngle.z * maxDistance);
    }

    public static boolean hasBreakPermission(ServerPlayer serverPlayer, Level level, BlockPos pos) {
        return hasEditPermission(serverPlayer, level, pos) && checkBreakPermission(serverPlayer, level, pos);
    }

    public static boolean checkBreakPermission(ServerPlayer serverPlayer, Level level, BlockPos pos) {
        return !CommonHooks.fireBlockBreak(level, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer, pos, level.getBlockState(pos)).isCanceled();
    }

    public static boolean hasEditPermission(Player player, Level level, BlockPos pos) {
        if (!player.mayInteract(level, pos)) {
            return false;
        }
        for (Direction e : Constants.DIRECTIONS) {
            if (!player.mayUseItemAt(pos, e, ItemStack.EMPTY)) {
                return false;
            }
        }
        return true;
    }

    public static void updateScore(ServerPlayer serverPlayer, ObjectiveCriteria objective, BigInteger value) {
        updateScore(serverPlayer, objective, MathUtils.clampToInt(value));
    }

    public static void updateScore(ServerPlayer serverPlayer, ObjectiveCriteria objective, int value) {
        serverPlayer.getScoreboard().forAllObjectives(objective, serverPlayer, score -> score.set(value));
    }
    
    public static boolean hasFullUltimaSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(InventoryExchangeItems.ULTIMA_HELMET.get())
            && player.getItemBySlot(EquipmentSlot.CHEST).is(InventoryExchangeItems.ULTIMA_CHESTPLATE.get())
            && player.getItemBySlot(EquipmentSlot.LEGS).is(InventoryExchangeItems.ULTIMA_LEGGINGS.get())
            && player.getItemBySlot(EquipmentSlot.FEET).is(InventoryExchangeItems.ULTIMA_BOOTS.get());
    }
    
    public static void updateUltimaEffects(Player player) {
        updateEffect(player, MobEffects.NIGHT_VISION, 1200, 0);
        updateEffect(player, MobEffects.WATER_BREATHING, 1200, 0);
        updateEffect(player, MobEffects.FIRE_RESISTANCE, 1200, 0);
        updateEffect(player, MobEffects.MOVEMENT_SPEED, 1200, 2);
    }
    
    public static void updateEffect(Player player, Holder<MobEffect> effect, int duration, int amplifier) {
        MobEffectInstance cur = player.getEffect(effect);
        if (cur == null || cur.getDuration() <= 1180 || cur.getAmplifier() != amplifier) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier, true, false, true));
        }
    }
    
    public static void removeUltimaEffects(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
        player.removeEffect(MobEffects.WATER_BREATHING);
        player.removeEffect(MobEffects.FIRE_RESISTANCE);
        player.removeEffect(MobEffects.MOVEMENT_SPEED);
    }

    public static void syncCapabilities(ServerPlayer serverPlayer) {
        IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
        IAbilityEMCProvider abilityEMC = serverPlayer.getCapability(InventoryExchangeCapabilities.ABILITY_EMC_CAPABILITY);
        knowledge.sync(serverPlayer);
        abilityEMC.sync(serverPlayer);
        PECore.debugLog("Synced knowledge and EMC for {}", serverPlayer.getName());
    }

    public static void containerKnowledgeCheck(ServerPlayer serverPlayer, AbstractContainerMenu container) {
        for (ItemStack itemStack : container.getItems()) {
            knowledgeCheck(serverPlayer, itemStack);
        }
        for (ItemStack itemStack : serverPlayer.inventoryMenu.getItems()) {
            knowledgeCheck(serverPlayer, itemStack);
        }
    }
    
    public static void knowledgeCheck(ServerPlayer serverPlayer, ItemStack itemStack) {
        try {
            IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
            if (!itemStack.isEmpty() && IEMCProxy.INSTANCE.getValue(itemStack) > 0) {
                if (!knowledge.hasKnowledge(itemStack)) {
                    knowledge.addKnowledge(itemStack);
                    knowledge.syncKnowledgeChange(serverPlayer, ItemInfo.fromStack(itemStack), true);
                    ItemStack icon = itemStack.copy();
                    icon.setCount(1);
                    PacketDistributor.sendToPlayer(serverPlayer, new KnowledgeToastPKT(icon));
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error retrieving knowledge provider: " + e.getMessage());
        }
        PECore.debugLog("Checked {}'s knowledge for {}", itemStack, serverPlayer.getName());
    }
    
    public static boolean canInventoryAccept(Inventory inv, ItemStack itemStack) {
        if (itemStack.isEmpty()) return true;
        for (ItemStack cur : inv.items) {
            if (cur.isEmpty()) {
                return true;
            }
            if (ItemStack.isSameItemSameComponents(cur, itemStack)) {
                int limit = Math.min(cur.getMaxStackSize(), 64);
                if (cur.getCount() < limit) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static ItemStack insertIntoInventoryMergeOnly(Inventory inv, ItemStack itemStack) {
        if (itemStack.isEmpty()) return itemStack;
        
        // Merge into existing stacks
        for (int i = 0; i < inv.items.size() && !itemStack.isEmpty(); i++) {
            ItemStack cur = inv.items.get(i);
            if (cur.isEmpty()) continue;
            if (!ItemStack.isSameItemSameComponents(cur, itemStack)) continue;

            int limit = Math.min(cur.getMaxStackSize(), 64);
            if (cur.getCount() >= limit) continue;

            int canAdd = Math.min(limit - cur.getCount(), itemStack.getCount());
            if (canAdd <= 0) continue;

            ItemStack newStack = cur.copy();
            newStack.grow(canAdd);
            inv.items.set(i, newStack);
            itemStack.shrink(canAdd);
        }
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }
    
    public static ItemStack insertIntoHandlerMergeOnly(IItemHandlerModifiable handler, ItemStack itemStack) {
        if (itemStack.isEmpty()) return itemStack;
        int slots = handler.getSlots();

        // Merge into existing stacks
        for (int i = 0; i < slots && !itemStack.isEmpty(); i++) {
            ItemStack cur = handler.getStackInSlot(i);
            if (cur.isEmpty()) continue;
            if (!ItemStack.isSameItemSameComponents(cur, itemStack)) continue;

            int limit = Math.min(cur.getMaxStackSize(), handler.getSlotLimit(i));
            if (cur.getCount() >= limit) continue;

            int canAdd = Math.min(limit - cur.getCount(), itemStack.getCount());
            if (canAdd <= 0) continue;

            ItemStack newStack = cur.copy();
            newStack.grow(canAdd);
            handler.setStackInSlot(i, newStack);
            itemStack.shrink(canAdd);
        }
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }

    public static boolean insertIntoHandler(IItemHandlerModifiable handler, ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        boolean changed = false;
        int slots = handler.getSlots();

        // Merge into existing stacks
        for (int i = 0; i < slots && !itemStack.isEmpty(); i++) {
            ItemStack cur = handler.getStackInSlot(i);
            if (cur.isEmpty()) continue;
            if (!ItemStack.isSameItemSameComponents(cur, itemStack)) continue;

            int limit = Math.min(cur.getMaxStackSize(), handler.getSlotLimit(i));
            if (cur.getCount() >= limit) continue;

            int canAdd = Math.min(limit - cur.getCount(), itemStack.getCount());
            if (canAdd <= 0) continue;

            ItemStack newStack = cur.copy();
            newStack.grow(canAdd);
            handler.setStackInSlot(i, newStack);
            itemStack.shrink(canAdd);
            changed = true;
        }

        // Fill empty slots
        for (int i = 0; i < slots && !itemStack.isEmpty(); i++) {
            ItemStack cur = handler.getStackInSlot(i);
            if (!cur.isEmpty()) continue;
            if (!handler.isItemValid(i, itemStack)) continue;

            int toMove = Math.min(itemStack.getCount(), Math.min(itemStack.getMaxStackSize(), handler.getSlotLimit(i)));
            if (toMove <= 0) continue;

            ItemStack place = itemStack.copy();
            place.setCount(toMove);
            handler.setStackInSlot(i, place);
            itemStack.shrink(toMove);
            changed = true;
        }
        return changed;
    }
    
    public static void playPickupSound(Player player) {
        float vol = 0.2F;
        float pitch = ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, vol, pitch);
    }

    public static void dropAndClearHandler(Player source, IItemHandlerModifiable handler, boolean isPortableFurnace) {
        if (handler == null) return;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (isPortableFurnace && i == 1) continue;
            ItemStack itemStack = handler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                ItemStack toDrop = itemStack.copy();
                handler.setStackInSlot(i, ItemStack.EMPTY);
                ItemEntity entity = new ItemEntity(source.level(), source.getX(), source.getY() + 0.5, source.getZ(), toDrop);
                source.level().addFreshEntity(entity);
            }
        }
    }

    public static void clearHandler(IItemHandlerModifiable handler, boolean isPortableFurnace) {
        if (handler == null) return;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (isPortableFurnace && i == 1) continue;
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
    
    public static double repairItem(ServerPlayer serverPlayer, ItemStack itemStack, double availableEMC) {
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
    
    public static boolean hasRepairableItem(Player player) {
    	for (ItemStack itemStack : player.getInventory().items) {
    		if (itemStack.isDamageableItem() && itemStack.isDamaged()) return true;
    	}
		for (ItemStack itemStack : player.getInventory().armor) {
    		if (itemStack.isDamageableItem() && itemStack.isDamaged()) return true;
    	}
		for (ItemStack itemStack : player.getInventory().offhand) {
    		if (itemStack.isDamageableItem() && itemStack.isDamaged()) return true;
    	}
		return false;
    }
    
    public static void awardExp(ServerPlayer serverPlayer, float exp) {
        if (serverPlayer.level() instanceof ServerLevel serverLevel) {
            if (exp <= 0F) return;
            int outputExp = net.minecraft.util.Mth.floor(exp);
            float fractionalExp = exp - outputExp;
            if (fractionalExp > 0F && serverPlayer.getRandom().nextFloat() < fractionalExp) outputExp++;
            ExperienceOrb.award(serverLevel, serverPlayer.position(), outputExp);
        }
    }
    
    public static boolean condenseFirstItem(ServerPlayer serverPlayer) {
        Inventory inventory = serverPlayer.getInventory();
        for (ItemStack itemStack : inventory.items) {
            if (itemStack.isEmpty()) continue;
            if (itemStack.is(InventoryExchangeTags.Items.NO_INVENTORY_CONDENSING)) continue;
            
            long emcValue = IEMCProxy.INSTANCE.getValue(itemStack.copyWithCount(1));
            if (emcValue <= 0) continue;
            
            knowledgeCheck(serverPlayer, itemStack);
            
            IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
            BigInteger current = knowledge.getEmc();
            knowledge.setEmc(current.add(BigInteger.valueOf(emcValue)));
            knowledge.syncEmc(serverPlayer);
            
            itemStack.shrink(1);
            return true;
        }
        IItemHandlerModifiable handler = InfiniteContainerHelper.getInventory(serverPlayer);
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack itemStack = handler.getStackInSlot(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.is(InventoryExchangeTags.Items.NO_INVENTORY_CONDENSING)) continue;
            
            long emcValue = IEMCProxy.INSTANCE.getValue(itemStack.copyWithCount(1));
            if (emcValue <= 0) continue;
            
            knowledgeCheck(serverPlayer, itemStack);
            
            IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
            BigInteger current = knowledge.getEmc();
            knowledge.setEmc(current.add(BigInteger.valueOf(emcValue)));
            knowledge.syncEmc(serverPlayer);
            
            itemStack.shrink(1);
            handler.setStackInSlot(i, itemStack.isEmpty() ? ItemStack.EMPTY : itemStack);
            return true;
        }
        return false;
    }
}