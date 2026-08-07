package org.wallet.rogalik.wallet.neoforge;

import org.wallet.rogalik.wallet.Wallet;
import net.neoforged.fml.common.Mod;

// Architectury 21.x no longer exposes a public EventBuses helper - DeferredRegister/registry
// hooks register directly against the platform's registration lifecycle without manual mod
// event bus wiring (see dev.architectury.neoforge.ArchitecturyNeoForge for the internal hookup).
@Mod(Wallet.MOD_ID)
public class WalletNeoForge {
    public WalletNeoForge() {
        Wallet.init();
    }
}
