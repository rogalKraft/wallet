package org.wallet.rogalik.wallet.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.wallet.rogalik.wallet.Wallet;

public class ModCreativeTabs
{
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Wallet.MOD_ID, Registries.CREATIVE_MODE_TAB);

	public static final RegistrySupplier<CreativeModeTab> WALLET = TABS.register("wallet", () ->
		CreativeTabRegistry.create(builder -> builder
			.title(Component.translatable("itemGroup.wallet"))
			.icon(() -> new ItemStack(ModItems.COIN_500.get()))
			.displayItems((params, output) -> {
				output.accept(ModItems.COIN_1.get());
				output.accept(ModItems.COIN_5.get());
				output.accept(ModItems.COIN_10.get());
				output.accept(ModItems.COIN_20.get());
				output.accept(ModItems.COIN_50.get());
				output.accept(ModItems.COIN_100.get());
				output.accept(ModItems.COIN_500.get());
				output.accept(ModItems.WALLET_COIN_SACK.get());
			})));

	public static void init() {
		TABS.register();
	}
}
