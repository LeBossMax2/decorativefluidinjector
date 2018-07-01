package fr.max2.dfi.block;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FilledGlassBlock extends BlockGlass implements ITileEntityProvider
{
	
	public FilledGlassBlock()
	{
		super(Material.GLASS, true);
		this.setHardness(0.3F);
		this.setSoundType(SoundType.GLASS);
	}
	
	// Render logic
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
	
	// Other things
	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getItem(worldIn, pos, baseState);
		}
		
		return super.getItem(worldIn, pos, state);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getPickBlock(baseState, target, world, pos, player);
		}
		
		return super.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			
			baseState.getBlock().harvestBlock(worldIn, player, pos, baseState, te, stack);
		}
		else
		{
			player.addExhaustion(0.005F);
		}
    }
	
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			baseState.getBlock().dropBlockAsItemWithChance(worldIn, pos, baseState, chance, fortune);
		}
		
		super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getDrops(world, pos, baseState, fortune);
		}
		
		return new ArrayList<>();
	}
	
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			return ((FilledGlassTileEntity) te).getBaseBlock().getBlock().addDestroyEffects(world, pos, manager);
		}
		
		return super.addDestroyEffects(world, pos, manager);
	}
	
	@Override
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager)
	{
		TileEntity te = worldObj.getTileEntity(target.getBlockPos());
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().addHitEffects(baseState, worldObj, target, manager);
		}
		
		return super.addHitEffects(state, worldObj, target, manager);
	}
	
	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
	{
		TileEntity te = worldObj.getTileEntity(blockPosition);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().addLandingEffects(baseState, worldObj, blockPosition, iblockstate, entity, numberOfParticles);
		}
		
		return super.addLandingEffects(state, worldObj, blockPosition, iblockstate, entity, numberOfParticles);
	}
	
	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			if (baseState.getBlock().canConnectRedstone(baseState, world, pos, side)) return true;
			
			Fluid fluid = ((FilledGlassTileEntity) te).getFluidIn();
			if (fluid != null)
			{
				Block fluidBlock = fluid.getBlock();
				if (fluidBlock != null && fluidBlock.canConnectRedstone(fluidBlock.getDefaultState(), world, pos, side))
				{
					return true;
				}
			}
		}
		
		return super.canConnectRedstone(state, world, pos, side);
	}
	
	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().canCreatureSpawn(baseState, world, pos, type);
		}
		
		return super.canCreatureSpawn(state, world, pos, type);
	}
	
	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().canEntityDestroy(baseState, world, pos, entity);
		}
		
		return super.canEntityDestroy(state, world, pos, entity);
	}
	
	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().canHarvestBlock(world, pos, player);
		}
		
		return super.canHarvestBlock(world, pos, player);
	}
	
	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().canPlaceTorchOnTop(baseState, world, pos);
		}
		
		return super.canPlaceTorchOnTop(state, world, pos);
	}
	
	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().canSustainPlant(baseState, world, pos, direction, plantable);
		}
		
		return super.canSustainPlant(state, world, pos, direction, plantable);
	}
	
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.collisionRayTrace(worldIn, pos, start, end);
		}
		
		return super.collisionRayTrace(blockState, worldIn, pos, start, end);
	}
	
	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.doesSideBlockRendering(world, pos, face);
		}
		
		return super.doesSideBlockRendering(state, world, pos, face);
	}
	
	@Override
	public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getAiPathNodeType(baseState, world, pos);
		}
		
		return super.getAiPathNodeType(state, world, pos);
	}
	
	@Override
	public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getBeaconColorMultiplier(baseState, world, pos, beaconPos);
		}
		
		return super.getBeaconColorMultiplier(state, world, pos, beaconPos);
	}
	
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlockHardness(worldIn, pos);
		}
		
		return super.getBlockHardness(blockState, worldIn, pos);
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getComparatorInputOverride(worldIn, pos);
		}
		
		return super.getComparatorInputOverride(blockState, worldIn, pos);
	}
	
	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getEnchantPowerBonus(world, pos);
		}
		
		return super.getEnchantPowerBonus(world, pos);
	}
	
	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getExpDrop(baseState, world, pos, fortune);
		}
		
		return super.getExpDrop(state, world, pos, fortune);
	}
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getExplosionResistance(world, pos, exploder, explosion);
		}
		
		return super.getExplosionResistance(world, pos, exploder, explosion);
	}
	
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getFireSpreadSpeed(world, pos, face);
		}
		
		return super.getFireSpreadSpeed(world, pos, face);
	}
	
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getFlammability(world, pos, face);
		}
		
		return super.getFlammability(world, pos, face);
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			int light = ((FilledGlassTileEntity) te).getBaseBlock().getLightValue();
			
			Fluid fluid = ((FilledGlassTileEntity) te).getFluidIn();
			if (fluid != null) light = Math.max(light, fluid.getLuminosity());
			
			return light;
		}
		
		return super.getLightValue(state, world, pos);
	}
	
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			int light = ((FilledGlassTileEntity) te).getBaseBlock().getLightOpacity(world, pos);
			
			Fluid fluid = ((FilledGlassTileEntity) te).getFluidIn();
			if (fluid != null && fluid.getBlock() != null) light = Math.max(light, fluid.getBlock().getDefaultState().getLightOpacity(world, pos));
			
			return light;
		}
		
		return super.getLightOpacity(state, world, pos);
	}
	
	@Override
	public Vec3d getOffset(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getOffset(worldIn, pos);
		}
		
		return Vec3d.ZERO;
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
	{
		float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }

        if (!canHarvestBlock(player, world, pos))
        {
            return player.getDigSpeed(state, pos) / hardness / 100F;
        }
        else
        {
            return player.getDigSpeed(state, pos) / hardness / 30F;
        }
	}

    private boolean canHarvestBlock(@Nonnull EntityPlayer player, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
    	TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState state = ((FilledGlassTileEntity) te).getBaseBlock();
			
			if (state.getMaterial().isToolNotRequired())
	        {
	            return true;
	        }
			
			Block block = state.getBlock();
	        ItemStack stack = player.getHeldItemMainhand();
	        String tool = block.getHarvestTool(state);
	        if (stack.isEmpty() || tool == null)
	        {
	            return player.canHarvestBlock(state);
	        }

	        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
	        if (toolLevel < 0)
	        {
	            return player.canHarvestBlock(state);
	        }

	        return toolLevel >= block.getHarvestLevel(state);
		}
		return ForgeHooks.canHarvestBlock(this, player, world, pos);
    }
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getBlock().getSoundType(baseState, world, pos, entity);
		}
		
		return super.getSoundType(state, world, pos, entity);
	}
	
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		TileEntity te = blockAccess.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getStrongPower(blockAccess, pos, side);
		}
		
		return super.getStrongPower(blockState, blockAccess, pos, side);
	}
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		TileEntity te = blockAccess.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
			return baseState.getWeakPower(blockAccess, pos, side);
		}
		
		return super.getWeakPower(blockState, blockAccess, pos, side);
	}
	
	@Override
	public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		IBlockState state = world.getBlockState(pos.offset(facing));
		return state.getBlock() instanceof BlockPane || state.getBlock() == this;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			return ((FilledGlassTileEntity) te).getBaseBlock().getBoundingBox(world, pos);
		}
		
		return super.getBoundingBox(state, world, pos);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			return ((FilledGlassTileEntity) te).getBaseBlock().getCollisionBoundingBox(world, pos);
		}
		
		return super.getCollisionBoundingBox(state, world, pos);
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			return ((FilledGlassTileEntity) te).getBaseBlock().getSelectedBoundingBox(world, pos);
		}
		
		return super.getSelectedBoundingBox(state, world, pos);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_)
	{
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof FilledGlassTileEntity)
		{
			((FilledGlassTileEntity) te).getBaseBlock().addCollisionBoxToList(world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
		}
		else super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.removeTileEntity(pos);
	}
	
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		if (state instanceof IExtendedBlockState)
		{
			return new FilledGlassTileEntity(((IExtendedBlockState)state).getValue(BASE_BLOCK), ((IExtendedBlockState)state).getValue(FLUID_IN));
		}
		
		return super.createTileEntity(world, state);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new FilledGlassTileEntity();
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if (state instanceof IExtendedBlockState)
		{
			TileEntity te = world.getTileEntity(pos);
			
			if (te instanceof FilledGlassTileEntity)
			{
				
				IBlockState baseState = ((FilledGlassTileEntity) te).getBaseBlock();
				Fluid fluidIn = ((FilledGlassTileEntity) te).getFluidIn();
				
				int renderFlags = 0;
				
				int i = 1;
				for (EnumFacing face : EnumFacing.VALUES)
				{
					
					if (shouldBlockSideBeRendered(world, pos, face, baseState)) renderFlags |= i;

					if (shouldFluidSideBeRendered(world, pos, face, baseState, fluidIn)) renderFlags |= i << 6;
					
					i <<= 1;
				}
				
				return ((IExtendedBlockState)state).withProperty(BASE_BLOCK, baseState.getActualState(world, pos)).withProperty(FLUID_IN, fluidIn).withProperty(RENDER_FLAGS, renderFlags);
			}
		}
		
		return state;
	}
	
	private static boolean shouldBlockSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing, IBlockState baseBlock)
	{
		IBlockState sideBlock = blockAccess.getBlockState(pos.offset(facing));
		
		if (sideBlock.getBlock() instanceof FilledGlassBlock)
		{
			TileEntity sideTe = blockAccess.getTileEntity(pos.offset(facing));
			if (sideTe instanceof FilledGlassTileEntity)
			{
				sideBlock = ((FilledGlassTileEntity) sideTe).getBaseBlock();
			}
		}
		
		return sideBlock != baseBlock;
	}
	
	private static boolean shouldFluidSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing, IBlockState baseBlock, Fluid fluidIn)
	{
		if (shouldBlockSideBeRendered(blockAccess, pos, facing, baseBlock))
			return true;
		
		TileEntity sideTe = blockAccess.getTileEntity(pos.offset(facing));
		return !(sideTe instanceof FilledGlassTileEntity && ((FilledGlassTileEntity) sideTe).getFluidIn() == fluidIn);
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] { BASE_BLOCK, FLUID_IN, RENDER_FLAGS });
	}
	
	public static final IUnlistedProperty<IBlockState> BASE_BLOCK = new IUnlistedProperty<IBlockState>()
	{
		
		@Override
		public String getName()
		{
			return "base_block";
		}
		
		@Override
		public boolean isValid(IBlockState value)
		{
			return true;
		}
		
		@Override
		public Class<IBlockState> getType()
		{
			return IBlockState.class;
		}
		
		@Override
		public String valueToString(IBlockState value)
		{
			return value.toString();
		}
	};
	public static final IUnlistedProperty<Fluid> FLUID_IN = new IUnlistedProperty<Fluid>()
	{
		
		@Override
		public String getName()
		{
			return "fluid_in";
		}
		
		@Override
		public boolean isValid(Fluid value)
		{
			return true;
		}
		
		@Override
		public Class<Fluid> getType()
		{
			return Fluid.class;
		}
		
		@Override
		public String valueToString(Fluid value)
		{
			return value.getName();
		}
	};
	public static final IUnlistedProperty<Integer> RENDER_FLAGS = Properties.toUnlisted(PropertyInteger.create("render_flags", 0, 4095));
	
}
