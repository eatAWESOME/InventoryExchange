package eatawesome.inventoryexchange.impl.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public final class PlayerSettingsImpl {

	public static final Codec<PlayerSettingsAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		Codec.BOOL.fieldOf("feedEnabled").orElse(true).forGetter(att -> att.feedEnabled),
		Codec.BOOL.fieldOf("repairEnabled").orElse(false).forGetter(att -> att.repairEnabled)
	).apply(inst, PlayerSettingsAttachment::new));

	public static class PlayerSettingsAttachment {
		public boolean feedEnabled;
		public boolean repairEnabled;

		public PlayerSettingsAttachment() { this(true, false); }
		public PlayerSettingsAttachment(boolean feedEnabled, boolean repairEnabled) {
			this.feedEnabled = feedEnabled;
			this.repairEnabled = repairEnabled;
		}

		public PlayerSettingsAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
			return new PlayerSettingsAttachment(feedEnabled, repairEnabled);
		}
	}

	private PlayerSettingsImpl() {}
}