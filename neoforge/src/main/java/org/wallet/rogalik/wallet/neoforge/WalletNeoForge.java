package org.wallet.rogalik.wallet.neoforge;

import org.wallet.rogalik.wallet.Wallet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

// Architectury 21.x no longer exposes a public EventBuses helper - DeferredRegister/registry
// hooks register directly against the platform's registration lifecycle without manual mod
// event bus wiring (see dev.architectury.neoforge.ArchitecturyNeoForge for the internal hookup).
// Client-only setup (like registering a Screen for a MenuType) still needs the mod event bus,
// though - FML supplies IEventBus/Dist by injecting them as constructor parameters (no
// FMLJavaModLoadingContext/DistExecutor in this NeoForge version).
@Mod(Wallet.MOD_ID)
public class WalletNeoForge {
    public WalletNeoForge(IEventBus modEventBus, Dist dist) {
        Wallet.init();
        if (dist == Dist.CLIENT) {
            modEventBus.addListener(WalletNeoForgeClient::onRegisterMenuScreens);
        }
    }
}
