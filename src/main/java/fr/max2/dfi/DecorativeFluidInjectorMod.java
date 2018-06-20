package fr.max2.dfi;

import fr.max2.dfi.block.FilledGlassBlock;
import fr.max2.dfi.block.FilledGlassTileEntity;
import fr.max2.dfi.item.SyringeItem;
import fr.max2.dfi.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = DecorativeFluidInjectorMod.MOD_ID, name = DecorativeFluidInjectorMod.MOD_NAME, version = DecorativeFluidInjectorMod.MOD_VERSION, acceptedMinecraftVersions = "[1.11,1.11.2]")
public class DecorativeFluidInjectorMod
{
	public static final String
		MOD_ID = "decorativefluidinjector",
		MOD_NAME = "DecorativeFluidInjector",
		MOD_VERSION = "1.0";

	public static final ModelResourceLocation SYRINGE_LOC = new ModelResourceLocation(MOD_ID + ":syringe_item", "inventory");
	public static final ModelResourceLocation SYRINGE_FULL_LOC = new ModelResourceLocation(MOD_ID + ":syringe_item_full", "inventory");
	
	@SidedProxy(clientSide = "fr.max2.dfi.proxy.ClientProxy", serverSide = "fr.max2.dfi.proxy.CommonProxy", modId = MOD_ID)
	public static CommonProxy proxy;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		GameRegistry.registerTileEntity(FilledGlassTileEntity.class, MOD_ID + ":filled_glass");
		
		proxy.preInit();
		
		GameRegistry.addRecipe(new ItemStack(Registry.SYRINGE_ITEM),
			"  I",
			"IG ",
			" I ",
			'I', Items.IRON_INGOT,
			'G', Blocks.GLASS_PANE);
		
		GameRegistry.addShapelessRecipe(new ItemStack(Registry.SYRINGE_ITEM), Registry.SYRINGE_ITEM);
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		ModConfig.init();
	}
	
	@EventBusSubscriber(modid = MOD_ID)
	public static class Registry
	{
		public static final Block FILLED_GLASS = new FilledGlassBlock().setRegistryName(new ResourceLocation(MOD_ID, "filled_glass")).setUnlocalizedName("filled_glass").setHardness(0.3F);
		
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event)
		{
			event.getRegistry().register(FILLED_GLASS);
		}
		
		public static final Item SYRINGE_ITEM = new SyringeItem().setRegistryName(new ResourceLocation(MOD_ID, "syringe_item")).setUnlocalizedName("syringe_item").setCreativeTab(CreativeTabs.MISC);
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event)
		{
			event.getRegistry().register(SYRINGE_ITEM);
		}
	}
	
}
