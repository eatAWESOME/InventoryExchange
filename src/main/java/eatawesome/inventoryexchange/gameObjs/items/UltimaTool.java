package eatawesome.inventoryexchange.gameObjs.items;

import org.jetbrains.annotations.NotNull;

import eatawesome.inventoryexchange.utils.ToolHelper;
import eatawesome.inventoryexchange.utils.UltimaToolMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

public final class UltimaTool extends DiggerItem {

	public UltimaTool() {
		super(Tiers.NETHERITE, BlockTags.MINEABLE_WITH_PICKAXE, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
	}

	public static Item create() {
		return new UltimaTool();
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
			|| state.is(BlockTags.MINEABLE_WITH_SHOVEL)
			|| state.is(BlockTags.MINEABLE_WITH_AXE)
			|| state.is(BlockTags.MINEABLE_WITH_HOE);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)
			|| state.is(BlockTags.MINEABLE_WITH_SHOVEL)
			|| state.is(BlockTags.MINEABLE_WITH_AXE)
			|| state.is(BlockTags.MINEABLE_WITH_HOE)
			|| state.is(Blocks.COBWEB)) {
			return 16.0F;
		}
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity miner) {
		if (miner instanceof ServerPlayer serverPlayer) {
			ToolHelper.digBasedOnMode(stack, level, pos, serverPlayer, Item::getPlayerPOVHitResult, UltimaToolMode.getMode(serverPlayer));
		}
		return true;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repairCandidate) {
		return false;
	}
	
	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
		return false;
	}

	@Override
	public boolean isPrimaryItemFor(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
		return false;
	}

	@Override
	public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
		return false;
	}
	
	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		if (context.getLevel().getBlockState(pos).is(Tags.Blocks.ORES)) {
			return ToolHelper.tryVeinMine(player, context.getItemInHand(), pos, context.getClickedFace());
		}
		return InteractionResult.PASS;
	}
}