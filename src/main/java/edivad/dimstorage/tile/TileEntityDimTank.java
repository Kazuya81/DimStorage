package edivad.dimstorage.tile;

import javax.annotation.Nullable;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.fluid.FluidUtils;
import codechicken.lib.packet.PacketCustom;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.manager.DimStorageManager;
import edivad.dimstorage.network.DimStorageSPH;
import edivad.dimstorage.network.TankSynchroniser;
import edivad.dimstorage.storage.DimTankStorage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TileEntityDimTank extends TileFrequencyOwner {

	public class DimTankState extends TankSynchroniser.TankState {

		@Override
		public void sendSyncPacket()
		{
			PacketCustom packet = new PacketCustom(DimStorageSPH.channel, 5);
			packet.writePos(getPos());
			packet.writeFluidStack(s_liquid);
			packet.sendToChunk(world, pos.getX() >> 4, pos.getZ() >> 4);
		}

		@Override
		public void onLiquidChanged()
		{
			world.checkLight(pos);
		}

	}

	public class TankFluidCap implements IFluidHandler {

		@Override
		public IFluidTankProperties[] getTankProperties()
		{

			if(world.isRemote)
			{
				return new IFluidTankProperties [] { new FluidTankProperties(liquid_state.s_liquid, DimTankStorage.CAPACITY) };
			}
			return getStorage().getTankProperties();
		}

		@Override
		public int fill(FluidStack resource, boolean doFill)
		{

			return getStorage().fill(resource, doFill);
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain)
		{

			return getStorage().drain(resource, doDrain);
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain)
		{

			return getStorage().drain(maxDrain, doDrain);
		}
	}

	public int rotation;
	public DimTankState liquid_state = new DimTankState();
	public TankFluidCap fluidCap = new TankFluidCap();

	private boolean described;

	@Override
	public void update()
	{
		super.update();
		ejectLiquid();
		liquid_state.update(world.isRemote);
	}

	private void ejectLiquid()
	{
		for(EnumFacing side : EnumFacing.values())
		{
			IFluidHandler c = FluidUtils.getFluidHandlerOrEmpty(world, getPos().offset(side), side.getOpposite());
			FluidStack liquid = getStorage().drain(100, false);
			if(liquid == null)
			{
				continue;
			}
			int qty = c.fill(liquid, true);
			if(qty > 0)
			{
				getStorage().drain(qty, true);
			}
		}
	}

	@Override
	public void setFreq(Frequency frequency)
	{
		super.setFreq(frequency);
		if(!world.isRemote)
		{
			liquid_state.setFrequency(frequency);
		}
	}

	@Override
	public DimTankStorage getStorage()
	{
		return (DimTankStorage) DimStorageManager.instance(world.isRemote).getStorage(frequency, "liquid");
	}

	@Override
	public void onPlaced(EntityLivingBase entity)
	{
		rotation = (int) Math.floor(entity.rotationYaw * 4 / 360 + 2.5D) & 3;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setByte("rot", (byte) rotation);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		liquid_state.setFrequency(frequency);
		rotation = tag.getByte("rot") & 3;
	}

	@Override
	public void writeToPacket(MCDataOutput packet)
	{
		super.writeToPacket(packet);
		packet.writeByte(rotation);
		packet.writeFluidStack(liquid_state.s_liquid);
	}

	@Override
	public void readFromPacket(MCDataInput packet)
	{
		super.readFromPacket(packet);
		liquid_state.setFrequency(frequency);
		rotation = packet.readUByte() & 3;
		liquid_state.s_liquid = packet.readFluidStack();
		if(!described)
		{
			liquid_state.c_liquid = liquid_state.s_liquid;
		}
		described = true;
	}

	@Override
	public boolean activate(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand)
	{
		return FluidUtil.interactWithFluidHandler(player, hand, getStorage());
	}

	@Override
	public int getLightValue()
	{
		if(liquid_state.s_liquid.amount > 0)
		{
			return FluidUtils.getLuminosity(liquid_state.c_liquid, liquid_state.s_liquid.amount / 16D);
		}
		return 0;
	}

	public void sync(PacketCustom packet)
	{
		if(packet.getType() == 5)
		{
			liquid_state.sync(packet.readFluidStack());
		}
	}

	@Override
	public boolean rotate()
	{
		if(!world.isRemote)
		{
			rotation = (rotation + 1) % 4;
			PacketCustom.sendToChunk(getUpdatePacket(), world, pos.getX() >> 4, pos.getZ() >> 4);
		}
		return true;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
		{
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidCap);
		}
		return super.getCapability(capability, facing);
	}

	//    @Override
	//	public Container createContainer(EntityPlayer player)
	//	{
	//		return null;
	//	}
	//
	//	@Override
	//	public GuiContainer createGui(EntityPlayer player)
	//	{
	//		return new GuiDimTank(player.inventory, chestInv, owner);
	//	}
}
