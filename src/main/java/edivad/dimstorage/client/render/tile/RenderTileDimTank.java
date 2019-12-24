package edivad.dimstorage.client.render.tile;

import org.lwjgl.opengl.GL11;

import edivad.dimstorage.storage.DimTankStorage;
import edivad.dimstorage.tile.TileEntityDimTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileDimTank extends TileEntitySpecialRenderer<TileEntityDimTank> {

	public float TANK_THICKNESS = 0.04f;

	public RenderTileDimTank()
	{
	}

	@Override
	public void render(TileEntityDimTank tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		GlStateManager.pushMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.translate((float) x, (float) y, (float) z);

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		renderFluid(tileEntity);

		GlStateManager.popMatrix();
	}

	private void renderFluid(TileEntityDimTank tank)
	{
		if(tank == null)
			return;

		FluidStack fluid = tank.liquid_state.c_liquid;
		System.out.println(tank.frequency + " " + fluid.getFluid().getName() + " " + fluid.amount);
		if(fluid == null)
			return;

		Fluid renderFluid = fluid.getFluid();
		if(renderFluid == null)
			return;

		float scale = (1.0f - TANK_THICKNESS / 2 - TANK_THICKNESS) * fluid.amount / (DimTankStorage.CAPACITY);
		if(scale > 0.0f)
		{
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder renderer = tessellator.getBuffer();
			ResourceLocation still = renderFluid.getStill();
			TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(still.toString());

			RenderHelper.disableStandardItemLighting();

			GlStateManager.color(1, 1, 1, .5f);
			renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

			float u1 = sprite.getMinU();
			float v1 = sprite.getMinV();
			float u2 = sprite.getMaxU();
			float v2 = sprite.getMaxV();

			float margin = 0.9f;

			// Top
			renderer.pos(TANK_THICKNESS + 0.1f, scale + TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u1, v1).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, scale + TANK_THICKNESS, margin - TANK_THICKNESS).tex(u1, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, scale + TANK_THICKNESS, margin - TANK_THICKNESS).tex(u2, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, scale + TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u2, v1).color(255, 255, 255, 128).endVertex();

			// Bottom
			renderer.pos(margin - TANK_THICKNESS, TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u2, v1).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, TANK_THICKNESS, margin - TANK_THICKNESS).tex(u2, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, TANK_THICKNESS, margin - TANK_THICKNESS).tex(u1, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u1, v1).color(255, 255, 255, 128).endVertex();

			// Sides
			//NORTH
			renderer.pos(TANK_THICKNESS + 0.1f, scale + TANK_THICKNESS, margin - TANK_THICKNESS).tex(u1, v1).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, TANK_THICKNESS, margin - TANK_THICKNESS).tex(u1, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, TANK_THICKNESS, margin - TANK_THICKNESS).tex(u2, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, scale + TANK_THICKNESS, margin - TANK_THICKNESS).tex(u2, v1).color(255, 255, 255, 128).endVertex();

			//SOUTH
			renderer.pos(margin - TANK_THICKNESS, scale + TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u2, v1).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u2, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u1, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, scale + TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u1, v1).color(255, 255, 255, 128).endVertex();

			//WEAST
			renderer.pos(margin - TANK_THICKNESS, scale + TANK_THICKNESS, margin - TANK_THICKNESS).tex(u2, v1).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, TANK_THICKNESS, margin - TANK_THICKNESS).tex(u2, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u1, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(margin - TANK_THICKNESS, scale + TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u1, v1).color(255, 255, 255, 128).endVertex();

			//EAST
			renderer.pos(TANK_THICKNESS + 0.1f, scale + TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u1, v1).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, TANK_THICKNESS, TANK_THICKNESS + 0.1f).tex(u1, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, TANK_THICKNESS, margin - TANK_THICKNESS).tex(u2, v2).color(255, 255, 255, 128).endVertex();
			renderer.pos(TANK_THICKNESS + 0.1f, scale + TANK_THICKNESS, margin - TANK_THICKNESS).tex(u2, v1).color(255, 255, 255, 128).endVertex();

			tessellator.draw();

			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		}
	}
}
