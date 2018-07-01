package fr.max2.dfi.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;

import static fr.max2.dfi.DecorativeFluidInjectorMod.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import fr.max2.dfi.block.FilledGlassBlock;

public enum FilledGlassModelLoader implements ICustomModelLoader
{
	INSTANCE;

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		return modelLocation.getResourceDomain().equals(MOD_ID) &&
			   modelLocation.getResourcePath().equals("filled_glass");
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		return new FilledGlassModel();
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{ }
	
	
	public static class FilledGlassModel implements IModel
	{
		private ABakedFilledGlassModel baseModel = new SemiBakedFilledGlassModel();
		
		@Override
		public Collection<ResourceLocation> getDependencies()
		{
			return Collections.EMPTY_SET;
		}

		@Override
		public Collection<ResourceLocation> getTextures()
		{
			return Collections.EMPTY_SET;
		}

		@Override
		public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
		{
			return baseModel;
		}

		@Override
		public IModelState getDefaultState()
		{
			return null;
		}
	}
	
	public static abstract class ABakedFilledGlassModel implements IBakedModel
	{
		protected final IBlockState baseState;
		
		private final Map<String, IBakedModel> fluidModelCache = new HashMap<String, IBakedModel>();
		
		public ABakedFilledGlassModel()
		{
			this.baseState = Blocks.GLASS.getDefaultState();
		}
		
		public ABakedFilledGlassModel(IBlockState baseState)
		{
			this.baseState = baseState;
		}
		
		protected abstract IBakedModel getBaseModel();
		
		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
		{
			return getModelFromState(state).getAllQuads(state, side, rand);
		}
		
		protected ABakedFilledGlassModel getModelFromState(IBlockState state)
		{
			if (state instanceof IExtendedBlockState)
			{
				IBlockState baseState = ((IExtendedBlockState)state).getValue(FilledGlassBlock.BASE_BLOCK);
				return new BakedFilledGlassModel(baseState);
			}
			return this;
		}
		
		protected List<BakedQuad> getAllQuads(IBlockState state, EnumFacing side, long rand)
		{
			IBakedModel baseModel = getBaseModel();
			BlockRenderLayer renderLayer = MinecraftForgeClient.getRenderLayer();
			
			boolean renderBlock = baseState.getBlock().canRenderInLayer(baseState, renderLayer);
			
			boolean render = renderLayer != null;
			
			if (render && state instanceof IExtendedBlockState)
			{
				
				List<BakedQuad> quads = new ArrayList();
				
				Fluid fluid = ((IExtendedBlockState)state).getValue(FilledGlassBlock.FLUID_IN);
				
				if (fluid != null && fluid.getBlock() != null)
				{
					BlockRenderLayer[] layers = BlockRenderLayer.values();
					
					int validLayer;
					for (validLayer = layers.length -1; validLayer > 0 && !baseState.getBlock().canRenderInLayer(baseState, layers[validLayer]) && !fluid.getBlock().canRenderInLayer(fluid.getBlock().getDefaultState(), layers[validLayer]); validLayer--)
					{ }
					
					render = layers[validLayer] == renderLayer;
				}
				else render = renderBlock;
				
				if (render)
				{
					int renderFlags = ((IExtendedBlockState)state).getValue(FilledGlassBlock.RENDER_FLAGS);
					
					if ( fluid != null && fluid.getBlock() != null && (side == null || (renderFlags & (1 << (side.getIndex() + 6))) > 0) )
					{
						IBakedModel fluidModel = fluidModelCache.get(fluid.getName());
						
						if (fluidModel == null)
						{
							Function<ResourceLocation, TextureAtlasSprite> textureGetter = ModelLoader.defaultTextureGetter();
							
							TextureAtlasSprite stillTexture = textureGetter.apply(fluid.getStill());
							TextureAtlasSprite baseFlow = textureGetter.apply(fluid.getFlowing());
							
							fluidModel = new BakedModelRetextured(baseModel, stillTexture, 1.0f, baseFlow, 0.5f);
							
							fluidModelCache.put(fluid.getName(), fluidModel);
						}
						quads.addAll(fluidModel.getQuads(baseState, side, rand));
					}

					if (side == null || (renderFlags & (1 <<  side.getIndex())) > 0) quads.addAll(baseModel.getQuads(baseState, side, rand));
				}
				
				return quads;
			}
			
			return render ? new ArrayList() : baseModel.getQuads(baseState, side, rand);
		}
		
		@Override
		public boolean isAmbientOcclusion()
		{
			return getBaseModel().isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d()
		{
			return getBaseModel().isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return getBaseModel().getParticleTexture();
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			return getBaseModel().getItemCameraTransforms();
		}

		@Override
		public ItemOverrideList getOverrides()
		{
			return ItemOverrideList.NONE;
		}
		
	}
	
	public static class SemiBakedFilledGlassModel extends ABakedFilledGlassModel
	{
		private IBakedModel baseModel;
		
		private final Map<IBlockState, BakedFilledGlassModel> bakedModelCache = new HashMap<IBlockState, BakedFilledGlassModel>();
		
		public SemiBakedFilledGlassModel()
		{
			super();
		}
		
		@Override
		protected IBakedModel getBaseModel()
		{
			if (baseModel == null) baseModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(baseState);
			return baseModel;
		}
		
		@Override
		protected ABakedFilledGlassModel getModelFromState(IBlockState state)
		{
			if (state instanceof IExtendedBlockState)
			{
				IBlockState baseState = ((IExtendedBlockState)state).getValue(FilledGlassBlock.BASE_BLOCK);
				
				BakedFilledGlassModel model = bakedModelCache.get(baseState);
				if (model == null)
				{
					model = new BakedFilledGlassModel(baseState);
					bakedModelCache.put(baseState, model);
				}
				return model;
			}
			return this;
		}
		
	}
	
	public static class BakedFilledGlassModel extends ABakedFilledGlassModel
	{
		private final IBakedModel baseModel;
		
		public BakedFilledGlassModel(IBlockState baseState)
		{
			super(baseState);
			this.baseModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(baseState);
		}
		
		@Override
		protected IBakedModel getBaseModel()
		{
			return baseModel;
		}
		
	}
	
}
