package fr.max2.dfi.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class BakedModelRetextured implements IBakedModel
{

	private final IBakedModel baseModel;
	private final TextureAtlasSprite topNBot, sides;
	private final float topNBotScale, sidesScale;
	
	public BakedModelRetextured(IBakedModel baseModel, TextureAtlasSprite topNBot, float topNBotScale, TextureAtlasSprite sides, float sidesScale)
	{
		this.baseModel = baseModel;
		this.topNBot = topNBot;
		this.topNBotScale = topNBotScale;
		this.sides = sides;
		this.sidesScale = sidesScale;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
	{
		List<BakedQuad> quads = new ArrayList<>(baseModel.getQuads(state, side, rand));
		
		for (int i = 0; i < quads.size(); i++)
		{
			quads.set(i, retexture(quads.get(i), topNBot, topNBotScale, sides, sidesScale));
		}
		
		return quads;
	}
	
	public static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite topNBot, float topNBotScale, TextureAtlasSprite sides, float sidesScale)
	{
		EnumFacing side = quad.getFace();
		boolean topOrBot = side == null || side.getAxis() == EnumFacing.Axis.Y;
		
		return new BakedQuadScaledRetextured(quad, topOrBot ? topNBot : sides, topOrBot ? topNBotScale : sidesScale);
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return baseModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return baseModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return baseModel.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return baseModel.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return baseModel.getOverrides();
	}
	
}
