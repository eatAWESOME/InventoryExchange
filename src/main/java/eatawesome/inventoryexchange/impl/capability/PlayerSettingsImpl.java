package eatawesome.inventoryexchange.impl.capability;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public final class PlayerSettingsImpl {

	public static final Codec<PlayerSettingsAttachment> CODEC =
		Codec.BOOL.xmap(PlayerSettingsAttachment::new, att -> att.feedEnabled);

	public static class PlayerSettingsAttachment {
		public boolean feedEnabled;

		public PlayerSettingsAttachment() { this(true); }
		public PlayerSettingsAttachment(boolean feedEnabled) { this.feedEnabled = feedEnabled; }

		public PlayerSettingsAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
			return new PlayerSettingsAttachment(feedEnabled);
		}
	}

	private PlayerSettingsImpl() {}
}