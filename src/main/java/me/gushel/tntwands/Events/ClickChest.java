package me.gushel.tntwands.Events;

import com.massivecraft.factions.*;
import de.tr7zw.nbtapi.NBTItem;
import me.gushel.tntwands.TNTWands;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClickChest implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onUse(PlayerInteractEvent event) {
        FileConfiguration config = TNTWands.getInstance().getConfig();
        Player player = event.getPlayer();
        ItemStack wand = event.getPlayer().getItemInHand();
        Block block = event.getClickedBlock();
        ItemMeta meta = wand.getItemMeta();
        List<String> lore = config.getStringList("wand.lore");
        FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
        FLocation fblocklocation = new FLocation(block.getLocation());
        Faction echunk = Board.getInstance().getFactionAt(fblocklocation);
        Faction faction = fplayer.getFaction();
        if (player.getItemInHand().hasItemMeta()
                && player.getItemInHand().getType() == Material.matchMaterial(config.getString("wand.material"))
                && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',config.getString("wand.name"))))
        {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK && block.getType().equals(Material.CHEST)) {
                if (!fplayer.hasFaction() || !echunk.getFPlayers().contains(fplayer) || !fplayer.isInOwnTerritory()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("notownteritorry")));
                    event.setCancelled(true);
                    return;
                }
                Chest chest = (Chest) block.getState();
                int tntcount = 0;
                Inventory inventory = chest.getInventory();
                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack tnt = inventory.getItem(i);
                    if (tnt != null && tnt.getType() == Material.TNT) {
                        tntcount += tnt.getAmount();
                        inventory.removeItem(tnt);
                    }
                }
                if (tntcount == 0){
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("notnt")));
                    event.setCancelled(true);
                    return;
                }
                NBTItem nbti = new NBTItem(wand);
                if (nbti.getString("uses").equalsIgnoreCase("infinite")){
                    event.setCancelled(true);
                    return;
                }
                if (!nbti.getString("uses").equalsIgnoreCase("infinite")) {
                    int usesi = Integer.parseInt(nbti.getString("uses"));
                    String usess = String.valueOf(usesi - 1);
                    if (usess.equalsIgnoreCase("0")){
                        player.setItemInHand(new ItemStack(Material.AIR));
                        return;
                    }
                    String owner = nbti.getString("owner");
                    nbti = new NBTItem(wand);
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%uses%", String.valueOf(Integer.parseInt(nbti.getString("uses"))-1)));
                        lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%owner%", owner));
                    }
                    meta.setLore(lore);
                    wand.setItemMeta(meta);
                    nbti = new NBTItem(wand);
                    nbti.setString("uses", usess);
                    player.setItemInHand(nbti.getItem());
                    if (faction.getTnt()+tntcount>faction.getTntBankLimit()){
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("toomuchtnt")));
                        return;
                    }
                    faction.addTnt(tntcount);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("addedtnt").replace("%tnt%",String.valueOf(tntcount)).replace("%faction%",faction.getTag())));
                    event.setCancelled(true);
                    return;
                }
            }
            event.setCancelled(true);
        }
    }
}
