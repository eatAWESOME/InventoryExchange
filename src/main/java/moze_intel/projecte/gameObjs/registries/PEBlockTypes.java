package moze_intel.projecte.gameObjs.registries;

import com.mojang.serialization.MapCodec;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.blocks.TransmutationStone;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.BlockTypeDeferredRegister;
import net.minecraft.world.level.block.Block;

public class PEBlockTypes {

	private PEBlockTypes() {
	}

	public static final BlockTypeDeferredRegister BLOCK_TYPES = new BlockTypeDeferredRegister(PECore.PEID);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<TransmutationStone>> TRANSMUTATION_TABLE = BLOCK_TYPES.registerSimple("transmutation_table", TransmutationStone::new);
}