package org.wallet.rogalik.wallet.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
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

	public static final RegistrySupplier<Item> WALLET_COIN_SACK = registerWallet("coin_sack");

	private static RegistrySupplier<Item> registerCoin(String name) {
		ResourceKey<Item> id = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Wallet.MOD_ID, name));
		return ITEMS.register(name, () -> new Item(new Item.Properties().setId(id)));
	}

	private static RegistrySupplier<Item> registerWallet(String name) {
		ResourceKey<Item> id = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Wallet.MOD_ID, name));
		return ITEMS.register(name, () -> new WalletItem(new Item.Properties()
			.setId(id)
			.stacksTo(1)
			// The minecraft:enchantable component is what makes the enchanting table accept the item.
			.enchantable(15)
			.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
			.component(ModDataComponents.AUTO_PICKUP.get(), Boolean.FALSE)
			// Hide the vanilla per-slot contents dump; WalletItem.appendHoverText shows creator + total value instead.
			.component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.CONTAINER, true))));
	}

	public static int coinValue(Item item) {
		if (item == COIN_1.get()) return 1;
		if (item == COIN_5.get()) return 5;
		if (item == COIN_10.get()) return 10;
		if (item == COIN_20.get()) return 20;
		if (item == COIN_50.get()) return 50;
		if (item == COIN_100.get()) return 100;
		if (item == COIN_500.get()) return 500;
		return 0;
	}

	public static void init() {
		ITEMS.register();
	}
}
