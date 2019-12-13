package edivad.dimstorage.client.gui;

import edivad.dimstorage.Main;
import edivad.dimstorage.container.ContainerDimChest;
import edivad.dimstorage.storage.DimChestStorage;
import edivad.dimstorage.tile.TileEntityDimTank;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiDimTank extends BaseGUI {

	private TileEntityDimTank ownerTile;
	private InventoryPlayer invPlayer;

	public GuiDimTank(InventoryPlayer invPlayer, DimChestStorage chestInv, TileEntityDimTank owner)
	{
		super(new ResourceLocation(Main.MODID, "gui/dimtank.png"), new ContainerDimChest(invPlayer, chestInv));
		this.ownerTile = owner;
		this.invPlayer = invPlayer;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		// add Freq textfield
		//currentFreq = ownerTile.frequency.getChannel();
		currentFreq = 1;
		freqTextField = new GuiTextField(0, this.fontRenderer, this.width / 2 + 95, this.height / 2, 64, 15);
		freqTextField.setMaxStringLength(3);
		freqTextField.setFocused(false);
		freqTextField.setText(String.valueOf(currentFreq));

		drawSettings(drawSettings);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
//		if(button.id == BUTTON_OWNER)
//		{
//			ownerTile.swapOwner();
//			PacketHandler.packetReq.sendToServer(new DoBlockUpdate(ownerTile.getPos(), ownerTile.frequency, ownerTile.locked));
//			this.inventorySlots = new ContainerDimChest(invPlayer, ownerTile.getStorage());
//		}
//		else if(button.id == BUTTON_FREQ)
//		{
//			try
//			{
//				int freq = Integer.parseInt(freqTextField.getText());
//				ownerTile.setFreq(ownerTile.frequency.copy().setChannel(freq));
//				PacketHandler.packetReq.sendToServer(new DoBlockUpdate(ownerTile.getPos(), ownerTile.frequency, ownerTile.locked));
//				this.inventorySlots = new ContainerDimChest(invPlayer, ownerTile.getStorage());
//				currentFreq = freq;
//			}
//			catch(Exception e)
//			{
//				freqTextField.setText(String.valueOf(currentFreq));
//			}
//		}
//		else if(button.id == BUTTON_LOCKED)
//		{
//			ownerTile.swapLocked();
//			PacketHandler.packetReq.sendToServer(new DoBlockUpdate(ownerTile.getPos(), ownerTile.frequency, ownerTile.locked));
//		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
//		this.fontRenderer.drawString(name, 8, 6, 4210752);
//		this.fontRenderer.drawString(inventory, 8, 128, 4210752);
//
//		if(!drawSettings)
//			return;
//
//		int posY = 45;
//
//		// owner
//		this.fontRenderer.drawString(owner, 185, posY, 4210752);
//		posY += 9;
//		this.drawHorizontalLine(185, 185 + this.fontRenderer.getStringWidth(owner), posY, 0xFF333333);
//		posY += 6;
//		int width = this.fontRenderer.getStringWidth(ownerTile.frequency.getOwner());
//		this.fontRenderer.drawString(ownerTile.frequency.getOwner(), 215 - width / 2, posY, 4210752);
//		posY += 40;
//
//		// freq
//		this.fontRenderer.drawString(freq.toString(), 185, posY, 4210752);
//		posY += 9;
//		this.drawHorizontalLine(185, 185 + this.fontRenderer.getStringWidth(freq), posY, 0xFF333333);
//		posY += 51;
//
//		// locked
//		this.fontRenderer.drawString(locked, 185, posY, 4210752);
//		posY += 9;
//		this.drawHorizontalLine(185, 185 + this.fontRenderer.getStringWidth(locked), posY, 0xFF333333);
//
//		// refresh button label
//		setTextLockedButton(ownerTile.locked);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		// Render Ice Water
//		int z = this.getFluidScaled(60);
		int z = 60;
		TextureAtlasSprite fluidTexture = mc.getTextureMapBlocks().getTextureExtry(ownerTile.getStorage().getFluid().getFluid().getStill().toString());
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		this.drawTexturedModalRect(this.guiLeft + 10, this.guiTop + 12 + z, fluidTexture, 16, 60 - z);
	}

//	private int getFluidScaled(int pixels)
//	{
//		int currentLiquidAmount = ownerTile.getStorage().getTank().getFluidAmount();
//		int x = currentLiquidAmount * pixels / ownerTile.CAPACITY;
//		return pixels - x;
//	}
}
