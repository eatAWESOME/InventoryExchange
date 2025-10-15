package eatawesome.inventoryexchange.utils;

import net.minecraft.commands.CommandSourceStack;

public final class PermissionHelper {
	
	public static final int BASE = 0;
	public static final int ADMIN = 3;
	
	public static boolean has(CommandSourceStack src, String node, int fallbackLevel) {
		return src.hasPermission(fallbackLevel);
	}
	private PermissionHelper() {}
}