package moze_intel.projecte.common;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public class PEDatapackRegistryProvider extends DatapackBuiltinEntriesProvider {

	public PEDatapackRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of(PECore.PEID));
	}

	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();
}