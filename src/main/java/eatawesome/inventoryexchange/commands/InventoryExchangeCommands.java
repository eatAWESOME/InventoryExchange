package eatawesome.inventoryexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IInfiniteContainerProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import eatawesome.inventoryexchange.utils.FeedToggle;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import eatawesome.inventoryexchange.utils.PermissionHelper;
import eatawesome.inventoryexchange.utils.RepairToggle;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
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

		LiteralArgumentBuilder<CommandSourceStack> root =
			Commands.literal("inventoryexchange")
				.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_ROOT, PermissionHelper.BASE))
				.then(Commands.literal("view")
					.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_ROOT, PermissionHelper.BASE))
					.then(Commands.literal("furnace")
						.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_FURNACE, PermissionHelper.BASE))
						.executes(ctx -> {
							ServerPlayer viewer = ctx.getSource().getPlayerOrException();
							openFurnace(viewer, viewer);
							return 1;
						})
						.then(Commands.argument("player", EntityArgument.player())
							.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_FURNACE, PermissionHelper.ADMIN))
							.executes(ctx -> {
								ServerPlayer viewer = ctx.getSource().getPlayerOrException();
								ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
								openFurnace(viewer, target);
								return 1;
							})
						)
					)
					.then(Commands.literal("infiniteinventory")
						.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_INFINITE_INVENTORY, PermissionHelper.BASE))
						.executes(ctx -> {
							ServerPlayer viewer = ctx.getSource().getPlayerOrException();
							openInfinite(viewer, viewer);
							return 1;
						})
						.then(Commands.argument("player", EntityArgument.player())
							.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_INFINITE_INVENTORY, PermissionHelper.ADMIN))
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
					.then(Commands.literal("feeding")
						.requires(src -> PermissionHelper.has(src, CommandPermissions.TOGGLE_FEEDING, PermissionHelper.BASE))
						.executes(ctx -> {
							ServerPlayer self = ctx.getSource().getPlayerOrException();
							FeedToggle.toggleValue(self);
							return 1;
						})
					)
					.then(Commands.literal("repairing")
						.requires(src -> PermissionHelper.has(src, CommandPermissions.TOGGLE_REPAIRING, PermissionHelper.BASE))
						.executes(ctx -> {
							ServerPlayer self = ctx.getSource().getPlayerOrException();
							RepairToggle.toggleValue(self);
							return 1;
						})
					)
				)
				.then(Commands.literal("set")
					.then(Commands.literal("feeding")
						.requires(src -> PermissionHelper.has(src, CommandPermissions.SET_FEEDING, PermissionHelper.BASE))
						.then(Commands.argument("value", BoolArgumentType.bool())
							.executes(ctx -> {
								ServerPlayer self = ctx.getSource().getPlayerOrException();
								boolean value = BoolArgumentType.getBool(ctx, "value");
								FeedToggle.setValue(self, value);
								return 1;
							})
						)
					)
					.then(Commands.literal("repairing")
						.requires(src -> PermissionHelper.has(src, CommandPermissions.SET_REPAIRING, PermissionHelper.BASE))
						.then(Commands.argument("value", BoolArgumentType.bool())
							.executes(ctx -> {
								ServerPlayer self = ctx.getSource().getPlayerOrException();
								boolean value = BoolArgumentType.getBool(ctx, "value");
								RepairToggle.setValue(self, value);
								return 1;
							})
						)
					)
				);

		LiteralArgumentBuilder<CommandSourceStack> alias =
			Commands.literal("ie")
				.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_ROOT, PermissionHelper.BASE))
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
			(id, inv, p) -> new InfiniteContainer(id, inv, InfiniteContainerHelper.getInventory(target)),
			InventoryExchangeLang.INFINITE_CONTAINER_CONTAINER.translate()
		));
		IInfiniteContainerProvider provider = target.getCapability(InventoryExchangeCapabilities.INFINITE_CONTAINER_CAPABILITY);
		if (provider != null) {
			provider.sync(viewer);
		}
	}
}