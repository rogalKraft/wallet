package org.wallet.rogalik.wallet.loot;

import java.util.Set;

import dev.architectury.event.events.common.LootEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import org.wallet.rogalik.wallet.enchantment.ModEnchantments;

/**
 * "Owner's Rope" is deliberately absent from every enchanting-table tag, so the only way to obtain it
 * is as an enchanted book from chests in structures that come with hostile mobs.
 */
public final class WalletLootInjection
{
	private static final Set<ResourceKey<LootTable>> TARGETS = Set.of(
		BuiltInLootTables.SIMPLE_DUNGEON,
		BuiltInLootTables.ABANDONED_MINESHAFT,
		BuiltInLootTables.STRONGHOLD_CORRIDOR,
		BuiltInLootTables.BASTION_TREASURE,
		BuiltInLootTables.PILLAGER_OUTPOST,
		BuiltInLootTables.DESERT_PYRAMID,
		BuiltInLootTables.JUNGLE_TEMPLE,
		BuiltInLootTables.ANCIENT_CITY);

	private static final float CHANCE = 0.12F;

	private WalletLootInjection() {
	}

	public static void init() {
		// Implemented as an anonymous class rather than a lambda since a lambda can't carry the
		// @Override that documents which interface method this is. Architectury 22.0.2 narrowed the
		// provider parameter to HolderGetter.Provider (from HolderLookup.Provider on 21.0.7) and
		// dropped the deprecated 3-arg overload this used to also stub out - it's the only method
		// left on the interface now.
		LootEvent.MODIFY_LOOT_TABLE.register(new LootEvent.ModifyLootTable() {
			@Override
			public void modifyLootTable(HolderGetter.Provider registries, ResourceKey<LootTable> key,
					LootEvent.LootTableModificationContext context, boolean builtin) {
				// Only touch vanilla's own tables; leave datapack overrides alone.
				if (!builtin || !TARGETS.contains(key)) {
					return;
				}

				Holder<Enchantment> ownerRope = ModEnchantments.holder(registries, ModEnchantments.OWNER_ROPE);
				// setRolls and withEnchantment's level both now take an int-typed provider (were a
				// float NumberProvider on 26.2) - the static ConstantValue.exactly(...) factory is
				// gone too, replaced by the plain record constructor wrapped in a direct
				// (non-registry-backed) Holder.
				context.addPool(LootPool.lootPool()
					.setRolls(Holder.direct(new ConstantValue(1)))
					.when(LootItemRandomChanceCondition.randomChance(CHANCE))
					.add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
						.apply(new SetEnchantmentsFunction.Builder()
							.withEnchantment(ownerRope, Holder.direct(new ConstantValue(1))))));
			}
		});
	}
}
