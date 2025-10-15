package eatawesome.inventoryexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.capabilities.IInfiniteContainerProvider;
import eatawesome.inventoryexchange.capabilities.InventoryExchangeCapabilities;
import eatawesome.inventoryexchange.gameObjs.container.InfiniteContainer;
import eatawesome.inventoryexchange.gameObjs.container.PortableFurnaceMenu;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import eatawesome.inventoryexchange.utils.InfiniteContainerHelper;
import eatawesome.inventoryexchange.utils.PermissionHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/* commands */
@EventBusSubscriber(modid = InventoryExchange.MODID)
public class InventoryExchangeCommands {

	@SubscribeEvent
	public static void register(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		LiteralArgumentBuilder<CommandSourceStack> root =
			Commands.literal("inventoryexchange")
				.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_ROOT, PermissionHelper.ADMIN))
				.then(Commands.literal("view")
					.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_ROOT, PermissionHelper.ADMIN))
					.then(Commands.literal("furnace")
						.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_FURNACE, PermissionHelper.ADMIN))
						.then(Commands.argument("player", EntityArgument.player())
							.executes(ctx -> {
								ServerPlayer viewer = ctx.getSource().getPlayerOrException();
								ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
								openFurnace(viewer, target);
								return 1;
							})
						)
					)
					.then(Commands.literal("infiniteinventory")
						.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_INFINITE_INVENTORY, PermissionHelper.ADMIN))
						.then(Commands.argument("player", EntityArgument.player())
							.executes(ctx -> {
								ServerPlayer viewer = ctx.getSource().getPlayerOrException();
								ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
								openInfinite(viewer, target);
								return 1;
							})
						)
					)
				);

		LiteralArgumentBuilder<CommandSourceStack> alias =
			Commands.literal("ie")
				.requires(src -> PermissionHelper.has(src, CommandPermissions.VIEW_ROOT, PermissionHelper.ADMIN))
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
		var targetInv = InfiniteContainerHelper.getInventory(target);
		viewer.openMenu(new SimpleMenuProvider(
			(id, inv, p) -> new InfiniteContainer(id, inv, targetInv),
			Component.translatable("container.inventoryexchange.infinite_container")
		));
		IInfiniteContainerProvider provider = target.getCapability(InventoryExchangeCapabilities.INFINITE_CONTAINER_CAPABILITY);
		if (provider != null) {
			provider.sync(viewer);
		}
	}
}