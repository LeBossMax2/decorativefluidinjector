package fr.max2.dfi.proxy;

import static fr.max2.dfi.DecorativeFluidInjectorMod.*;

import fr.max2.dfi.DecorativeFluidInjectorMod.Registry;
import fr.max2.dfi.render.FilledGlassModelLoader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		ModelLoaderRegistry.registerLoader(FilledGlassModelLoader.INSTANCE);
		
		ModelLoader.registerItemVariants(Registry.SYRINGE_ITEM, SYRINGE_FULL_LOC, SYRINGE_LOC);
		ModelLoader.setCustomMeshDefinition(Registry.SYRINGE_ITEM, stack ->
		{
			if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				IFluidHandlerItem capability = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				FluidStack fluidIn = capability.drain(Fluid.BUCKET_VOLUME, false);
				if (fluidIn != null && fluidIn.amount > 0)
				{
					return SYRINGE_FULL_LOC;
				}
			}
			return SYRINGE_LOC;
		});
		
		super.preInit();
	}
}
