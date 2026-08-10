package org.wallet.rogalik.wallet.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import org.wallet.rogalik.wallet.client.WalletScreen;
import org.wallet.rogalik.wallet.menu.ModMenuTypes;

public class WalletFabricClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenuTypes.WALLET.get(), WalletScreen::new);
	}
}
