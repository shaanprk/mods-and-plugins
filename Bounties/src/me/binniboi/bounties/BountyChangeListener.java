package me.binniboi.bounties;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class BountyChangeListener implements Listener {

    @EventHandler
    public void onChange(BountyChangeEvent e) {
        TreeMap<UUID, Double> sorted = Main.getAllBounties();

        for (int i = 0; i < 5; i++) {
            Map.Entry<UUID, Double> entry = sorted.pollFirstEntry();
            UUID id = entry.getKey();
            OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        }
    }
}
