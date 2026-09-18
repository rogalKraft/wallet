package org.wallet.rogalik.wallet.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.wallet.rogalik.wallet.Wallet;

/**
 * The mod's three enchantments. They are defined as datapack JSON (see {@code data/wallet/enchantment/})
 * rather than in code, which is how enchantments work since MC 1.21 - so all we keep here are their
 * {@link ResourceKey}s plus helpers.
 *
 * <p>The read helpers deliberately walk the {@code minecraft:enchantments} component and match entries
 * by {@code ResourceKey} instead of resolving a {@code Holder} through {@code registryAccess()}. That
 * keeps them usable where no registry access is at hand - notably {@code Recipe.assemble(input)}, which
 * receives no {@code HolderGetter.Provider} in 26.2/26.3.
 */
public final class ModEnchantments
{
	public static final ResourceKey<Enchantment> WALLET_GUARDIAN = key("wallet_guardian");
	public static final ResourceKey<Enchantment> OWNER_ROPE = key("owner_rope");
	public static final ResourceKey<Enchantment> FRAGILITY = key("fragility");

	private ModEnchantments() {
	}

	private static ResourceKey<Enchantment> key(String name) {
		return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Wallet.MOD_ID, name));
	}

	/** The stack's own Holder for this enchantment, or null when the stack doesn't have it. */
	public static Holder<Enchantment> findHolder(ItemStack stack, ResourceKey<Enchantment> enchantment) {
		for (Holder<Enchantment> holder : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).keySet()) {
			if (holder.is(enchantment)) {
				return holder;
			}
		}
		return null;
	}

	public static int getLevel(ItemStack stack, ResourceKey<Enchantment> enchantment) {
		Holder<Enchantment> holder = findHolder(stack, enchantment);
		return holder == null ? 0 : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(holder);
	}

	public static boolean has(ItemStack stack, ResourceKey<Enchantment> enchantment) {
		return findHolder(stack, enchantment) != null;
	}

	public static void remove(ItemStack stack, ResourceKey<Enchantment> enchantment) {
		EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.removeIf(holder -> holder.is(enchantment)));
	}

	/** Changes the level of an enchantment the stack already has; no-op otherwise. */
	public static void setExistingLevel(ItemStack stack, ResourceKey<Enchantment> enchantment, int level) {
		Holder<Enchantment> holder = findHolder(stack, enchantment);
		if (holder != null) {
			EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(holder, level));
		}
	}

	/**
	 * Adds (or replaces) an enchantment. Needs a registry lookup since the Holder may not be on the
	 * stack yet. Takes the narrower {@code HolderGetter.Provider} (rather than
	 * {@code HolderLookup.Provider}) so callers fed from Architectury's loot event - which as of
	 * 22.0.2 only supplies that narrower type - don't need to cast; a real
	 * {@code HolderLookup.Provider} (e.g. {@code Level.registryAccess()}) satisfies it too, since
	 * that interface extends this one.
	 */
	public static void set(HolderGetter.Provider registries, ItemStack stack, ResourceKey<Enchantment> enchantment, int level) {
		Holder<Enchantment> holder = holder(registries, enchantment);
		EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(holder, level));
	}

	public static Holder<Enchantment> holder(HolderGetter.Provider registries, ResourceKey<Enchantment> enchantment) {
		return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
	}
}
