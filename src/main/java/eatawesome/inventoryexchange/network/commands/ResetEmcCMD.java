package eatawesome.inventoryexchange.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import eatawesome.inventoryexchange.InventoryExchangePermissions;
import eatawesome.inventoryexchange.api.nss.NSSItem;
import eatawesome.inventoryexchange.config.InventoryExchangeCustomEMCParser;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ResetEmcCMD {

	private static final DynamicCommandExceptionType INVALID_ITEM = new DynamicCommandExceptionType(InventoryExchangeLang.COMMAND_INVALID_ITEM::translate);

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext context) {
		return Commands.literal("resetemc")
				.requires(InventoryExchangePermissions.COMMAND_RESET_EMC)
				.then(Commands.argument("item", ItemArgument.item(context))
						.executes(ctx -> {
							ItemInput itemInput = ItemArgument.getItem(ctx, "item");
							return resetEmc(ctx, NSSItem.createItem(itemInput.createItemStack(1, false)));
						}))
				.then(Commands.argument("tag", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM))
						.executes(ctx -> {
							Either<ResourceKey<Item>, TagKey<Item>> result = ResourceOrTagKeyArgument.getResourceOrTagKey(ctx, "tag", Registries.ITEM, SetEmcCMD.ERROR_INVALID_ITEM).unwrap();
							return resetEmc(ctx, result.map(NSSItem::createItem, NSSItem::createTag));
						}))
				.executes(ctx -> resetEmc(ctx, RemoveEmcCMD.getHeldStack(ctx)));
	}

	private static int resetEmc(CommandContext<CommandSourceStack> ctx, NSSItem toReset) throws CommandSyntaxException {
		if (InventoryExchangeCustomEMCParser.removeFromFile(toReset)) {
			ctx.getSource().sendSuccess(() -> InventoryExchangeLang.COMMAND_RESET_SUCCESS.translate(toReset), true);
			ctx.getSource().sendSuccess(InventoryExchangeLang.RELOAD_NOTICE::translate, true);
			return Command.SINGLE_SUCCESS;
		}
		throw INVALID_ITEM.create(toReset);
	}
}