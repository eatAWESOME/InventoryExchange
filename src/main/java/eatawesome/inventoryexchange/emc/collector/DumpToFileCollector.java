package eatawesome.inventoryexchange.emc.collector;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.nio.file.Path;

import eatawesome.inventoryexchange.api.conversion.CustomConversion;
import eatawesome.inventoryexchange.api.conversion.CustomConversionFile;
import eatawesome.inventoryexchange.api.mapper.arithmetic.IValueArithmetic;
import eatawesome.inventoryexchange.api.mapper.collector.IExtendedMappingCollector;
import eatawesome.inventoryexchange.api.nss.NormalizedSimpleStack;
import eatawesome.inventoryexchange.impl.codec.InventoryExchangeCodecHelper;
import net.minecraft.core.HolderLookup;

public class DumpToFileCollector<A extends IValueArithmetic<?>> extends AbstractMappingCollector<NormalizedSimpleStack, Long, A> {

	public static String currentGroupName = "default";
	private final CustomConversionFile out = new CustomConversionFile();
	private final IExtendedMappingCollector<NormalizedSimpleStack, Long, A> inner;
	private final Path path;

	public DumpToFileCollector(Path path, IExtendedMappingCollector<NormalizedSimpleStack, Long, A> inner) {
		super(inner.getArithmetic());
		this.path = path;
		this.inner = inner;
	}

	private Object2IntMap<NormalizedSimpleStack> withoutZeroAmounts(
			Object2IntMap<NormalizedSimpleStack> ingredients) {
		Object2IntMap<NormalizedSimpleStack> filtered = new Object2IntOpenHashMap<>(ingredients.size());
		for (Object2IntMap.Entry<NormalizedSimpleStack> ingredient : ingredients.object2IntEntrySet()) {
			if (ingredient.getIntValue() != 0) {
				filtered.put(ingredient.getKey(), ingredient.getIntValue());
			}
		}
		return filtered;
	}
	
	@Override
	public void setValueFromConversion(int outnumber, NormalizedSimpleStack something,
			Object2IntMap<NormalizedSimpleStack> ingredientsWithAmount) {
		inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
		if (something != null && !ingredientsWithAmount.containsKey(null)) {
			Object2IntMap<NormalizedSimpleStack> ingredients = withoutZeroAmounts(ingredientsWithAmount);
			if (!ingredients.isEmpty()) {
				out.values().addConversion(CustomConversion.getFor(outnumber, something, ingredients));
			}
		}
	}

	@Override
	public void addConversion(int outnumber, NormalizedSimpleStack output,
			Object2IntMap<NormalizedSimpleStack> ingredientsWithAmount, A arithmeticForConversion) {
		inner.addConversion(outnumber, output, ingredientsWithAmount, arithmeticForConversion);
		if (output != null && !ingredientsWithAmount.containsKey(null)) {
			Object2IntMap<NormalizedSimpleStack> ingredients = withoutZeroAmounts(ingredientsWithAmount);
			if (!ingredients.isEmpty()) {
				out.getOrAddGroup(currentGroupName).addConversion(CustomConversion.getFor(outnumber, output, ingredients));
			}
		}
	}

	@Override
	public void setValueBefore(NormalizedSimpleStack something, Long value) {
		inner.setValueBefore(something, value);
		if (something != null) {
			out.values().setValueBefore().put(something, value.longValue());
		}
	}

	@Override
	public void setValueAfter(NormalizedSimpleStack something, Long value) {
		inner.setValueAfter(something, value);
		if (something != null) {
			out.values().setValueAfter().put(something, value.longValue());
		}
	}

	@Override
	public void finishCollection(HolderLookup.Provider registries) {
		InventoryExchangeCodecHelper.writeToFile(registries, path, CustomConversionFile.CODEC, out, "custom conversion");
		inner.finishCollection(registries);
	}
}