package me.gushel.tntwands;

import me.gushel.tntwands.Commands.TNTWandCMD;
import me.gushel.tntwands.Events.ClickChest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TNTWands extends JavaPlugin {

    private static TNTWands instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Config.setup();
        Config.get().options().copyDefaults(true);
        Config.save();
        if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
            System.out.println("[TNTWands] The plugin couldn't find Factions! If you think that this is wrong, contact the developer.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        this.getCommand("tntwands").setExecutor(new TNTWandCMD());
        getServer().getPluginManager().registerEvents(new ClickChest(), this);
    }

    public static TNTWands getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {

    }

}
