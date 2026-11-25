package eatawesome.inventoryexchange.integration.pixelmon;

import com.pixelmonmod.pixelmon.Pixelmon;
import eatawesome.inventoryexchange.integration.pixelmon.events.PixelmonListener;

public final class PixelmonIntegration {

	private PixelmonIntegration() {}

	public static void register() {
		Pixelmon.EVENT_BUS.register(new PixelmonListener());
	}
}