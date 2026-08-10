package org.wallet.rogalik.wallet.event;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.wallet.rogalik.wallet.item.ModDataComponents;
import org.wallet.rogalik.wallet.item.ModItems;
import org.wallet.rogalik.wallet.menu.WalletMenu;

public class WalletEvents
{
	public static void init() {
		PlayerEvent.PICKUP_ITEM_POST.register(WalletEvents::onPickupItem);
	}

	private static void onPickupItem(Player player, ItemEntity itemEntity, ItemStack stack) {
		if (stack.isEmpty() || !stack.typeHolder().is(WalletMenu.COINS_TAG)) {
			return;
		}

		Inventory inventory = player.getInventory();
		int remaining = stack.getCount();
		for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
			ItemStack candidate = inventory.getItem(i);
			if (candidate.getItem() != ModItems.WALLET_COIN_SACK.get()
					|| !candidate.getOrDefault(ModDataComponents.AUTO_PICKUP.get(), Boolean.FALSE)) {
				continue;
			}

			int moved = insertCoins(candidate, stack.getItem(), remaining);
			if (moved > 0) {
				removeFromInventory(inventory, stack.getItem(), moved);
				remaining -= moved;
			}
		}
	}

	private static int insertCoins(ItemStack walletStack, Item coinItem, int amount) {
		NonNullList<ItemStack> slots = NonNullList.withSize(WalletMenu.COIN_SLOT_COUNT, ItemStack.EMPTY);
		walletStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(slots);

		int remaining = amount;
		for (int i = 0; i < slots.size() && remaining > 0; i++) {
			ItemStack slotStack = slots.get(i);
			if (slotStack.isEmpty() || slotStack.getItem() != coinItem) {
				continue;
			}
			int take = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), remaining);
			if (take > 0) {
				slotStack.grow(take);
				remaining -= take;
			}
		}
		for (int i = 0; i < slots.size() && remaining > 0; i++) {
			if (!slots.get(i).isEmpty()) {
				continue;
			}
			int take = Math.min(coinItem.getDefaultMaxStackSize(), remaining);
			slots.set(i, new ItemStack(coinItem, take));
			remaining -= take;
		}

		int inserted = amount - remaining;
		if (inserted > 0) {
			walletStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(slots));
		}
		return inserted;
	}

	private static void removeFromInventory(Inventory inventory, Item coinItem, int amount) {
		int remaining = amount;
		for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
			ItemStack slotStack = inventory.getItem(i);
			if (slotStack.isEmpty() || slotStack.getItem() != coinItem) {
				continue;
			}
			int take = Math.min(slotStack.getCount(), remaining);
			slotStack.shrink(take);
			remaining -= take;
		}
	}
}
