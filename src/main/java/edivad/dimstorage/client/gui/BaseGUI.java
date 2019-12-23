package edivad.dimstorage.client.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import edivad.dimstorage.Main;
import edivad.dimstorage.tools.Translate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class BaseGUI extends GuiContainer {

	protected static final int BUTTON_OWNER = 1;
	protected static final int BUTTON_FREQ = 2;
	protected static final int BUTTON_LOCKED = 3;

	private static final int ANIMATION_SPEED = 10;
	private static final int SETTINGS_WIDTH = 80;
	private static final int BUTTON_WIDTH = 20;

	private static enum SettingsState {
		STATE_CLOSED, STATE_OPENNING, STATE_OPENED, STATE_CLOSING
	}

	private ResourceLocation background;
	protected String change, owner, freq, locked, yes, no, inventory, name;

	private GuiButton ownerButton, freqButton, lockedButton;
	protected GuiTextField freqTextField;

	protected int currentFreq;

	private SettingsState state;
	private int animationState;
	protected boolean drawSettings;
	private boolean settingsButtonOver;

	private boolean noConfig;

	public BaseGUI(ResourceLocation background, Container inventory, boolean drawSettings)
	{
		super(inventory);
		this.background = background;

		this.xSize = 176;//176
		this.ySize = 230;//230

		this.state = SettingsState.STATE_CLOSED;
		this.animationState = 0;
		this.drawSettings = false;
		this.settingsButtonOver = false;
		this.noConfig = false;
		
		if(this.drawSettings)
		{
			animationState = SETTINGS_WIDTH;
			state = SettingsState.STATE_OPENED;
		}
	}

	@Override
	public void initGui()
	{
		super.initGui();

		// Get translation
		change = Translate.translateToLocal("gui." + Main.MODID + ".change");
		owner = Translate.translateToLocal("gui." + Main.MODID + ".owner");
		freq = Translate.translateToLocal("gui." + Main.MODID + ".frequency");
		locked = Translate.translateToLocal("gui." + Main.MODID + ".locked");
		yes = Translate.translateToLocal("gui." + Main.MODID + ".yes");
		no = Translate.translateToLocal("gui." + Main.MODID + ".no");
		inventory = Translate.translateToLocal("container.inventory");
		name = Translate.translateToLocal("tile." + Main.MODID + ".dimensional_chest.name");

		// init buttons list
		this.buttonList.clear();

		ownerButton = new GuiButton(BUTTON_OWNER, this.width / 2 + 95, this.height / 2 - 42, 64, 20, change);
		this.buttonList.add(ownerButton);

		freqButton = new GuiButton(BUTTON_FREQ, this.width / 2 + 95, this.height / 2 + 19, 64, 20, change);
		this.buttonList.add(this.freqButton);

		lockedButton = new GuiButton(BUTTON_LOCKED, this.width / 2 + 95, this.height / 2 + 58, 64, 20, no);
		this.buttonList.add(lockedButton);
	}

	@Override
	public void updateScreen()
	{
		freqTextField.updateCursorCounter();

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
	protected void keyTyped(char c, int code) throws IOException
	{
		super.keyTyped(c, code);
		//14 delete
		if((code >= 2 && code <= 11) || code == 14)
		{
			freqTextField.textboxKeyTyped(c, code);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int par3) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, par3);

		if(noConfig)
			return;

		freqTextField.mouseClicked(mouseX, mouseY, par3);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		int buttonX = x + this.xSize;
		int buttonY = y + 16;

		boolean over = false;

		if(mouseX >= buttonX && mouseX <= buttonX + BUTTON_WIDTH)
			if(mouseY >= buttonY && mouseY <= buttonY + BUTTON_WIDTH)
				over = true;

		if(!over)
			return;

		if(state == SettingsState.STATE_CLOSED)
		{
			state = SettingsState.STATE_OPENNING;
		}
		else if(state == SettingsState.STATE_OPENED)
		{
			state = SettingsState.STATE_CLOSING;
			drawSettings(false);
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		int buttonX = x + this.xSize;
		int buttonY = y + 16;

		this.settingsButtonOver = false;

		if(mouseX >= buttonX && mouseX <= buttonX + BUTTON_WIDTH)
			if(mouseY >= buttonY && mouseY <= buttonY + BUTTON_WIDTH)
				settingsButtonOver = true;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		int settingsX = x + (this.xSize - SETTINGS_WIDTH);

		if(!noConfig)
			this.drawTexturedModalRect(settingsX + this.animationState, y + 36, this.xSize, 36, SETTINGS_WIDTH, this.ySize);

		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, 222);

		int buttonX = x + this.xSize;
		int buttonY = y + 16;

		// button background
		this.drawTexturedModalRect(buttonX, buttonY, this.xSize, 16, BUTTON_WIDTH, BUTTON_WIDTH);

		if(state == SettingsState.STATE_CLOSED || state == SettingsState.STATE_OPENNING)
		{
			if(settingsButtonOver)
				this.drawTexturedModalRect(buttonX + 6, buttonY - 3, this.xSize + 28, 16, 8, BUTTON_WIDTH);
			else
				this.drawTexturedModalRect(buttonX + 6, buttonY - 3, this.xSize + 20, 16, 8, BUTTON_WIDTH);
		}
		else if(state == SettingsState.STATE_OPENED || state == SettingsState.STATE_CLOSING)
		{
			if(settingsButtonOver)
				this.drawTexturedModalRect(buttonX + 4, buttonY - 3, this.xSize + 44, 16, 8, BUTTON_WIDTH);
			else
				this.drawTexturedModalRect(buttonX + 4, buttonY - 3, this.xSize + 36, 16, 8, BUTTON_WIDTH);
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		super.drawScreen(par1, par2, par3);

		// freq
		freqTextField.drawTextBox();
	}

	protected void drawSettings(boolean draw)
	{
		drawSettings = draw;

		ownerButton.visible = draw;
		freqButton.visible = draw;
		lockedButton.visible = draw;

		freqTextField.setVisible(draw);
	}

	protected void setTextLockedButton(boolean locked)
	{
		// refresh button label
		lockedButton.displayString = (locked ? yes : no);
	}
}
