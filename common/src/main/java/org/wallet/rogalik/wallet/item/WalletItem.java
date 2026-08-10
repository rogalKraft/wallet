package org.wallet.rogalik.wallet.item;

import java.util.UUID;
import java.util.function.Consumer;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.wallet.rogalik.wallet.enchantment.ModEnchantments;
import org.wallet.rogalik.wallet.menu.WalletMenu;

public class WalletItem extends Item
{
	public WalletItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
			if (!mayOpen(stack, player)) {
				player.sendOverlayMessage(Component.translatable("message.wallet.not_owner").withStyle(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}
			if (!stack.has(ModDataComponents.CREATOR.get())) {
				stack.set(ModDataComponents.CREATOR.get(), player.getName().getString());
				stack.set(ModDataComponents.OWNER_UUID.get(), player.getUUID());
			}
			MenuRegistry.openExtendedMenu(serverPlayer, new SimpleMenuProvider(
				(containerId, inventory, menuPlayer) -> WalletMenu.server(containerId, inventory, stack, hand),
				stack.getHoverName()), buf -> {});
		}
		return InteractionResult.SUCCESS;
	}

	/**
	 * "Owner's Rope" locks the wallet to whoever created it. Without the enchantment - or on a wallet
	 * that was never opened, so has no recorded owner - anyone may open it.
	 */
	public static boolean mayOpen(ItemStack stack, Player player) {
		if (!ModEnchantments.has(stack, ModEnchantments.OWNER_ROPE)) {
			return true;
		}
		UUID owner = stack.get(ModDataComponents.OWNER_UUID.get());
		return owner == null || owner.equals(player.getUUID());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

		String creator = stack.get(ModDataComponents.CREATOR.get());
		if (creator != null) {
			// With Owner's Rope the creator is an access check, not just flavour - call that out.
			String key = ModEnchantments.has(stack, ModEnchantments.OWNER_ROPE)
				? "item.wallet.coin_sack.owner"
				: "item.wallet.coin_sack.creator";
			tooltipAdder.accept(Component.translatable(key, creator).withStyle(ChatFormatting.GRAY));
		}

		int totalValue = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
			.nonEmptyItemCopyStream()
			.mapToInt(coin -> ModItems.coinValue(coin.getItem()) * coin.getCount())
			.sum();
		tooltipAdder.accept(Component.translatable("item.wallet.coin_sack.total_value", totalValue).withStyle(ChatFormatting.GOLD));

		addEnchantmentLore(stack, tooltipAdder, ModEnchantments.OWNER_ROPE, "enchantment.wallet.owner_rope.desc");
		addEnchantmentLore(stack, tooltipAdder, ModEnchantments.WALLET_GUARDIAN, "enchantment.wallet.wallet_guardian.desc");
		addEnchantmentLore(stack, tooltipAdder, ModEnchantments.FRAGILITY, "enchantment.wallet.fragility.desc");
	}

	private static void addEnchantmentLore(ItemStack stack, Consumer<Component> tooltipAdder,
			ResourceKey<Enchantment> enchantment, String descriptionKey) {
		if (ModEnchantments.has(stack, enchantment)) {
			tooltipAdder.accept(Component.translatable(descriptionKey).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
		}
	}
}
