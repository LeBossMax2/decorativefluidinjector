package fr.max2.dfi.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fr.max2.dfi.ModConfig;
import fr.max2.dfi.DecorativeFluidInjectorMod.Registry;
import fr.max2.dfi.block.FilledGlassTileEntity;
import fr.max2.dfi.registry.FillableBlockRegistry;
import fr.max2.dfi.registry.IFillableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SyringeItem extends Item
{
	
	public SyringeItem()
	{
		this.maxStackSize = 1;
		this.addPropertyOverride(new ResourceLocation("full"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return stack.hasTagCompound() && stack.getTagCompound().hasKey(FluidHandlerItemStackSimple.FLUID_NBT_KEY, Constants.NBT.TAG_COMPOUND) ? 1.0F : 0.0F;
            }
        });
	}
	
	@Override
	@Nonnull
	public String getItemStackDisplayName(@Nonnull ItemStack stack)
	{
		IFluidHandlerItem capability = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		FluidStack fluidStack = capability.drain(Fluid.BUCKET_VOLUME, false);
		
		String unloc = this.getUnlocalizedNameInefficiently(stack);
		
		if (fluidStack == null)
		{
			return I18n.translateToLocal(unloc + "_empty.name").trim();
		}
		
		if (I18n.canTranslate(unloc + "." + fluidStack.getFluid().getName()))
		{
			return I18n.translateToLocal(unloc + "." + fluidStack.getFluid().getName());
		}
		
		return I18n.translateToLocalFormatted(unloc + "_full.name", fluidStack.amount * ModConfig.usePerBucket / Fluid.BUCKET_VOLUME, ModConfig.usePerBucket, fluidStack.getLocalizedName());
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		tooltip.add(net.minecraft.client.resources.I18n.format("item.syringe_item.desc", ModConfig.usePerBucket));
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		IFluidHandlerItem capability = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		
		FluidStack fluid = capability.drain(Fluid.BUCKET_VOLUME / ModConfig.usePerBucket, false);
		if (fluid != null)
		{
			IBlockState target = world.getBlockState(pos);
			if (canFill(target, fluid))
			{
				world.setBlockState(pos, Registry.FILLED_GLASS.getDefaultState());
				TileEntity te = world.getTileEntity(pos);
				
				if (te instanceof FilledGlassTileEntity)
				{
					((FilledGlassTileEntity) te).setData(target, capability.drain(Fluid.BUCKET_VOLUME / ModConfig.usePerBucket, true).getFluid());
					world.checkLightFor(EnumSkyBlock.BLOCK, pos);
				}
				
				return EnumActionResult.SUCCESS;
			}
		}
		else
		{
			FluidActionResult filledResult = FluidUtil.tryPickUpFluid(stack, player, world, pos.offset(facing), facing);
			if (filledResult.isSuccess())
			{
				player.setHeldItem(hand, filledResult.getResult());
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}
	
	protected boolean canFill(IBlockState target, FluidStack fluid)
	{
		Block block = target.getBlock();
		
		if (block instanceof IFillableBlock)
		{
			return ((IFillableBlock) block).canBeFilled(target, fluid);
		}
		
		return FillableBlockRegistry.contains(block);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		return new FluidHandlerItemStack(stack, Fluid.BUCKET_VOLUME);
	}
}
