package fr.max2.dfi.registry;

import java.util.ArrayList;
import java.util.List;

import fr.max2.dfi.block.FilledGlassBlock;
import net.minecraft.block.Block;

public abstract class FillableBlockRegistry
{
	private static final List<Block> REGISTRY = new ArrayList();
	
	public static void register(Block block)
	{
		if (!(block instanceof FilledGlassBlock))
			REGISTRY.add(block);
	}
	
	public static void registerAll(Block... blocks)
	{
		for (Block block : blocks) register(block);
	}
	
	public static boolean contains(Block block)
	{
		return REGISTRY.contains(block);
	}
}
