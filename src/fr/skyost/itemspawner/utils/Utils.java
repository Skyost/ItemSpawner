package fr.skyost.itemspawner.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;

public class Utils {
	
	public static final Block getTargetBlock(final LivingEntity entity, final int range) {
		final BlockIterator bit = new BlockIterator(entity, range);
		while(bit.hasNext()) {
			final Block next = bit.next();
			if(next != null && next.getType() != Material.AIR) {
				return next;
			}
		}
		return null;
	}
	
	public static final Integer intTryParse(final String input) {
		try {
			return Integer.parseInt(input);
		}
		catch(final NumberFormatException ex) {}
		return null;
	}

}
