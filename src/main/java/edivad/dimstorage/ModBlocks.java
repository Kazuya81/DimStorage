package edivad.dimstorage;

import codechicken.lib.model.ModelRegistryHelper;
import edivad.dimstorage.blocks.DimChest;
import edivad.dimstorage.blocks.DimTank;
import edivad.dimstorage.client.render.item.DimChestItemRender;
import edivad.dimstorage.tile.TileEntityDimChest;
import edivad.dimstorage.tile.TileEntityDimTank;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

	@GameRegistry.ObjectHolder(Main.MODID + ":dimensional_chest")
	public static DimChest dimChest;

	@GameRegistry.ObjectHolder(Main.MODID + ":dimensional_tank")
	public static DimTank dimTank;

	@SideOnly(Side.CLIENT)
	public static void initModels()
	{
		dimChest.initModel();
		dimTank.initModel();
	}

	public static void register(IForgeRegistry<Block> registry)
	{
		registry.register(new DimChest());

		GameRegistry.registerTileEntity(TileEntityDimChest.class, Main.MODID + ":dimensional_chest");
		ModelRegistryHelper.register(new ModelResourceLocation(Main.MODID + ":dimensional_chest"), new DimChestItemRender());
		//ModelLoader.setCustomStateMapper(dimChest, new StateMap.Builder().build());
		//ModelRegistryHelper.register(new ModelResourceLocation(Main.MODID + ":dimensional_chest", "normal"), ParticleDummyModel.INSTANCE);

		registry.register(new DimTank());
		GameRegistry.registerTileEntity(TileEntityDimTank.class, Main.MODID + ":dimensional_tank");
	}
}
