package fr.max2.dfi.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FilledGlassTileEntity extends TileEntity
{
	private IBlockState baseBlock;
	private Fluid fluidIn;
	
	public FilledGlassTileEntity()
	{
		this.baseBlock = Blocks.AIR.getDefaultState();
	}
	
	public FilledGlassTileEntity(IBlockState newBaseBlock, Fluid newFluidIn)
	{
		this.baseBlock = newBaseBlock == null ? Blocks.AIR.getDefaultState() : newBaseBlock;
		this.fluidIn = newFluidIn;
	}
	
	public void setData(IBlockState newBaseBlock, Fluid newFluidIn)
	{
		this.baseBlock = newBaseBlock;
		this.fluidIn = newFluidIn;
	}
	
	public IBlockState getBaseBlock()
	{
		return baseBlock;
	}
	
	public Fluid getFluidIn()
	{
		return fluidIn;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setString("Block", baseBlock.getBlock().getRegistryName().toString());
		compound.setInteger("BlockMata", baseBlock.getBlock().getMetaFromState(baseBlock));
		
		if (fluidIn != null) compound.setString("Fluid", fluidIn.getName());
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		Block block = Block.getBlockFromName(compound.getString("Block"));
		baseBlock = block.getStateFromMeta(compound.getInteger("BlockMata"));
		

		if (compound.hasKey("Fluid", Constants.NBT.TAG_STRING))
			fluidIn = FluidRegistry.getFluid(compound.getString("Fluid"));
	}
	
	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.serializeNBT();
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		this.readFromNBT(packet.getNbtCompound());
	}
	
}
