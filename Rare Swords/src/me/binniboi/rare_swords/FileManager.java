package me.binniboi.rare_swords;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private Plugin p;

    private static FileManager instance = new FileManager();

    public static FileManager getInstance() {
        return instance;
    }

    public void setup() {
        this.p = Bukkit.getPluginManager().getPlugin("RareSwords");
    }

    public FileConfiguration getSwordConfig(SwordTier tier, int id) {
        File dir = new File(p.getDataFolder() + File.separator + ChatColor.stripColor(tier.getDisplayName()));

        if (!dir.exists()) dir.mkdirs();

        File f = new File(dir + File.separator + id + ".yml");

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(f);
    }

    public void save(SwordTier tier, int id, FileConfiguration fc) {
        File f = new File(p.getDataFolder() + File.separator + tier.getDisplayName() + File.separator + id + ".yml");
        try {
            fc.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
