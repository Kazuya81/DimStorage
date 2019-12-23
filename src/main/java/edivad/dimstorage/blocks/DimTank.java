package edivad.dimstorage.blocks;

import java.util.List;

import edivad.dimstorage.Main;
import edivad.dimstorage.client.render.tile.TankTESR;
import edivad.dimstorage.tile.TileEntityDimTank;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DimTank extends Block implements ITileEntityProvider {

	public static final ResourceLocation DIMTANK = new ResourceLocation(Main.MODID, "dimensional_tank");

	public DimTank()
	{
		super(Material.GLASS);

		this.setHardness(20F);
		this.setResistance(100F);
		this.setSoundType(SoundType.GLASS);

		setRegistryName(DIMTANK);
		setUnlocalizedName(Main.MODID + "." + "dimensional_tank");

		this.setCreativeTab(Main.tabDimStorage);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityDimTank();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return true;

		FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing);

		TileEntity tile = worldIn.getTileEntity(pos);
		if(!(tile instanceof TileEntityDimTank))
			return false;

		TileEntityDimTank owner = (TileEntityDimTank) tile;

		return !playerIn.isSneaking() && owner.activate(playerIn, worldIn, pos);
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return side == EnumFacing.UP || side == EnumFacing.DOWN;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return new AxisAlignedBB(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 16 / 16D, 14 / 16D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced)
	{
		NBTTagCompound tagCompound = stack.getTagCompound();
		if(tagCompound != null)
		{
			NBTTagCompound nbt = tagCompound.getCompoundTag("tank");
			FluidStack fluidStack = null;
			if(!nbt.hasKey("Empty"))
				fluidStack = FluidStack.loadFluidStackFromNBT(nbt);
			if(fluidStack == null)
				System.out.println("empty");
			else
			{
				String name = fluidStack.getLocalizedName();
				System.out.println(name);
			}
		}
		super.addInformation(stack, player, tooltip, advanced);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityDimTank)
		{
			return ((TileEntityDimTank) tile).getLightValue();
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDimTank.class, new TankTESR());
	}
}
