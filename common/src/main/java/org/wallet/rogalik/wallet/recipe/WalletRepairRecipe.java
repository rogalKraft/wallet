package org.wallet.rogalik.wallet.recipe;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleSmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.wallet.rogalik.wallet.enchantment.ModEnchantments;

/**
 * Smithing-table recipe that mends a torn wallet: it strips "Fragility" and nothing else.
 *
 * <p>A plain JSON {@code minecraft:smithing_transform} would almost work - vanilla's
 * {@code SmithingTransformRecipe.assemble} inherits the base stack's whole component patch, so stored
 * coins would survive - but JSON can only delete the entire {@code minecraft:enchantments} map, which
 * would also wipe "Owner's Rope". Hence this recipe: it copies the base stack (carrying every
 * component: contents, owner, auto-pickup flag, other enchantments) and removes just the one entry.
 */
public class WalletRepairRecipe extends SimpleSmithingRecipe
{
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;

	public WalletRepairRecipe(Recipe.CommonInfo commonInfo, Ingredient template, Ingredient base, Ingredient addition) {
		super(commonInfo);
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input) {
		ItemStack result = input.base().copy();
		ModEnchantments.remove(result, ModEnchantments.FRAGILITY);
		return result;
	}

	@Override
	public Optional<Ingredient> templateIngredient() {
		return Optional.of(this.template);
	}

	@Override
	public Ingredient baseIngredient() {
		return this.base;
	}

	@Override
	public Optional<Ingredient> additionIngredient() {
		return Optional.of(this.addition);
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.create(List.of(this.template, this.base, this.addition));
	}

	@Override
	public RecipeSerializer<? extends SimpleSmithingRecipe> getSerializer() {
		return ModRecipeSerializers.WALLET_REPAIR.get();
	}

	public static final MapCodec<WalletRepairRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
		Ingredient.CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
		Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
		Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition)
	).apply(instance, WalletRepairRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, WalletRepairRecipe> STREAM_CODEC = StreamCodec.composite(
		Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
		Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.template,
		Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.base,
		Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.addition,
		WalletRepairRecipe::new);
}
