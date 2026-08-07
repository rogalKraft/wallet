package org.wallet.rogalik.wallet;

import org.wallet.rogalik.wallet.item.ModCreativeTabs;
import org.wallet.rogalik.wallet.item.ModItems;

public class Wallet
{
	public static final String MOD_ID = "wallet";

	public static void init() {
		ModItems.init();
		ModCreativeTabs.init();
	}
}
