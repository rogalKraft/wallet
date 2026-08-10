package org.wallet.rogalik.wallet.item;

import java.util.UUID;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import org.wallet.rogalik.wallet.Wallet;

public class ModDataComponents
{
	public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Wallet.MOD_ID, Registries.DATA_COMPONENT_TYPE);

	public static final RegistrySupplier<DataComponentType<Boolean>> AUTO_PICKUP = COMPONENTS.register("auto_pickup", () ->
		DataComponentType.<Boolean>builder()
			.persistent(Codec.BOOL)
			.networkSynchronized(ByteBufCodecs.BOOL)
			.build());

	// Absent by default (not attached at Item.Properties time) so its presence marks "has been
	// opened at least once" - stamped with the opening player's name on first use.
	public static final RegistrySupplier<DataComponentType<String>> CREATOR = COMPONENTS.register("creator", () ->
		DataComponentType.<String>builder()
			.persistent(Codec.STRING)
			.networkSynchronized(ByteBufCodecs.STRING_UTF8)
			.build());

	// Stamped alongside CREATOR on first open. CREATOR is the display name; this is the identity the
	// "Owner's Rope" enchantment checks, since names can change but UUIDs can't.
	public static final RegistrySupplier<DataComponentType<UUID>> OWNER_UUID = COMPONENTS.register("owner_uuid", () ->
		DataComponentType.<UUID>builder()
			.persistent(UUIDUtil.CODEC)
			.networkSynchronized(UUIDUtil.STREAM_CODEC)
			.build());

	public static void init() {
		COMPONENTS.register();
	}
}
