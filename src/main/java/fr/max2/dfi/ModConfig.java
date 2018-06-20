package fr.max2.dfi;

import java.io.File;

import fr.max2.dfi.registry.FillableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

@EventBusSubscriber(modid = DecorativeFluidInjectorMod.MOD_ID)
public abstract class ModConfig
{
	private static Configuration config;
	
	private static String[] baseBlockList;
	
	private static boolean useAutoDetection;
	private static String[] autoDetectionKeywords;
	private static String[] blocksBlackList;
	
	public static int usePerBucket;
	
	static
	{
		//Config init
		config = new Configuration(new File(Loader.instance().getConfigDir(), "filltheglass.cfg"));
		
		baseBlockList = config.getStringList("BaseBlockList", "BlockList", new String[] {"glass", "glass_pane", "stained_glass", "stained_glass_pane", "ice"}, "The list of blocks you can fill. ");
		
		useAutoDetection = config.getBoolean("EnableAutoDetection", "AutoDetection", true, "");
		//useForgeOreDictonary = config.getBoolean("EnableForgeOreDictionaryDetection", "AutoDetection", true, "");
		autoDetectionKeywords = config.getStringList("AutoDetectionKeywords", "AutoDetection", new String[] {"Glass", "Ice"}, "");
		blocksBlackList = config.getStringList("BlockBlockList", "AutoDetection", new String[] {}, "");
		
		usePerBucket = config.getInt("UsePerBucket", "Syringe", 4, 1, Integer.MAX_VALUE, "The number of times you can use a full syringe (must be a divisor of " + Fluid.BUCKET_VOLUME + ")");
		
		if (config.hasChanged())
		{
			config.save();
		}
	}
	
	public static void init()
	{
		//Register init from config
		
		for (String blockName : baseBlockList)
		{
			Block block = Block.getBlockFromName(blockName);
			if (block != null) FillableBlockRegistry.register(block);
		}
	}
	
	@SubscribeEvent
	public static void onOreRegistered(OreRegisterEvent event)
	{
		if (useAutoDetection)
		{
			Block block = Block.getBlockFromItem(event.getOre().getItem());
			if (block != Blocks.AIR)
			{
				for (String keyword : autoDetectionKeywords)
				{
					if (event.getName().contains(keyword))
					{
						for (String removeString : blocksBlackList)
						{
							if (event.getName().equals(removeString)) return;
						}
						FillableBlockRegistry.register(block);
					}
				}
			}
		}
	}
}
