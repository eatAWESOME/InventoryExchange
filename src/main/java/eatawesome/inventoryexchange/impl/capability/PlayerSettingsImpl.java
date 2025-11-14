package eatawesome.inventoryexchange.impl.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public final class PlayerSettingsImpl {

	public static final Codec<PlayerSettingsAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		Codec.BOOL.fieldOf("condensingEnabled").orElse(true).forGetter(att -> att.condensingEnabled),
		Codec.BOOL.fieldOf("feedingEnabled").orElse(true).forGetter(att -> att.feedingEnabled),
		Codec.BOOL.fieldOf("repairingEnabled").orElse(false).forGetter(att -> att.repairingEnabled),
		Codec.BOOL.fieldOf("smeltingEnabled").orElse(false).forGetter(att -> att.smeltingEnabled),
		Codec.BOOL.fieldOf("condensingLimited").orElse(true).forGetter(att -> att.condensingLimited),
		Codec.BOOL.fieldOf("feedingLimited").orElse(true).forGetter(att -> att.feedingLimited),
		Codec.BOOL.fieldOf("repairingLimited").orElse(false).forGetter(att -> att.repairingLimited),
		Codec.BOOL.fieldOf("smeltingLimited").orElse(false).forGetter(att -> att.smeltingLimited),
		Codec.INT.fieldOf("ultimaToolMode").orElse(0).forGetter(att -> att.ultimaToolMode),
		Codec.INT.fieldOf("ultimaWatchMode").orElse(0).forGetter(att -> att.ultimaWatchMode)
	).apply(inst, PlayerSettingsAttachment::new));

	public static class PlayerSettingsAttachment {
		public boolean condensingEnabled;
		public boolean feedingEnabled;
		public boolean repairingEnabled;
		public boolean smeltingEnabled;
		public boolean condensingLimited;
		public boolean feedingLimited;
		public boolean repairingLimited;
		public boolean smeltingLimited;
		public int ultimaToolMode;
		public int ultimaWatchMode;

		public PlayerSettingsAttachment() { this(false, true, false, false, true, false, true, false, 0, 0); }
		public PlayerSettingsAttachment(boolean condensingEnabled, boolean feedingEnabled, boolean repairingEnabled, boolean smeltingEnabled, boolean condensingLimited, boolean feedingLimited, boolean repairingLimited, boolean smeltingLimited, int ultimaToolMode, int ultimaWatchMode) {
			this.condensingEnabled = condensingEnabled;
			this.feedingEnabled = feedingEnabled;
			this.repairingEnabled = repairingEnabled;
			this.smeltingEnabled = smeltingEnabled;
			this.condensingLimited = condensingLimited;
			this.feedingLimited = feedingLimited;
			this.repairingLimited = repairingLimited;
			this.smeltingLimited = smeltingLimited;
			this.ultimaToolMode = ultimaToolMode;
			this.ultimaWatchMode = ultimaWatchMode;
		}

		public PlayerSettingsAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
			return new PlayerSettingsAttachment(condensingEnabled, feedingEnabled, repairingEnabled, smeltingEnabled, condensingLimited, feedingLimited, repairingLimited, smeltingLimited, ultimaToolMode, ultimaWatchMode);
		}
	}

	private PlayerSettingsImpl() {}
}