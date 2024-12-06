package me.binniboi.bounties;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import sun.reflect.generics.tree.Tree;

import java.io.File;
import java.util.*;

public class Main extends JavaPlugin implements Listener {

    public static Economy econ = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(this);
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Disabling Bounties v1.0 due to unavailability of Vault.");
        }

        FileManager.getInstance().setup();
        saveDefaultConfig();

        getCommand("bounty").setExecutor(new BountyCommand());
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        FileManager.getInstance().getPlayerData(p.getUniqueId());
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            Player dead = e.getEntity();
            Player killer = dead.getKiller();

            BountyPlayer bp1 = new BountyPlayer(dead.getUniqueId());
            BountyPlayer bp2 = new BountyPlayer(killer.getUniqueId());

//            double money = (1d/10d) *
            double money = ((double) getConfig().getInt("money-percent") / 100d) * bp1.getBounty();
//            double money = (1d/10d) * bp1.getBounty();

//            Main.econ.depositPlayer(killer, (1d/10d) * bp1.getBounty());
            Main.econ.depositPlayer(killer, money);


            int threshold = getConfig().getInt("bounty-threshold");

            double toRemove;

            if (bp1.getBounty() <= threshold) {
                 toRemove = bp1.getBounty();
                 bp1.removeBounty(toRemove);
                 bp2.addBounty(toRemove);
            } else {
//                toRemove = (25d/100d) * bp1.getBounty();
                  toRemove = ((double) getConfig().getInt("bounty-percent") / 100d) * bp1.getBounty();

                  bp1.removeBounty(toRemove);
                  bp2.addBounty(toRemove);
            }

//            killer.sendMessage(ChatColor.GREEN + "You have got the bounty of " + (int) toRemove + " and Ⓑ" + (int) money + " in your balance.");
            killer.sendMessage(ChatColor.GREEN + "Your bounty has increased by Ⓑ" + (int) toRemove + " and you have been awarded Ⓑ" + (int) money + " berries.");

            dead.sendMessage(ChatColor.RED + "You have lost the bounty of " + (int) toRemove + ".");

        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static TreeMap<UUID, Double> getAllBounties() {
        File dir = new File(Bukkit.getServer().getPluginManager().getPlugin("Bounties").getDataFolder() + File.separator
        + "players");

        Map<UUID, Double> map = new HashMap<>();


        for (File f : dir.listFiles()) {
            String name = f.getName().replace(".yml", "");
            UUID id = UUID.fromString(name);
            map.put(id, new BountyPlayer(id).getBounty());
        }

        BaseValueComparator c = new BaseValueComparator(map);

        TreeMap<UUID, Double> sorted = new TreeMap<>(c);
        sorted.putAll(map);

        return sorted;
    }
}

class BaseValueComparator implements Comparator<UUID> {

    Map<UUID, Double> map;

    public BaseValueComparator(Map<UUID, Double> map) {
        this.map = map;
    }

    @Override
    public int compare(UUID id1, UUID id2) {
        if (map.get(id2) > map.get(id1)) return 1;
        return -1;
    }
}
