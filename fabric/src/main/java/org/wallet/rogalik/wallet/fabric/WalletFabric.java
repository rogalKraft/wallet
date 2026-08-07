package org.wallet.rogalik.wallet.fabric;

import org.wallet.rogalik.wallet.Wallet;
import net.fabricmc.api.ModInitializer;

public class WalletFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Wallet.init();
    }
}