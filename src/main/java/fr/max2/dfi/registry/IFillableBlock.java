package fr.max2.dfi.registry;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.FluidStack;

public interface IFillableBlock
{
	boolean canBeFilled(IBlockState target, FluidStack fluid);
}
