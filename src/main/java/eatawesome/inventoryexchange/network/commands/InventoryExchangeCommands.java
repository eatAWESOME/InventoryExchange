package eatawesome.inventoryexchange.network.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.InventoryExchangePermissions;
import eatawesome.inventoryexchange.capabilities.IInfiniteContainerProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainerMenu;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import eatawesome.inventoryexchange.utils.CondensingToggle;
import eatawesome.inventoryexchange.utils.FeedingToggle;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import eatawesome.inventoryexchange.utils.RepairingToggle;
import eatawesome.inventoryexchange.utils.SmeltingToggle;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import moze_intel.projecte.network.commands.EMCCMD;
import moze_intel.projecte.network.commands.KnowledgeCMD;
import moze_intel.projecte.network.commands.RemoveEmcCMD;
import moze_intel.projecte.network.commands.ResetEmcCMD;
import moze_intel.projecte.network.commands.SetEmcCMD;
import moze_intel.projecte.network.commands.client.DumpMissingEmc;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = InventoryExchange.MODID)
public class InventoryExchangeCommands {

	@SubscribeEvent
	public static void register(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		CommandBuildContext context = event.getBuildContext();

		LiteralArgumentBuilder<CommandSourceStack> root =
			Commands.literal("inventoryexchange")
				.requires(InventoryExchangePermissions.COMMAND)
				.then(Commands.literal("view")
					.then(Commands.literal("furnace")
						.executes(ctx -> {
							ServerPlayer viewer = ctx.getSource().getPlayerOrException();
							openFurnace(viewer, viewer);
							return 1;
						})
						.then(Commands.argument("player", EntityArgument.player())
							.requires(InventoryExchangePermissions.COMMAND_VIEW_FURNACE_OTHER)
							.executes(ctx -> {
								ServerPlayer viewer = ctx.getSource().getPlayerOrException();
								ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
								openFurnace(viewer, target);
								return 1;
							})
						)
					)
					.then(Commands.literal("infiniteinventory")
						.executes(ctx -> {
							ServerPlayer viewer = ctx.getSource().getPlayerOrException();
							openInfinite(viewer, viewer);
							return 1;
						})
						.then(Commands.argument("player", EntityArgument.player())
							.requires(InventoryExchangePermissions.COMMAND_VIEW_INFINITE_INVENTORY_OTHER)
							.executes(ctx -> {
								ServerPlayer viewer = ctx.getSource().getPlayerOrException();
								ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
								openInfinite(viewer, target);
								return 1;
							})
						)
					)
				)
				.then(Commands.literal("toggle")
					.then(Commands.literal("condensing")
						.executes(ctx -> {
							ServerPlayer self = ctx.getSource().getPlayerOrException();
							CondensingToggle.toggleValue(self);
							return 1;
						})
					)
					.then(Commands.literal("feeding")
						.executes(ctx -> {
							ServerPlayer self = ctx.getSource().getPlayerOrException();
							FeedingToggle.toggleValue(self);
							return 1;
						})
					)
					.then(Commands.literal("repairing")
						.executes(ctx -> {
							ServerPlayer self = ctx.getSource().getPlayerOrException();
							RepairingToggle.toggleValue(self);
							return 1;
						})
					)
					.then(Commands.literal("smelting")
						.executes(ctx -> {
							ServerPlayer self = ctx.getSource().getPlayerOrException();
							SmeltingToggle.toggleValue(self);
							return 1;
						})
					)	
				)
				.then(Commands.literal("set")
					.then(Commands.literal("feeding")
						.then(Commands.argument("value", BoolArgumentType.bool())
							.executes(ctx -> {
								ServerPlayer self = ctx.getSource().getPlayerOrException();
								boolean value = BoolArgumentType.getBool(ctx, "value");
								FeedingToggle.setValue(self, value);
								return 1;
							})
						)
					)
					.then(Commands.literal("condensing")
						.then(Commands.argument("value", BoolArgumentType.bool())
							.executes(ctx -> {
								ServerPlayer self = ctx.getSource().getPlayerOrException();
								boolean value = BoolArgumentType.getBool(ctx, "value");
								CondensingToggle.setValue(self, value);
								return 1;
							})
						)
					)
					.then(Commands.literal("repairing")
						.then(Commands.argument("value", BoolArgumentType.bool())
							.executes(ctx -> {
								ServerPlayer self = ctx.getSource().getPlayerOrException();
								boolean value = BoolArgumentType.getBool(ctx, "value");
								RepairingToggle.setValue(self, value);
								return 1;
							})
						)
					)
					.then(Commands.literal("smelting")
						.then(Commands.argument("value", BoolArgumentType.bool())
							.executes(ctx -> {
								ServerPlayer self = ctx.getSource().getPlayerOrException();
								boolean value = BoolArgumentType.getBool(ctx, "value");
								SmeltingToggle.setValue(self, value);
								return 1;
							})
						)
					)
				)
				.then(RemoveEmcCMD.register(context))
				.then(ResetEmcCMD.register(context))
				.then(SetEmcCMD.register(context))
				.then(EMCCMD.register(context))
				.then(KnowledgeCMD.register(context))
				.then(DumpMissingEmc.register(context));
		

		LiteralArgumentBuilder<CommandSourceStack> alias =
			Commands.literal("ie")
				.requires(InventoryExchangePermissions.COMMAND)
				.redirect(root.build());

		dispatcher.register(root);
		dispatcher.register(alias);
	}

	private static void openFurnace(ServerPlayer viewer, ServerPlayer target) {
		viewer.openMenu(new SimpleMenuProvider(
			(id, inv, p) -> new PortableFurnaceMenu(id, inv, target),
			Component.translatable("container.furnace")
		));
		new PortableFurnaceImpl(target).sync(viewer);
	}

	private static void openInfinite(ServerPlayer viewer, ServerPlayer target) {
		viewer.openMenu(new SimpleMenuProvider(
			(id, inv, p) -> new InfiniteContainerMenu(id, inv, InfiniteContainerHelper.getInventory(target)),
			InventoryExchangeLang.INFINITE_CONTAINER_CONTAINER.translate()
		));
		IInfiniteContainerProvider provider = target.getCapability(InventoryExchangeCapabilities.INFINITE_CONTAINER_CAPABILITY);
		if (provider != null) {
			provider.sync(viewer);
		}
	}
}