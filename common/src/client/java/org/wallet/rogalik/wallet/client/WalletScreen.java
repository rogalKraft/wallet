package org.wallet.rogalik.wallet.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.wallet.rogalik.wallet.Wallet;
import org.wallet.rogalik.wallet.menu.WalletMenu;

public class WalletScreen extends AbstractContainerScreen<WalletMenu>
{
	// Own asset - hand-painted, laid out top-left on a 256x256 canvas (same convention as vanilla's
	// own container textures); only the top-left IMAGE_WIDTH x IMAGE_HEIGHT region is used for the
	// panel itself. The auto-pickup toggle's two visual states are reserved off to the side at
	// TOGGLE_OFF_U/TOGGLE_ON_U (outside the visible panel) - draw a 10x10 icon at each spot.
	private static final Identifier CONTAINER_TEXTURE = Identifier.fromNamespaceAndPath(Wallet.MOD_ID, "textures/gui/container/wallet.png");
	private static final int TEXTURE_SIZE = 256;
	private static final int IMAGE_WIDTH = 176;
	private static final int IMAGE_HEIGHT = 167;

	private static final int TOGGLE_SIZE = 10;
	private static final float TOGGLE_OFF_U = 180.0F;
	private static final float TOGGLE_ON_U = 194.0F;
	private static final float TOGGLE_V = 0.0F;
	private static final int TOGGLE_X = 142;
	private static final int TOGGLE_Y = 3;

	public WalletScreen(WalletMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, IMAGE_WIDTH, IMAGE_HEIGHT);
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = 24;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() == 0 && isOverToggle(event.x(), event.y())) {
			this.menu.clickMenuButton(this.minecraft.player, WalletMenu.TOGGLE_AUTO_PICKUP_BUTTON_ID);
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, WalletMenu.TOGGLE_AUTO_PICKUP_BUTTON_ID);
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	private boolean isOverToggle(double mouseX, double mouseY) {
		int x = this.leftPos + TOGGLE_X;
		int y = this.topPos + TOGGLE_Y;
		return mouseX >= x && mouseX < x + TOGGLE_SIZE && mouseY >= y && mouseY < y + TOGGLE_SIZE;
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
		// Vanilla hardcodes the title color to a dark gray meant for a light panel; our art is dark,
		// so redraw the title in white (with a shadow) for contrast instead of calling super.
		guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF, true);
		guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
		super.extractTooltip(guiGraphics, mouseX, mouseY);
		if (isOverToggle(mouseX, mouseY)) {
			guiGraphics.setTooltipForNextFrame(Component.translatable("gui.wallet.auto_pickup"), mouseX, mouseY);
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F,
			this.imageWidth, this.imageHeight, TEXTURE_SIZE, TEXTURE_SIZE);

		float u = this.menu.isAutoPickupEnabled() ? TOGGLE_ON_U : TOGGLE_OFF_U;
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos + TOGGLE_X, this.topPos + TOGGLE_Y,
			u, TOGGLE_V, TOGGLE_SIZE, TOGGLE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
	}
}
