package me.binniboi.bounties;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class BountyPlayer {

    OfflinePlayer op;
    PlayerData data;

    public BountyPlayer(UUID id) {
        this.op = Bukkit.getOfflinePlayer(id);
        this.data = new PlayerData(id);
    }

    public OfflinePlayer getOfflinePlayer() {
        return op;
    }

    public PlayerData getPlayerData() {
        return data;
    }

    public double getBounty() {
        return data.getData().getDouble("bounty");
    }

    public void setBounty(double bounty) {
        FileConfiguration fc = data.getData();

        BountyChangeEvent event = new BountyChangeEvent(op.getPlayer(), bounty);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            fc.set("bounty", bounty);
            data.saveData();
        }
    }

    public boolean removeBounty(double toWithdraw) {
        if (getBounty() < toWithdraw) return false;

        setBounty(getBounty() - toWithdraw);
        return true;
    }

    public void addBounty(double toAdd) {
        setBounty(getBounty() + toAdd);
    }

}
