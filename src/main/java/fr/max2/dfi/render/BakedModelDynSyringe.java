package fr.max2.dfi.render;

import static fr.max2.dfi.DecorativeFluidInjectorMod.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
public class BakedModelDynSyringe implements IPerspectiveAwareModel
{
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final ResourceLocation SYRINGE_FLUID_LOC = new ResourceLocation(MOD_ID, "items/syringe_fluid");
    
	@SubscribeEvent
	public static void onModelBakeEvent(ModelBakeEvent event)
	{
		IBakedModel oldModel = event.getModelRegistry().getObject(SYRINGE_FULL_LOC);
		
		event.getModelRegistry().putObject(SYRINGE_FULL_LOC,  new BakedModelDynSyringe(oldModel, new HashMap<String, BakedModelDynSyringe>()));
	}
	
	@SubscribeEvent
	public static void onTexturesStitch(TextureStitchEvent.Pre event)
	{
		event.getMap().registerSprite(SYRINGE_FLUID_LOC);
	}
	
	private final IBakedModel baseModel;
	private final List<BakedQuad> fluidQuads;
	private final Map<String, BakedModelDynSyringe> cache;
	
	private BakedModelDynSyringe(IBakedModel baseModel,  Map<String, BakedModelDynSyringe> cache)
	{
		this.baseModel = baseModel;
		this.fluidQuads = new ArrayList();
		this.cache = cache;
	}
	
	private BakedModelDynSyringe(IBakedModel baseModel, FluidStack fluidIn,  Map<String, BakedModelDynSyringe> cache)
	{
		this.baseModel = baseModel;
		this.fluidQuads = bake(fluidIn);
		this.cache = cache;
	}
	
	private BakedModelDynSyringe(BakedModelDynSyringe baseModel, FluidStack fluidIn)
	{
		this(baseModel.baseModel, fluidIn, baseModel.cache);
	}
	
	private static List<BakedQuad> bake(FluidStack fluidIn)
	{
		Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter = ModelLoader.defaultTextureGetter();
        TextureAtlasSprite liquid = bakedTextureGetter.apply(SYRINGE_FLUID_LOC);
		TextureAtlasSprite fluidSprite = bakedTextureGetter.apply(fluidIn.getFluid().getStill(fluidIn));

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        
        VertexFormat format = DefaultVertexFormats.ITEM;
        TRSRTransformation transform = TRSRTransformation.identity();
		builder.addAll(ItemTextureQuadBuilder.convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluidIn.getFluid().getColor()));
        builder.addAll(ItemTextureQuadBuilder.convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluidIn.getFluid().getColor()));
		
		return builder.build();
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
	{
		if (side == null)
		{
			List<BakedQuad> quads = new ArrayList(baseModel.getQuads(state, null, rand));
			quads.addAll(fluidQuads);
			return quads;
		}
		
		return baseModel.getQuads(state, side, rand);
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
		return CustomItemOverrideList.INSTANCE;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
	{
		if (baseModel instanceof IPerspectiveAwareModel)
		{
			return Pair.of(this, ((IPerspectiveAwareModel) baseModel).handlePerspective(cameraTransformType).getRight());
		}
		
		return Pair.of(this, null);
	}
	
	public static class CustomItemOverrideList extends ItemOverrideList
	{
	    public static final CustomItemOverrideList INSTANCE = new CustomItemOverrideList();
		
		private CustomItemOverrideList()
		{
			super(ImmutableList.of());
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
		{
			if (!stack.isEmpty() && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				IFluidHandlerItem capability = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				FluidStack fluidIn = capability.drain(Fluid.BUCKET_VOLUME, false);
				if (fluidIn != null)
				{
					if (originalModel instanceof BakedModelDynSyringe)
					{
						String fluidName = fluidIn.getFluid().getName();
						Map<String, BakedModelDynSyringe> cache = ((BakedModelDynSyringe)originalModel).cache;
						
						if (cache != null)
						{
							if (cache.containsKey(fluidName)) return cache.get(fluidName);
							
							BakedModelDynSyringe model = new BakedModelDynSyringe((BakedModelDynSyringe)originalModel, fluidIn);
							cache.put(fluidName, model);
							return model;
						}
					}
					
					return new BakedModelDynSyringe(originalModel, fluidIn, null);
				}
			}
			return originalModel;
		}
		
	}
	
}
