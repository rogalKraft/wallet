package org.wallet.rogalik.wallet.loot;

import java.util.Set;

import dev.architectury.event.events.common.LootEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.wallet.rogalik.wallet.item.ModItems;

/**
 * Reproduces the coin-drop distribution from the original datapack (extracted from
 * {@code Wallet2.6_1.21.6}) across vanilla chest/archaeology/spawner loot tables, adapted to this
 * project's own coin items via an additive pool (see {@link WalletLootInjection} for why: Architectury
 * only lets mods add pools, not edit existing ones, so we don't need - and deliberately don't chase -
 * the *current* full content of 55+ vanilla tables, only their identity and our own weight ratios).
 *
 * <p>All four groups below use the original's exact relative weights between denominations; only the
 * per-table "does a coin roll happen at all" chance is new (the original mixed coins into vanilla's own
 * pool at weights that only make sense relative to that pool's other, unreproduced entries).
 */
public final class CoinLootInjection
{
	private static final float CHEST_CHANCE = 0.40F;
	private static final float UNIFORM_CHANCE = 0.25F;
	private static final float END_CITY_CHANCE = 0.60F;

	/** Every table the original touched under data/minecraft/loot_table/chests/, except End City (see below). */
	private static final Set<ResourceKey<LootTable>> CHEST_TABLES = Set.of(
		BuiltInLootTables.ABANDONED_MINESHAFT,
		BuiltInLootTables.ANCIENT_CITY,
		BuiltInLootTables.ANCIENT_CITY_ICE_BOX,
		BuiltInLootTables.BASTION_BRIDGE,
		BuiltInLootTables.BASTION_HOGLIN_STABLE,
		BuiltInLootTables.BASTION_OTHER,
		BuiltInLootTables.BASTION_TREASURE,
		BuiltInLootTables.BURIED_TREASURE,
		BuiltInLootTables.DESERT_PYRAMID,
		BuiltInLootTables.IGLOO_CHEST,
		BuiltInLootTables.JUNGLE_TEMPLE,
		BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER,
		BuiltInLootTables.NETHER_BRIDGE,
		BuiltInLootTables.PILLAGER_OUTPOST,
		BuiltInLootTables.RUINED_PORTAL,
		BuiltInLootTables.SHIPWRECK_MAP,
		BuiltInLootTables.SHIPWRECK_SUPPLY,
		BuiltInLootTables.SHIPWRECK_TREASURE,
		BuiltInLootTables.SIMPLE_DUNGEON,
		BuiltInLootTables.SPAWN_BONUS_CHEST,
		BuiltInLootTables.STRONGHOLD_CORRIDOR,
		BuiltInLootTables.STRONGHOLD_CROSSING,
		BuiltInLootTables.STRONGHOLD_LIBRARY,
		BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR,
		BuiltInLootTables.TRIAL_CHAMBERS_ENTRANCE,
		BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION,
		BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL,
		BuiltInLootTables.TRIAL_CHAMBERS_REWARD,
		BuiltInLootTables.TRIAL_CHAMBERS_REWARD_COMMON,
		BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS,
		BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON,
		BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE,
		BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE,
		BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE,
		BuiltInLootTables.TRIAL_CHAMBERS_REWARD_UNIQUE,
		BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY,
		BuiltInLootTables.UNDERWATER_RUIN_BIG,
		BuiltInLootTables.UNDERWATER_RUIN_SMALL,
		BuiltInLootTables.VILLAGE_ARMORER,
		BuiltInLootTables.VILLAGE_BUTCHER,
		BuiltInLootTables.VILLAGE_CARTOGRAPHER,
		BuiltInLootTables.VILLAGE_DESERT_HOUSE,
		BuiltInLootTables.VILLAGE_FISHER,
		BuiltInLootTables.VILLAGE_FLETCHER,
		BuiltInLootTables.VILLAGE_MASON,
		BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
		BuiltInLootTables.VILLAGE_SAVANNA_HOUSE,
		BuiltInLootTables.VILLAGE_SHEPHERD,
		BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
		BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
		BuiltInLootTables.VILLAGE_TANNERY,
		BuiltInLootTables.VILLAGE_TEMPLE,
		BuiltInLootTables.VILLAGE_TOOLSMITH,
		BuiltInLootTables.VILLAGE_WEAPONSMITH,
		BuiltInLootTables.WOODLAND_MANSION);

	/** The original's archaeology/brush tables plus the trial chamber consumables table - flat 1:1:1:1 weights. */
	private static final Set<ResourceKey<LootTable>> UNIFORM_TABLES = Set.of(
		BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY,
		BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY,
		BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY,
		BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY,
		BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON,
		BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE,
		BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES);

	private CoinLootInjection() {
	}

	public static void init() {
		LootEvent.MODIFY_LOOT_TABLE.register(new LootEvent.ModifyLootTable() {
			@Override
			@SuppressWarnings("removal") // superseded by the Provider-carrying overload below
			public void modifyLootTable(ResourceKey<LootTable> key, LootEvent.LootTableModificationContext context, boolean builtin) {
				// Unused - see the 4-arg overload, which is what both platforms actually invoke.
			}

			@Override
			public void modifyLootTable(HolderLookup.Provider registries, ResourceKey<LootTable> key,
					LootEvent.LootTableModificationContext context, boolean builtin) {
				if (!builtin) {
					return;
				}
				if (CHEST_TABLES.contains(key)) {
					context.addPool(coinPool(CHEST_CHANCE)
						.add(entry(ModItems.COIN_1.get(), 12))
						.add(entry(ModItems.COIN_5.get(), 5))
						.add(entry(ModItems.COIN_10.get(), 2))
						.add(entry(ModItems.COIN_20.get(), 1)));
				} else if (UNIFORM_TABLES.contains(key)) {
					context.addPool(coinPool(UNIFORM_CHANCE)
						.add(entry(ModItems.COIN_1.get(), 1))
						.add(entry(ModItems.COIN_5.get(), 1))
						.add(entry(ModItems.COIN_10.get(), 1))
						.add(entry(ModItems.COIN_20.get(), 1)));
				} else if (key == BuiltInLootTables.SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS) {
					context.addPool(coinPool(UNIFORM_CHANCE)
						.add(entry(ModItems.COIN_10.get(), 1))
						.add(entry(ModItems.COIN_20.get(), 1)));
				} else if (key == BuiltInLootTables.END_CITY_TREASURE) {
					// Fills the gap the original left open: 50/100/500 never dropped anywhere in it,
					// so the End's own treasure room is where the mod's big denominations live instead.
					context.addPool(coinPool(END_CITY_CHANCE)
						.add(entry(ModItems.COIN_20.get(), 4))
						.add(entry(ModItems.COIN_50.get(), 3))
						.add(entry(ModItems.COIN_100.get(), 2))
						.add(entry(ModItems.COIN_500.get(), 1)));
				}
			}
		});
	}

	private static LootPool.Builder coinPool(float chance) {
		return LootPool.lootPool()
			.setRolls(ConstantValue.exactly(1.0F))
			.when(LootItemRandomChanceCondition.randomChance(chance));
	}

	private static LootPoolSingletonContainer.Builder<?> entry(Item item, int weight) {
		return LootItem.lootTableItem(item).setWeight(weight);
	}
}
