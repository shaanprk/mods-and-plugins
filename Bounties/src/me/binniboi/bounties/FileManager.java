package me.binniboi.bounties;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileManager {

    private Plugin p;

    public void setup() {
        this.p = Bukkit.getServer().getPluginManager().getPlugin("Bounties");
    }

    private static FileManager instance = new FileManager();

    public static FileManager getInstance() {
        return instance;
    }

    public FileConfiguration getPlayerData(UUID id) {
        File dir = new File(p.getDataFolder() + File.separator + "players");

        if (!dir.exists()) dir.mkdirs();

        File f = new File(dir + File.separator + id.toString() + ".yml");

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(f);
    }

    public void save(UUID id, FileConfiguration fc) {
        try {
            fc.save(new File(p.getDataFolder() + File.separator + "players" + File.separator + id.toString() + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
