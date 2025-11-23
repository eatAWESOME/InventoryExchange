package eatawesome.inventoryexchange.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class ToolHelper {

	
	public static InteractionResult tryVeinMine(Player player, ItemStack stack, BlockPos pos, Direction sideHit) {
		Level level = player.level();
		BlockState target = level.getBlockState(pos);
		if (target.getDestroySpeed(level, pos) == Block.INDESTRUCTIBLE || !stack.isCorrectToolForDrops(target)) {
			return InteractionResult.FAIL;
		}
		AABB area = WorldHelper.getBroadDeepBox(pos, sideHit, 0);
		return harvestVein(level, player, pos, stack, area, target.getBlock(), BlockStateBase::is, (drops, lvl, p) -> {
			WorldHelper.createLootDrop(drops, lvl, p);
			lvl.playSound(null, p.getX(), p.getY(), p.getZ(), PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		});
	}

	public static <DATA> InteractionResult harvestVein(Level level, Player player, BlockPos dropPos, ItemStack stack, AABB area, DATA data,
			BiPredicate<BlockState, DATA> stateChecker, DropSpawner spawnDrops) {
		List<ItemStack> drops = new ArrayList<>();
		int mined = WorldHelper.harvestVein(level, player, stack, area, drops, data, stateChecker);
		if (mined > 0) {
			if (level instanceof ServerLevel serverLevel) {
				int expTotal = 0;
				if (data instanceof Block block) {
					BlockState sample = block.defaultBlockState();
					for (int i = 0; i < mined; i++) {
						expTotal += block.getExpDrop(sample, serverLevel, dropPos, null, player, stack);
					}
				}
				spawnDrops.drop(drops, level, dropPos);
				if (expTotal > 0) {
					ExperienceOrb.award(serverLevel, Vec3.atCenterOf(dropPos), expTotal);
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}


	@FunctionalInterface
	public interface DropSpawner {
		void drop(List<ItemStack> drops, Level level, BlockPos pos);
	}
	
	public static void digBasedOnMode(ItemStack stack, Level level, BlockPos pos, ServerPlayer serverPlayer, RayTracePointer tracePointer, int mode) {
		if (mode == 0) {
			return;
		}
		BlockHitResult result = tracePointer.rayTrace(level, serverPlayer, ClipContext.Fluid.NONE);
		if (result.getType() == Type.MISS || !pos.equals(result.getBlockPos())) {
			return;
		}
		List<ItemStack> drops = new ArrayList<>();
		int expTotal = 0;
		for (BlockPos digPos : getTargets(pos, serverPlayer, result.getDirection(), mode)) {
			BlockState state = level.getBlockState(digPos);
			if (!state.isAir() && state.getDestroySpeed(level, digPos) != Block.INDESTRUCTIBLE && stack.isCorrectToolForDrops(state)) {
				digPos = digPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) serverPlayer, level, digPos)) {
					if (level instanceof ServerLevel serverLevel) {
						expTotal += state.getBlock().getExpDrop(state, serverLevel, digPos, WorldHelper.getBlockEntity(level, digPos), serverPlayer, stack);
					}
					drops.addAll(Block.getDrops(state, (ServerLevel) level, digPos, WorldHelper.getBlockEntity(level, digPos), serverPlayer, stack));
					level.removeBlock(digPos, false);
					serverPlayer.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
				}
			}
		}
		WorldHelper.createLootDrop(drops, level, pos);
		if (expTotal > 0 && level instanceof ServerLevel serverLevel) {
			ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), expTotal);
		}
	}
	
	public static Iterable<BlockPos> getTargets(BlockPos pos, Player player, Direction sideHit, int mode) {
		return switch (mode) {
			case 1 -> switch (sideHit.getAxis() == Axis.Y ? player.getDirection().getAxis() : sideHit.getAxis()) {
				case X -> BlockPos.betweenClosed(pos.south(), pos.north());
				case Z -> BlockPos.betweenClosed(pos.west(), pos.east());
				default -> Collections.singleton(pos);
			};
			case 2 -> BlockPos.betweenClosed(pos.below(), pos.above());
			case 3 -> BlockPos.betweenClosed(pos, pos.relative(sideHit.getOpposite(), 2));
			default -> Collections.singleton(pos);
		};
	}
	
	@FunctionalInterface
	public interface RayTracePointer {

		BlockHitResult rayTrace(Level level, Player player, ClipContext.Fluid fluidMode);
	}
}