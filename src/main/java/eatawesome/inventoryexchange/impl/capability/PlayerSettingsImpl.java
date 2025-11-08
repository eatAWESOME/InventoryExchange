package eatawesome.inventoryexchange.impl.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public final class PlayerSettingsImpl {

	public static final Codec<PlayerSettingsAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		Codec.BOOL.fieldOf("feedEnabled").orElse(true).forGetter(att -> att.feedEnabled),
		Codec.BOOL.fieldOf("repairEnabled").orElse(false).forGetter(att -> att.repairEnabled),
		Codec.BOOL.fieldOf("smeltEnabled").orElse(false).forGetter(att -> att.smeltEnabled),
		Codec.INT.fieldOf("ultimaToolMode").orElse(0).forGetter(att -> att.ultimaToolMode),
		Codec.INT.fieldOf("ultimaWatchMode").orElse(0).forGetter(att -> att.ultimaWatchMode)
	).apply(inst, PlayerSettingsAttachment::new));

	public static class PlayerSettingsAttachment {
		public boolean feedEnabled;
		public boolean repairEnabled;
		public boolean smeltEnabled;
		public int ultimaToolMode;
		public int ultimaWatchMode;

		public PlayerSettingsAttachment() { this(true, false, false, 0, 0); }
		public PlayerSettingsAttachment(boolean feedEnabled, boolean repairEnabled, boolean smeltEnabled, int ultimaToolMode, int ultimaWatchMode) {
			this.feedEnabled = feedEnabled;
			this.repairEnabled = repairEnabled;
			this.smeltEnabled = smeltEnabled;
			this.ultimaToolMode = ultimaToolMode;
			this.ultimaWatchMode = ultimaWatchMode;
		}

		public PlayerSettingsAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
			return new PlayerSettingsAttachment(feedEnabled, repairEnabled, smeltEnabled, ultimaToolMode, ultimaWatchMode);
		}
	}

	private PlayerSettingsImpl() {}
}