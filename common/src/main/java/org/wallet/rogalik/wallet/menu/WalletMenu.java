package org.wallet.rogalik.wallet.menu;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.wallet.rogalik.wallet.Wallet;
import org.wallet.rogalik.wallet.item.ModDataComponents;

public class WalletMenu extends AbstractContainerMenu
{
	public static final int COIN_SLOT_COLUMNS = 7;
	public static final int COIN_SLOT_ROWS = 3;
	public static final int COIN_SLOT_COUNT = COIN_SLOT_COLUMNS * COIN_SLOT_ROWS;
	public static final int TOGGLE_AUTO_PICKUP_BUTTON_ID = 0;

	// Matches vanilla ChestMenu's own gap formula (18 + rows*18 + 13) so the "Inventory" label has
	// room to render above the player's slots instead of overlapping the coin grid.
	public static final int INVENTORY_Y = 18 + COIN_SLOT_ROWS * 18 + 13;

	public static final TagKey<Item> COINS_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Wallet.MOD_ID, "coins"));

	private final ItemStack walletStack;
	private final InteractionHand hand;
	private final Container coinContainer;
	private final DataSlot autoPickup;
	private final int lockedPlayerSlot;

	/** Client-side factory constructor required by {@code MenuRegistry.ExtendedMenuTypeFactory}; real contents arrive via slot sync. */
	public WalletMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
		this(containerId, playerInventory, ItemStack.EMPTY, null, new SimpleContainer(COIN_SLOT_COUNT));
	}

	public static WalletMenu server(int containerId, Inventory playerInventory, ItemStack walletStack, InteractionHand hand) {
		SimpleContainer container = new SimpleContainer(COIN_SLOT_COUNT);
		NonNullList<ItemStack> items = NonNullList.withSize(COIN_SLOT_COUNT, ItemStack.EMPTY);
		walletStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
		for (int i = 0; i < COIN_SLOT_COUNT; i++) {
			container.setItem(i, items.get(i));
		}
		return new WalletMenu(containerId, playerInventory, walletStack, hand, container);
	}

	private WalletMenu(int containerId, Inventory playerInventory, ItemStack walletStack, InteractionHand hand, Container coinContainer) {
		super(ModMenuTypes.WALLET.get(), containerId);
		this.walletStack = walletStack;
		this.hand = hand;
		this.coinContainer = coinContainer;

		this.autoPickup = this.addDataSlot(DataSlot.standalone());
		this.autoPickup.set(walletStack.getOrDefault(ModDataComponents.AUTO_PICKUP.get(), Boolean.FALSE) ? 1 : 0);

		for (int row = 0; row < COIN_SLOT_ROWS; row++) {
			for (int col = 0; col < COIN_SLOT_COLUMNS; col++) {
				int index = row * COIN_SLOT_COLUMNS + col;
				this.addSlot(new CoinSlot(coinContainer, index, 26 + col * 18, 18 + row * 18));
			}
		}

		// Find which player-inventory slot the wallet itself currently occupies (if any) so it can be
		// locked below - moving the open wallet into another slot mid-session desyncs its contents.
		int lockedSlot = -1;
		for (int i = 0; i < playerInventory.getContainerSize(); i++) {
			if (playerInventory.getItem(i) == walletStack) {
				lockedSlot = i;
				break;
			}
		}
		this.lockedPlayerSlot = lockedSlot;

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				int index = (row + 1) * 9 + col;
				this.addSlot(new PlayerSlot(playerInventory, index, 8 + col * 18, INVENTORY_Y + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new PlayerSlot(playerInventory, col, 8 + col * 18, INVENTORY_Y + 58));
		}
	}

	public boolean isAutoPickupEnabled() {
		return this.autoPickup.get() != 0;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id == TOGGLE_AUTO_PICKUP_BUTTON_ID) {
			boolean newValue = this.autoPickup.get() == 0;
			this.autoPickup.set(newValue ? 1 : 0);
			if (!this.walletStack.isEmpty()) {
				this.walletStack.set(ModDataComponents.AUTO_PICKUP.get(), newValue);
			}
			return true;
		}
		return super.clickMenuButton(player, id);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasItem() && slot.mayPickup(player)) {
			ItemStack stackInSlot = slot.getItem();
			result = stackInSlot.copy();
			if (slotIndex < COIN_SLOT_COUNT) {
				if (!this.moveItemStackTo(stackInSlot, COIN_SLOT_COUNT, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!stackInSlot.typeHolder().is(COINS_TAG) || !this.moveItemStackTo(stackInSlot, 0, COIN_SLOT_COUNT, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (stackInSlot.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return result;
	}

	@Override
	public boolean stillValid(Player player) {
		return player.isAlive() && !this.walletStack.isEmpty();
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		// Write through on every change (not just on close) so contents survive a crash/disconnect
		// and so nothing is lost if `removed()` doesn't fire for some reason.
		if (container == this.coinContainer && !this.walletStack.isEmpty()) {
			writeBackContents();
		}
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (!player.level().isClientSide() && !this.walletStack.isEmpty()) {
			writeBackContents();
			// Belt-and-suspenders: explicitly re-set the held stack too, in case whatever holds the
			// "real" copy (equipment vs. inventory slot tracking) isn't the exact object we mutated.
			if (this.hand != null && player.getItemInHand(this.hand).getItem() == this.walletStack.getItem()) {
				player.setItemInHand(this.hand, this.walletStack);
			}
		}
	}

	private void writeBackContents() {
		List<ItemStack> items = new ArrayList<>(COIN_SLOT_COUNT);
		for (int i = 0; i < COIN_SLOT_COUNT; i++) {
			items.add(this.coinContainer.getItem(i));
		}
		// Denomination exchange is handled via crafting-table recipes (coin_exchange_*.json), not here.
		this.walletStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
	}

	private static class CoinSlot extends Slot
	{
		CoinSlot(Container container, int index, int x, int y) {
			super(container, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return stack.typeHolder().is(COINS_TAG);
		}
	}

	/** Blocks pickup/movement of whichever slot currently holds the open wallet itself. */
	private class PlayerSlot extends Slot
	{
		PlayerSlot(Container container, int index, int x, int y) {
			super(container, index, x, y);
		}

		@Override
		public boolean mayPickup(Player player) {
			return this.getContainerSlot() != WalletMenu.this.lockedPlayerSlot && super.mayPickup(player);
		}
	}
}
