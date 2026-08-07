package org.wallet.rogalik.wallet.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.wallet.rogalik.wallet.Wallet;

public class ModItems
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Wallet.MOD_ID, Registries.ITEM);

	public static final RegistrySupplier<Item> COIN_1 = registerCoin("coin_1");
	public static final RegistrySupplier<Item> COIN_5 = registerCoin("coin_5");
	public static final RegistrySupplier<Item> COIN_10 = registerCoin("coin_10");
	public static final RegistrySupplier<Item> COIN_20 = registerCoin("coin_20");
	public static final RegistrySupplier<Item> COIN_50 = registerCoin("coin_50");
	public static final RegistrySupplier<Item> COIN_100 = registerCoin("coin_100");
	public static final RegistrySupplier<Item> COIN_500 = registerCoin("coin_500");

	private static RegistrySupplier<Item> registerCoin(String name) {
		ResourceKey<Item> id = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Wallet.MOD_ID, name));
		return ITEMS.register(name, () -> new Item(new Item.Properties().setId(id)));
	}

	public static void init() {
		ITEMS.register();
	}
}
