package moze_intel.projecte.api.capabilities;

import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class PECapabilities {

	private PECapabilities() {
	}

	private static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(ProjectEAPI.PROJECTE_MODID, path);
	}

	/**
	 * The capability object for IKnowledgeProvider
	 */
	public static final EntityCapability<IKnowledgeProvider, Void> KNOWLEDGE_CAPABILITY = EntityCapability.createVoid(rl("knowledge"), IKnowledgeProvider.class);
}