package org.wallet.rogalik.wallet;

import org.wallet.rogalik.wallet.event.WalletDeathEvents;
import org.wallet.rogalik.wallet.event.WalletEvents;
import org.wallet.rogalik.wallet.item.ModCreativeTabs;
import org.wallet.rogalik.wallet.item.ModDataComponents;
import org.wallet.rogalik.wallet.item.ModItems;
import org.wallet.rogalik.wallet.loot.CoinLootInjection;
import org.wallet.rogalik.wallet.loot.WalletLootInjection;
import org.wallet.rogalik.wallet.menu.ModMenuTypes;
import org.wallet.rogalik.wallet.recipe.ModRecipeSerializers;

public class Wallet
{
	public static final String MOD_ID = "wallet";

	public static void init() {
		ModDataComponents.init();
		ModItems.init();
		ModCreativeTabs.init();
		ModMenuTypes.init();
		ModRecipeSerializers.init();
		WalletEvents.init();
		WalletDeathEvents.init();
		WalletLootInjection.init();
		CoinLootInjection.init();
	}
}
