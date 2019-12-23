package edivad.dimstorage.client.gui;

import edivad.dimstorage.Main;
import edivad.dimstorage.container.ContainerDimChest;
import edivad.dimstorage.network.test.DoBlockUpdate;
import edivad.dimstorage.network.test.PacketHandler;
import edivad.dimstorage.storage.DimChestStorage;
import edivad.dimstorage.tile.TileEntityDimChest;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiDimChest extends BaseGUI {

	private TileEntityDimChest ownerTile;
	private InventoryPlayer invPlayer;

	public GuiDimChest(InventoryPlayer invPlayer, DimChestStorage chestInv, TileEntityDimChest owner, boolean drawSettings)
	{
		super(new ResourceLocation(Main.MODID, "gui/dimchest.png"), new ContainerDimChest(invPlayer, chestInv), drawSettings);
		this.ownerTile = owner;
		this.invPlayer = invPlayer;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		// add Freq textfield
		currentFreq = ownerTile.frequency.getChannel();
		freqTextField = new GuiTextField(0, this.fontRenderer, this.width / 2 + 95, this.height / 2, 64, 15);
		freqTextField.setMaxStringLength(3);
		freqTextField.setFocused(false);
		freqTextField.setText(String.valueOf(currentFreq));

		drawSettings(drawSettings);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == BUTTON_OWNER)
		{
			ownerTile.swapOwner();
			PacketHandler.packetReq.sendToServer(new DoBlockUpdate(ownerTile));
		}
		else if(button.id == BUTTON_FREQ)
		{
			try
			{
				int freq = Math.abs(Integer.parseInt(freqTextField.getText()));
				ownerTile.setFreq(ownerTile.frequency.copy().setChannel(freq));
				currentFreq = freq;
				PacketHandler.packetReq.sendToServer(new DoBlockUpdate(ownerTile));
			}
			catch(Exception e)
			{
				freqTextField.setText(String.valueOf(currentFreq));
			}
		}
		else if(button.id == BUTTON_LOCKED)
		{
			ownerTile.swapLocked();
			PacketHandler.packetReq.sendToServer(new DoBlockUpdate(ownerTile));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		this.fontRenderer.drawString(name, 8, 6, 4210752);
		this.fontRenderer.drawString(inventory, 8, 128, 4210752);

		if(!drawSettings)
			return;

		int posY = 45;

		// owner
		this.fontRenderer.drawString(owner, 185, posY, 4210752);
		posY += 9;
		this.drawHorizontalLine(185, 185 + this.fontRenderer.getStringWidth(owner), posY, 0xFF333333);
		posY += 6;
		int width = this.fontRenderer.getStringWidth(ownerTile.frequency.getOwner());
		this.fontRenderer.drawString(ownerTile.frequency.getOwner(), 215 - width / 2, posY, 4210752);
		posY += 40;

		// freq
		this.fontRenderer.drawString(freq.toString(), 185, posY, 4210752);
		posY += 9;
		this.drawHorizontalLine(185, 185 + this.fontRenderer.getStringWidth(freq), posY, 0xFF333333);
		posY += 51;

		// locked
		this.fontRenderer.drawString(locked, 185, posY, 4210752);
		posY += 9;
		this.drawHorizontalLine(185, 185 + this.fontRenderer.getStringWidth(locked), posY, 0xFF333333);

		// refresh button label
		setTextLockedButton(ownerTile.locked);
	}
}
