package org.wallet.rogalik.wallet.neoforge;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.wallet.rogalik.wallet.client.WalletScreen;
import org.wallet.rogalik.wallet.menu.ModMenuTypes;

class WalletNeoForgeClient
{
	static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
		event.register(ModMenuTypes.WALLET.get(), WalletScreen::new);
	}
}
