package org.wallet.rogalik.wallet.menu;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import org.wallet.rogalik.wallet.Wallet;

public class ModMenuTypes
{
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Wallet.MOD_ID, Registries.MENU);

	public static final RegistrySupplier<MenuType<WalletMenu>> WALLET = MENUS.register("wallet", () -> MenuRegistry.ofExtended(WalletMenu::new));

	public static void init() {
		MENUS.register();
	}
}
