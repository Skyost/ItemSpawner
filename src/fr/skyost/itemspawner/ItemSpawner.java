package fr.skyost.itemspawner;

import net.minecraft.server.v1_7_R3.ItemStack;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.TileEntity;
import net.minecraft.server.v1_7_R3.TileEntityMobSpawner;
import net.minecraft.server.v1_7_R3.World;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemSpawner extends JavaPlugin {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("itemspawner")) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("You need to be a player to use this command!");
                return true;
            }
            if (!sender.hasPermission("itemspawner.use")) {
                sender.sendMessage(ChatColor.RED + "You're not allowed to use this command!");
                return true;
            }
            if (args.length > 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /is <delay>");
                return true;
            }
            int delay = -1;
            if (args.length == 1)
                try {
                    delay = Integer.parseInt(args[0]);
                } catch (final Exception e) {
                    sender.sendMessage(ChatColor.RED + "Usage: /is <delay>");
                    return true;
                }
            final Player player = (Player) sender;
            if (player.getItemInHand().getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED + "You need to be holding an item to use this command!");
                return true;
            }
            final Block target = player.getTargetBlock(null, 200);
            if (target == null || target.getType() != Material.MOB_SPAWNER) {
                sender.sendMessage(ChatColor.RED + "You need to be looking at a spawner to use this command!");
                return true;
            }
            final World world = ((CraftWorld) target.getWorld()).getHandle();
            final TileEntity tileEntity = world.getTileEntity(target.getX(), target.getY(), target.getZ());
            if (tileEntity instanceof TileEntityMobSpawner) {
                final TileEntityMobSpawner mobSpawner = (TileEntityMobSpawner) tileEntity;
                final NBTTagCompound spawnerTag = new NBTTagCompound();
                mobSpawner.b(spawnerTag);
                if (delay == -1) {
                    spawnerTag.remove("SpawnPotentials");
                    spawnerTag.setString("EntityId", "Item");
                    final NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setShort("Health", (short) 5);
                    itemTag.setShort("Age", (short) 0);
                    final ItemStack itemStack = CraftItemStack.asNMSCopy(((CraftItemStack) player.getItemInHand()));
                    final NBTTagCompound itemStackTag = new NBTTagCompound();
                    itemStack.save(itemStackTag);
                    itemStackTag.setByte("Count", (byte) 1);
                    itemTag.set("Item", itemStackTag);
                    spawnerTag.set("SpawnData", itemTag);
                    spawnerTag.setShort("SpawnCount", (short) itemStack.count);
                    spawnerTag.setShort("SpawnRange", (short) player.getLocation().distance(target.getLocation()));
                } else {
                    spawnerTag.setShort("Delay", (short) 0);
                    spawnerTag.setShort("MinSpawnDelay", (short) (delay * 20));
                    spawnerTag.setShort("MaxSpawnDelay", (short) (delay * 20));
                    spawnerTag.setShort("MaxNearbyEntities", (short) player.getItemInHand().getAmount());
                    spawnerTag.setShort("RequiredPlayerRange", (short) player.getLocation().distance(target.getLocation()));
                }
                mobSpawner.a(spawnerTag);
                sender.sendMessage(ChatColor.GREEN + "Properties were successfully edited!");
            } else
                sender.sendMessage(ChatColor.RED + "Something went wrong!");
            return true;
        }
        return false;
    }
    
}