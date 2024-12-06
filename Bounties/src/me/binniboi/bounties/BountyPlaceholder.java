package me.binniboi.bounties;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BountyPlaceholder extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "bounties";
    }

    @Override
    public String getAuthor() {
        return "binniboi";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer op, String params) {
        if (op == null) return null;

        Player p = op.getPlayer();

        if (params.equalsIgnoreCase("bounty")) {
            return new BountyPlayer(p.getUniqueId()).getBounty() + "";
        }

        return null;
    }
}
