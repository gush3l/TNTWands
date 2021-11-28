package me.gushel.tntwands.Commands;

import de.tr7zw.nbtapi.NBTItem;
import me.gushel.tntwands.Config;
import me.gushel.tntwands.TNTWands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TNTWandCMD implements CommandExecutor {
    FileConfiguration config = TNTWands.getInstance().getConfig();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            for (String help : config.getStringList("help")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', help));
            }
            return true;
        }
        if (args.length == 1){
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("tntwands.reload")){
                Config.reload();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("reload")));
                return true;
            }
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("tntwands.give")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("giveusage")));
                return true;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("unknowncommand")));
            return true;
        }
        if (args.length == 2){
            Player target = Bukkit.getPlayerExact(args[1]);
            if (args[0].equalsIgnoreCase("give") && target != null && sender.hasPermission("tntwands.give")){
                PlayerInventory inventory = target.getInventory();
                ItemStack wand = new ItemStack(Material.matchMaterial(config.getString("wand.material")));
                ItemMeta meta = wand.getItemMeta();
                List<String> lore = config.getStringList("wand.lore");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',config.getString("wand.name")));
                for (int i = 0; i < lore.size(); i++) {
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%uses%", config.getString("infinite")));
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%owner%", target.getName()));
                }
                meta.setLore(lore);
                wand.setItemMeta(meta);
                NBTItem nbtwand = new NBTItem(wand);
                nbtwand.setString("uses","infinite");
                inventory.addItem(nbtwand.getItem());
                target.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("givetarget"))
                        .replace("%uses%",config.getString("infinite")));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("givesender"))
                        .replace("%uses%",config.getString("infinite")).replace("%target%",target.getName()));
                return true;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("unknowncommand")));
            return true;
        }
        if (args.length == 3){
            Player target = Bukkit.getPlayerExact(args[1]);
            if (args[0].equalsIgnoreCase("give") && target != null && sender.hasPermission("tntwands.give")){
                PlayerInventory inventory = target.getInventory();
                ItemStack wand = new ItemStack(Material.matchMaterial(config.getString("wand.material")));
                ItemMeta meta = wand.getItemMeta();
                List<String> lore = config.getStringList("wand.lore");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',config.getString("wand.name")));
                for (int i = 0; i < lore.size(); i++) {
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%uses%", args[2]));
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%owner%", target.getName()));
                }
                meta.setLore(lore);
                wand.setItemMeta(meta);
                NBTItem nbtwand = new NBTItem(wand);
                nbtwand.setString("uses",args[2]);
                nbtwand.setString("owner",target.getName());
                inventory.addItem(nbtwand.getItem());
                target.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("givetarget"))
                        .replace("%uses%",args[2]));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("givesender"))
                        .replace("%uses%",args[2]).replace("%target%",target.getName()));
                return true;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("unknowncommand")));
            return true;
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("unknowncommand")));
        return true;
    }
}
