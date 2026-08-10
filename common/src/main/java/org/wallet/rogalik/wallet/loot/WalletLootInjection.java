package org.wallet.rogalik.wallet.loot;

import java.util.Set;

import dev.architectury.event.events.common.LootEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
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
		// Implemented as an anonymous class rather than a lambda on purpose: the interface's abstract
		// method has no HolderLookup.Provider, but both Fabric's and NeoForge's Architectury impls
		// actually invoke the 4-arg default overload - which is the only way to resolve the
		// Holder<Enchantment> the loot function needs.
		LootEvent.MODIFY_LOOT_TABLE.register(new LootEvent.ModifyLootTable() {
			@Override
			@SuppressWarnings("removal") // deprecated in favour of the Provider-carrying overload below
			public void modifyLootTable(ResourceKey<LootTable> key, LootEvent.LootTableModificationContext context, boolean builtin) {
				// Unused - see the overload below.
			}

			@Override
			public void modifyLootTable(HolderLookup.Provider registries, ResourceKey<LootTable> key,
					LootEvent.LootTableModificationContext context, boolean builtin) {
				// Only touch vanilla's own tables; leave datapack overrides alone.
				if (!builtin || !TARGETS.contains(key)) {
					return;
				}

				Holder<Enchantment> ownerRope = ModEnchantments.holder(registries, ModEnchantments.OWNER_ROPE);
				context.addPool(LootPool.lootPool()
					.setRolls(ConstantValue.exactly(1.0F))
					.when(LootItemRandomChanceCondition.randomChance(CHANCE))
					.add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
						.apply(new SetEnchantmentsFunction.Builder()
							.withEnchantment(ownerRope, ConstantValue.exactly(1.0F)))));
			}
		});
	}
}
