package eatawesome.inventoryexchange.integration.top;

import java.util.function.Function;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.api.proxy.IEMCProxy;
import eatawesome.inventoryexchange.config.EMCConfig;
import eatawesome.inventoryexchange.utils.EMCHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

//Registered via IMC
@SuppressWarnings("unused")
public class InventoryExchangeProbeInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData data) {
		if (true) {//EMCConfig.server.misc.lookingAtDisplay.get()) {
			long value = IEMCProxy.INSTANCE.getValue(blockState.getBlock());
			if (value > 0) {
				probeInfo.mcText(EMCHelper.getEmcTextComponent(value, 1));
			}
		}
	}

	@Override
	public ResourceLocation getID() {
		return InventoryExchange.rl("emc");
	}

	@Override
	public Void apply(ITheOneProbe iTheOneProbe) {
		iTheOneProbe.registerProvider(this);
		return null;
	}
}