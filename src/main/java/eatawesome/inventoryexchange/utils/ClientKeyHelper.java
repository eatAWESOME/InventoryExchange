package eatawesome.inventoryexchange.utils;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.blaze3d.platform.InputConstants;
import eatawesome.inventoryexchange.network.packets.to_server.KeyPressPKT;
import eatawesome.inventoryexchange.utils.text.InventoryExchangeLang;
import eatawesome.inventoryexchange.utils.text.TextComponentUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class ClientKeyHelper {

	private static ImmutableBiMap<InventoryExchangeKeybind, KeyMapping> toMC = ImmutableBiMap.of();

	public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
		ImmutableBiMap.Builder<InventoryExchangeKeybind, KeyMapping> builder = ImmutableBiMap.builder();
		addKeyBinding(event, builder, InventoryExchangeKeybind.CRAFTING, KeyModifier.NONE, GLFW.GLFW_KEY_KP_1);
		addKeyBinding(event, builder, InventoryExchangeKeybind.CONDENSE_TOGGLE, KeyModifier.NONE, GLFW.GLFW_KEY_KP_6);
		addKeyBinding(event, builder, InventoryExchangeKeybind.FEED_TOGGLE, KeyModifier.NONE, GLFW.GLFW_KEY_KP_7);
		addKeyBinding(event, builder, InventoryExchangeKeybind.FURNACE, KeyModifier.NONE, GLFW.GLFW_KEY_KP_2);
		addKeyBinding(event, builder, InventoryExchangeKeybind.INFINITE_CONTAINER, KeyModifier.NONE, GLFW.GLFW_KEY_KP_4);
		addKeyBinding(event, builder, InventoryExchangeKeybind.REPAIR_TOGGLE, KeyModifier.NONE, GLFW.GLFW_KEY_KP_8);
		addKeyBinding(event, builder, InventoryExchangeKeybind.SMELT_TOGGLE, KeyModifier.NONE, GLFW.GLFW_KEY_KP_3);
		addKeyBinding(event, builder, InventoryExchangeKeybind.TRANSMUTATION, KeyModifier.NONE, GLFW.GLFW_KEY_KP_5);
		addKeyBinding(event, builder, InventoryExchangeKeybind.ULTIMA_TOOL_MODE, KeyModifier.NONE, GLFW.GLFW_KEY_KP_9);
		toMC = builder.build();
	}

	private static void addKeyBinding(RegisterKeyMappingsEvent event, ImmutableBiMap.Builder<InventoryExchangeKeybind, KeyMapping> builder, InventoryExchangeKeybind keyBind, KeyModifier modifier, int keyCode) {
		KeyMapping keyMapping = new InventoryExchangeKeyMapping(keyBind, modifier, keyCode);
		builder.put(keyBind, keyMapping);
		event.register(keyMapping);
	}

	public static Component getKeyName(InventoryExchangeKeybind k) {
		KeyMapping keyMapping = toMC.get(k);
		if (keyMapping == null) {
			return TextComponentUtil.build(k);
		}
		return keyMapping.getTranslatedKeyMessage();
	}

	private static class InventoryExchangeKeyMapping extends KeyMapping {

		private final InventoryExchangeKeybind keybind;
		private boolean lastState;

		InventoryExchangeKeyMapping(InventoryExchangeKeybind keybind, KeyModifier keyModifier, int keyCode) {
			super(keybind.getTranslationKey(), KeyConflictContext.IN_GAME, keyModifier, InputConstants.Type.KEYSYM, keyCode, InventoryExchangeLang.INVENTORYEXCHANGE.getTranslationKey());
			this.keybind = keybind;
		}

		@Override
		public void setDown(boolean value) {
			super.setDown(value);
			boolean state = isDown();
			if (state != lastState) {
				if (state) {
					PacketDistributor.sendToServer(new KeyPressPKT(keybind));
				}
				lastState = state;
			}
		}
	}
}