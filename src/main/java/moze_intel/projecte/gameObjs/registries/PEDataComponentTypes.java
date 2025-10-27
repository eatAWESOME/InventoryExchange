package moze_intel.projecte.gameObjs.registries;

import com.mojang.serialization.DataResult;
import moze_intel.projecte.PECore;
import moze_intel.projecte.components.GemData;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.DataComponentTypeDeferredRegister;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.ItemContainerContents;

public class PEDataComponentTypes {

	private PEDataComponentTypes() {
	}

	public static final DataComponentTypeDeferredRegister DATA_COMPONENT_TYPES = new DataComponentTypeDeferredRegister(PECore.PEID);

	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> EYE_INVENTORY = DATA_COMPONENT_TYPES.simple("eye_inventory",
			builder -> builder.persistent(ItemContainerContents.CODEC
							.validate(contents -> contents.getSlots() > 2 ? DataResult.error(() -> "The eye cannot have more than two items stored") : DataResult.success(contents)))
					.networkSynchronized(ItemContainerContents.STREAM_CODEC)
					.cacheEncoding()
	);

	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<Byte>> COOLDOWN = DATA_COMPONENT_TYPES.registerByte("cooldown", (byte) 0, (byte) SharedConstants.TICKS_PER_SECOND);

	/**
	 * @see moze_intel.projecte.api.PEDataComponents#CHARGE
	 */
	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CHARGE = DATA_COMPONENT_TYPES.registerNonNegativeInt("charge");
	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_EXP = DATA_COMPONENT_TYPES.registerNonNegativeInt("stored_exp");

	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<Long>> STORED_EMC = DATA_COMPONENT_TYPES.registerNonNegativeLong("stored_emc");
	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<Double>> UNPROCESSED_EMC = DATA_COMPONENT_TYPES.registerNonNegativeDouble("unprocessed_emc");

	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ACTIVE = DATA_COMPONENT_TYPES.registerBoolean("active");
	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> STEP_ASSIST = DATA_COMPONENT_TYPES.registerBoolean("step_assist");
	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NIGHT_VISION = DATA_COMPONENT_TYPES.registerBoolean("night_vision");

	public static final PEDeferredHolder<DataComponentType<?>, DataComponentType<GemData>> GEM_DATA = DATA_COMPONENT_TYPES.simple("gem_data",
			builder -> builder.persistent(GemData.CODEC).networkSynchronized(GemData.STREAM_CODEC).cacheEncoding());
}