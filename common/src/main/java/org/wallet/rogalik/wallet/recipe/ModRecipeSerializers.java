package org.wallet.rogalik.wallet.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.wallet.rogalik.wallet.Wallet;

/**
 * Only a serializer is needed - {@code SmithingRecipe.getType()} defaults to
 * {@code RecipeType.SMITHING}, so the recipe shows up in the vanilla smithing table with no new
 * RecipeType and no menu changes. {@code RecipeSerializer} is a record in 26.2, so it's constructed
 * directly rather than subclassed.
 */
public class ModRecipeSerializers
{
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
		DeferredRegister.create(Wallet.MOD_ID, Registries.RECIPE_SERIALIZER);

	public static final RegistrySupplier<RecipeSerializer<WalletRepairRecipe>> WALLET_REPAIR =
		SERIALIZERS.register("wallet_repair", () ->
			new RecipeSerializer<>(WalletRepairRecipe.MAP_CODEC, WalletRepairRecipe.STREAM_CODEC));

	public static void init() {
		SERIALIZERS.register();
	}
}
