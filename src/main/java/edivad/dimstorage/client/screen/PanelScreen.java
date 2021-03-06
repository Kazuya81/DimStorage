package edivad.dimstorage.client.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edivad.dimstorage.Main;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.tile.TileEntityDimChest;
import edivad.dimstorage.tools.Config;
import edivad.dimstorage.tools.Translate;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class PanelScreen<T extends Container> extends BaseScreen<T> {

	private static final int ANIMATION_SPEED = 10;
	private static final int SETTINGS_WIDTH = 80;
	private static final int BUTTON_WIDTH = 20;

	private static enum SettingsState {
		STATE_CLOSED, STATE_OPENNING, STATE_OPENED, STATE_CLOSING
	}

	protected static enum Actions {
		OWNER, FREQ, LOCK, COLLECT
	}

	private String change, owner, freq, locked, yes, no, inventory, name, collecting, idle;

	private Button ownerButton, freqButton, lockedButton, collectButton;
	protected TextFieldWidget freqTextField;

	private SettingsState state;
	private int animationState;
	private boolean drawSettings;
	private boolean settingsButtonOver;

	private boolean allowConfig;

	public PanelScreen(T container, PlayerInventory invPlayer, ITextComponent text, ResourceLocation background, boolean drawSettings)
	{
		super(container, invPlayer, text, background);

		this.xSize = 176;//176
		this.ySize = 230;//230

		this.state = SettingsState.STATE_CLOSED;
		this.animationState = 0;
		this.drawSettings = drawSettings;
		this.settingsButtonOver = false;
		this.allowConfig = Config.DIMCHEST_ALLOWCONFIG.get();

		if(this.drawSettings)
		{
			animationState = SETTINGS_WIDTH;
			state = SettingsState.STATE_OPENED;
		}
	}

	@Override
	protected void init()
	{
		super.init();

		// Get translation
		change = Translate.translateToLocal("gui." + Main.MODID + ".change");
		owner = Translate.translateToLocal("gui." + Main.MODID + ".owner");
		freq = Translate.translateToLocal("gui." + Main.MODID + ".frequency");
		locked = Translate.translateToLocal("gui." + Main.MODID + ".locked");
		yes = Translate.translateToLocal("gui." + Main.MODID + ".yes");
		no = Translate.translateToLocal("gui." + Main.MODID + ".no");
		collecting = Translate.translateToLocal("gui." + Main.MODID + ".collecting");
		idle = Translate.translateToLocal("gui." + Main.MODID + ".idle");
		inventory = Translate.translateToLocal("container.inventory");
		name = this.getTitle().getFormattedText();

		// Init buttons list
		this.buttons.clear();

		ownerButton = new Button(this.width / 2 + 95, this.height / 2 - 57, 64, 20, getTileFrequency().getOwner(), button -> actions(Actions.OWNER));
		ownerButton.active = Config.DIMCHEST_ALLOWPRIVATENETWORK.get();
		freqButton = new Button(this.width / 2 + 95, this.height / 2 + 4, 64, 20, change, button -> actions(Actions.FREQ));
		lockedButton = new Button(this.width / 2 + 95, this.height / 2 + 43, 64, 20, isLocked() ? yes : no, button -> actions(Actions.LOCK));
		collectButton = new Button(this.width / 2 + 95, this.height / 2 + 70, 64, 20, isCollecting() ? collecting : idle, button -> actions(Actions.COLLECT));

		this.addButton(ownerButton);
		this.addButton(freqButton);
		this.addButton(lockedButton);
		this.addButton(collectButton);

		freqTextField = new TextFieldWidget(this.font, this.width / 2 + 95, this.height / 2 - 15, 64, 15, String.valueOf(getTileFrequency().getChannel()));
		freqTextField.setMaxStringLength(3);
		freqTextField.setVisible(true);
		freqTextField.setFocused2(false);
		freqTextField.setText(String.valueOf(getTileFrequency().getChannel()));
		children.add(freqTextField);

		drawSettings(drawSettings);
	}

	protected abstract void actions(Actions action);

	protected abstract Frequency getTileFrequency();

	protected abstract boolean isLocked();

	protected abstract boolean isCollecting();

	@Override
	public void tick()
	{
		super.tick();
		freqTextField.tick();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.render(mouseX, mouseY, partialTicks);
		freqTextField.render(mouseX, mouseY, partialTicks);

		this.renderHoveredToolTip(mouseX, mouseY);
		if(mouseX > this.width / 2 + 90 && mouseX < this.width / 2 + 164 && mouseY > this.height / 2 + 65 && mouseY < this.height / 2 + 95)
			this.renderTooltip(Collections.singletonList(Translate.translateToLocal("tooltip." + Main.MODID + ".collect", TileEntityDimChest.AREA, TileEntityDimChest.AREA)), mouseX, mouseY, font);

		if(state == SettingsState.STATE_OPENNING)
		{
			animationState += ANIMATION_SPEED;
			if(animationState >= SETTINGS_WIDTH)
			{
				animationState = SETTINGS_WIDTH;
				state = SettingsState.STATE_OPENED;
				drawSettings(true);
			}
		}
		else if(state == SettingsState.STATE_CLOSING)
		{
			animationState -= ANIMATION_SPEED;
			if(animationState <= 0)
			{
				animationState = 0;
				state = SettingsState.STATE_CLOSED;
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int clickedButton)
	{
		super.mouseClicked(mouseX, mouseY, clickedButton);

		if(!allowConfig)
			return false;

		freqTextField.mouseClicked(mouseX, mouseY, clickedButton);

		boolean over = false;

		if(mouseX >= getButtonX() && mouseX <= getButtonX() + BUTTON_WIDTH)
			if(mouseY >= getButtonY() && mouseY <= getButtonY() + BUTTON_WIDTH)
				over = true;

		if(!over)
			return false;

		if(state == SettingsState.STATE_CLOSED)
		{
			state = SettingsState.STATE_OPENNING;
		}
		else if(state == SettingsState.STATE_OPENED)
		{
			state = SettingsState.STATE_CLOSING;
			drawSettings(false);
		}

		return true;
	}

	public int getButtonX()
	{
		return guiLeft + xSize;
	}

	public int getButtonY()
	{
		return guiTop + 16;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		super.mouseMoved(mouseX, mouseY);

		this.settingsButtonOver = false;

		if(mouseX >= getButtonX() && mouseX <= getButtonX() + BUTTON_WIDTH)
			if(mouseY >= getButtonY() && mouseY <= getButtonY() + BUTTON_WIDTH)
				settingsButtonOver = true;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		super.drawGuiContainerBackgroundLayer(f, i, j);
		int settingsX = guiLeft + (this.xSize - SETTINGS_WIDTH);

		if(allowConfig)
			this.blit(settingsX + this.animationState, guiTop + 36, this.xSize, 36, SETTINGS_WIDTH, this.ySize);

		this.blit(guiLeft, guiTop, 0, 0, this.xSize, 222);

		// button background
		this.blit(getButtonX(), getButtonY(), this.xSize, 16, BUTTON_WIDTH, BUTTON_WIDTH);

		if(state == SettingsState.STATE_CLOSED || state == SettingsState.STATE_OPENNING)
		{
			if(settingsButtonOver)
				this.blit(getButtonX() + 6, getButtonY() - 3, this.xSize + 28, 16, 8, BUTTON_WIDTH);
			else
				this.blit(getButtonX() + 6, getButtonY() - 3, this.xSize + 20, 16, 8, BUTTON_WIDTH);
		}
		else if(state == SettingsState.STATE_OPENED || state == SettingsState.STATE_CLOSING)
		{
			if(settingsButtonOver)
				this.blit(getButtonX() + 4, getButtonY() - 3, this.xSize + 44, 16, 8, BUTTON_WIDTH);
			else
				this.blit(getButtonX() + 4, getButtonY() - 3, this.xSize + 36, 16, 8, BUTTON_WIDTH);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.font.drawString(name, 8, 6, 4210752);
		this.font.drawString(inventory, 8, 128, 4210752);

		if(!drawSettings)
			return;

		int posY = 45;

		// owner
		this.font.drawString(owner, 185, posY, 4210752);
		posY += 9;
		this.hLine(185, 185 + this.font.getStringWidth(owner), posY, 0xFF333333);
		posY += 6;
		this.ownerButton.setMessage(getTileFrequency().getOwner());
		posY += 25;

		// freq
		this.font.drawString(freq, 185, posY, 4210752);
		posY += 9;
		this.hLine(185, 185 + this.font.getStringWidth(freq), posY, 0xFF333333);
		posY += 51;

		// locked
		this.font.drawString(locked, 185, posY, 4210752);
		posY += 9;
		this.hLine(185, 185 + this.font.getStringWidth(locked), posY, 0xFF333333);
		// refresh button label
		this.lockedButton.setMessage(isLocked() ? yes : no);
	}

	public List<Rectangle2d> getAreas()
	{
		List<Rectangle2d> extraAreas = new ArrayList<>();
		extraAreas.add(new Rectangle2d(guiLeft + xSize, getButtonY(), BUTTON_WIDTH, BUTTON_WIDTH));
		extraAreas.add(new Rectangle2d(guiLeft + xSize, getButtonY() + BUTTON_WIDTH, animationState, 180));
		return extraAreas;
	}

	private void drawSettings(boolean draw)
	{
		drawSettings = draw;

		ownerButton.visible = draw;
		freqButton.visible = draw;
		lockedButton.visible = draw;
		collectButton.visible = draw;

		freqTextField.setVisible(draw);
	}
}
