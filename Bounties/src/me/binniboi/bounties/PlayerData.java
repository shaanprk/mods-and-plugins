package me.binniboi.bounties;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class PlayerData {

    UUID id;
    FileConfiguration fc;

    public PlayerData(UUID id) {
        this.id = id;
        this.fc = FileManager.getInstance().getPlayerData(id);
    }

    public UUID getPlayerUUID() {
        return id;
    }

    public FileConfiguration getData() {
        return fc;
    }

    public void saveData() {
        FileManager.getInstance().save(id, fc);
    }
}
