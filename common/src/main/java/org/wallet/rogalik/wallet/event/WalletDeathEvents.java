package org.wallet.rogalik.wallet.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.phys.Vec3;
import org.wallet.rogalik.wallet.enchantment.ModEnchantments;
import org.wallet.rogalik.wallet.item.WalletItem;

/**
 * Death handling for the "Wallet Guardian" / "Fragility" enchantments.
 *
 * <p>Everything hinges on running at the HEAD of {@code die()}, which Architectury's
 * {@code LIVING_DEATH} does. Vanilla's order is {@code die() -> dropAllDeathLoot() -> dropEquipment()
 * -> inventory.dropAll()}, so emptying a slot here happens strictly before vanilla ever iterates the
 * inventory to drop it. That makes the whole thing dupe-proof by construction: the wallet is either
 * in the inventory or in our stash, never both, and vanilla sees {@code EMPTY}.
 *
 * <p>Note this deliberately does not touch the {@code keepInventory} gamerule.
 */
public final class WalletDeathEvents
{
	/**
	 * Wallets pulled out of a dying player's inventory, waiting to be handed back on respawn.
	 * Keyed by player UUID.
	 */
	private static final Map<UUID, List<ItemStack>> STASH = new ConcurrentHashMap<>();

	/**
	 * Guards against double-processing: on Fabric, Architectury's LIVING_DEATH mixin targets
	 * LivingEntity, Player and ServerPlayer, all of which chain die(), so the event fires three times
	 * for a single player death (NeoForge fires it once). Cleared when the player respawns.
	 */
	private static final Set<UUID> PROCESSED = ConcurrentHashMap.newKeySet();

	private WalletDeathEvents() {
	}

	public static void init() {
		EntityEvent.LIVING_DEATH.register((entity, source) -> {
			if (entity instanceof ServerPlayer player && player.level() instanceof ServerLevel serverLevel) {
				onPlayerDeath(player, serverLevel);
			}
			// Never cancel the death itself - we only rearrange the inventory beforehand.
			return EventResult.pass();
		});

		PlayerEvent.PLAYER_CLONE.register((oldPlayer, newPlayer, wonGame) -> {
			// wonGame is true only for the End-portal return, which isn't a death.
			if (!wonGame) {
				restoreStash(newPlayer);
			}
		});

		// Backstop for the case where the player disconnects while dead and respawns on reconnect.
		PlayerEvent.PLAYER_JOIN.register(WalletDeathEvents::restoreStash);
	}

	private static void onPlayerDeath(ServerPlayer player, ServerLevel level) {
		if (!PROCESSED.add(player.getUUID())) {
			return;
		}

		Inventory inventory = player.getInventory();
		List<ItemStack> rescued = new ArrayList<>();

		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.isEmpty() || !(stack.getItem() instanceof WalletItem)) {
				continue;
			}

			if (ModEnchantments.has(stack, ModEnchantments.FRAGILITY)) {
				// The wallet tears open: it is destroyed, but the coins inside hit the ground.
				inventory.setItem(i, ItemStack.EMPTY);
				spillContents(level, player.position(), stack);
			} else {
				int guardian = ModEnchantments.getLevel(stack, ModEnchantments.WALLET_GUARDIAN);
				if (guardian <= 0) {
					continue;
				}
				inventory.setItem(i, ItemStack.EMPTY);
				if (guardian == 1) {
					// Last save - the enchantment burns out into Fragility.
					ModEnchantments.remove(stack, ModEnchantments.WALLET_GUARDIAN);
					ModEnchantments.set(level.registryAccess(), stack, ModEnchantments.FRAGILITY, 1);
				} else {
					ModEnchantments.setExistingLevel(stack, ModEnchantments.WALLET_GUARDIAN, guardian - 1);
				}
				rescued.add(stack);
			}
		}

		if (!rescued.isEmpty()) {
			STASH.computeIfAbsent(player.getUUID(), uuid -> new ArrayList<>()).addAll(rescued);
		}
	}

	private static void spillContents(ServerLevel level, Vec3 pos, ItemStack wallet) {
		wallet.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
			.nonEmptyItemCopyStream()
			.forEach(coin -> {
				ItemEntity dropped = new ItemEntity(level, pos.x, pos.y + 0.5, pos.z, coin);
				dropped.setDefaultPickUpDelay();
				level.addFreshEntity(dropped);
			});

		level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F);
		level.sendParticles(ParticleTypes.POOF, pos.x, pos.y + 0.5, pos.z, 12, 0.2, 0.2, 0.2, 0.02);
	}

	private static void restoreStash(ServerPlayer player) {
		PROCESSED.remove(player.getUUID());
		List<ItemStack> stashed = STASH.remove(player.getUUID());
		if (stashed == null) {
			return;
		}

		for (ItemStack stack : stashed) {
			if (!player.getInventory().add(stack) && player.level() instanceof ServerLevel level) {
				Vec3 pos = player.position();
				ItemEntity dropped = new ItemEntity(level, pos.x, pos.y + 0.5, pos.z, stack);
				dropped.setNoPickUpDelay();
				level.addFreshEntity(dropped);
			}
		}
	}
}
