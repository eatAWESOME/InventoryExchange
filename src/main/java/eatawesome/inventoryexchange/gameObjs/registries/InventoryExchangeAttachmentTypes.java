package eatawesome.inventoryexchange.gameObjs.registries;

import eatawesome.inventoryexchange.InventoryExchange;
import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredHolder;
import eatawesome.inventoryexchange.gameObjs.registration.InventoryExchangeDeferredRegister;
import eatawesome.inventoryexchange.impl.capability.AbilityEMCImpl.AbilityEMCAttachment;
import eatawesome.inventoryexchange.impl.capability.InfiniteContainerImpl;
import eatawesome.inventoryexchange.impl.capability.InfiniteContainerImpl.InfiniteContainerAttachment;
import eatawesome.inventoryexchange.impl.capability.KnowledgeImpl.KnowledgeAttachment;
import eatawesome.inventoryexchange.impl.capability.PlayerSettingsImpl;
import eatawesome.inventoryexchange.impl.capability.PlayerSettingsImpl.PlayerSettingsAttachment;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl;
import eatawesome.inventoryexchange.impl.capability.PortableFurnaceImpl.PortableFurnaceAttachment;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class InventoryExchangeAttachmentTypes {

	private InventoryExchangeAttachmentTypes() {
	}

	public static final InventoryExchangeDeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			new InventoryExchangeDeferredRegister<>(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, InventoryExchange.MODID);

	public static final InventoryExchangeDeferredHolder<AttachmentType<?>, AttachmentType<KnowledgeAttachment>> KNOWLEDGE = ATTACHMENT_TYPES.register("knowledge",
			() -> AttachmentType.builder(KnowledgeAttachment::new)
					.serialize(KnowledgeAttachment.CODEC)
					.copyHandler(KnowledgeAttachment::copy)
					.copyOnDeath()
					.build()
	);
	
	public static final InventoryExchangeDeferredHolder<AttachmentType<?>, AttachmentType<AbilityEMCAttachment>> ABILITY_EMC =
			ATTACHMENT_TYPES.register("ability_emc",
					() -> AttachmentType.builder(AbilityEMCAttachment::new)
							.serialize(AbilityEMCAttachment.CODEC)
							.copyHandler(AbilityEMCAttachment::copy)
							.copyOnDeath()
							.build()
			);

	public static final InventoryExchangeDeferredHolder<AttachmentType<?>, AttachmentType<InfiniteContainerAttachment>> INFINITE_CONTAINER =
			ATTACHMENT_TYPES.register("infinite_container",
					() -> AttachmentType.<InfiniteContainerAttachment>builder(holder -> new InfiniteContainerAttachment())
							.serialize(InfiniteContainerImpl.CODEC)
							.copyHandler(InfiniteContainerAttachment::copy)
							.copyOnDeath()
							.build()
			);
	
	public static final InventoryExchangeDeferredHolder<AttachmentType<?>, AttachmentType<PortableFurnaceAttachment>> PORTABLE_FURNACE =
			ATTACHMENT_TYPES.register("portable_furnace",
					() -> AttachmentType.<PortableFurnaceAttachment>builder(holder -> new PortableFurnaceAttachment())
							.serialize(PortableFurnaceImpl.CODEC)
							.copyHandler(PortableFurnaceAttachment::copy)
							.copyOnDeath()
							.build()
			);

	public static final InventoryExchangeDeferredHolder<AttachmentType<?>, AttachmentType<PlayerSettingsAttachment>> PLAYER_SETTINGS =
		ATTACHMENT_TYPES.register("player_settings",
			() -> AttachmentType.<PlayerSettingsAttachment>builder(holder -> new PlayerSettingsAttachment())
				.serialize(PlayerSettingsImpl.CODEC)
				.copyHandler(PlayerSettingsAttachment::copy)
				.copyOnDeath()
				.build()
		);

	public static <HANDLER extends IItemHandlerModifiable> HANDLER copyHandler(IItemHandler handler, Int2ObjectFunction<HANDLER> handlerCreator) {
		int slots = handler.getSlots();
		HANDLER handlerCopy = handlerCreator.get(slots);
		for (int i = 0; i < slots; i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if (!stack.isEmpty()) {
				handlerCopy.setStackInSlot(i, stack.copy());
			}
		}
		return handlerCopy;
	}
}