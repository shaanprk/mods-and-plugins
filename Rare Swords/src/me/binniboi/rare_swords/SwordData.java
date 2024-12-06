package me.binniboi.rare_swords;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SwordData {

    int id;
    SwordTier tier;
    FileConfiguration fc;

    public SwordData(SwordTier tier, int id) {
        this.tier = tier;
        this.id = id;
        this.fc = FileManager.getInstance().getSwordConfig(tier, id);
    }

    public int getID() {
        return id;
    }

    public SwordTier getTier() {
        return tier;
    }

    public UUID getOwner() {
        String str = fc.getString("owner");
        if (str == null) return null;

        return UUID.fromString(str);
    }

    public void setOwner(UUID uuid) {
        if (uuid == null) {
            fc.set("owner", null);
            FileManager.getInstance().save(tier, id, fc);
            return;
        }
        fc.set("owner", uuid.toString());
        FileManager.getInstance().save(tier, id, fc);
    }

    public Location getCurrentSpawn() {
        return fc.getLocation("current-spawn");
    }

    public void setCurrentSpawn(Location loc) {
        fc.set("current-spawn", loc);
        FileManager.getInstance().save(tier, id, fc);
    }

    public void addSpawn(Location loc) {
        int count = fc.getInt("spawn-id");

        fc.set("spawns." + "spawn-" + ++count, loc);
        fc.set("spawn-id", count);
        FileManager.getInstance().save(tier, id, fc);
    }

    public Location getRandomSpawn() {
        List<Location> locs = new ArrayList<>();

        for (String str : fc.getConfigurationSection("spawns").getKeys(false)) {
            locs.add(fc.getLocation("spawns." + str));
        }

        return locs.get(new Random().nextInt(locs.size()));
    }

    public boolean isSpawnSet() {
        return fc.getConfigurationSection("spawns") != null;
    }


    private static int getSwordLoreIndex(ItemStack item) {

        List<String> lore = item.getItemMeta().getLore();

        int index = -1;

        for (String str : lore) {
            index++;
            for (SwordTier tier : SwordTier.values()) {
                str = ChatColor.stripColor(str);
                if (str.contains(tier.getDisplayName())) return index;
            }
        }
        return -1;
    }

    public static SwordData fromItem(ItemStack item) {
        if (Main.isRareSword(item)) {
            String infoLine = item.getItemMeta().getLore().get(getSwordLoreIndex(item));
            infoLine = ChatColor.stripColor(infoLine);

            int id = Integer.parseInt(infoLine.replaceAll("[^0-9]", ""));

            infoLine = infoLine.replace(" Sword", "");

            String[] split = infoLine.split(" #");

            String tier = split[0];
            tier = tier.replace(" ", "_");
            tier = tier.toUpperCase();

            SwordTier t = SwordTier.fromString(tier);

            return new SwordData(t, id);
        }
        return null;
    }

    public List<String> getInfo() {
        List<String> list = fc.getStringList("sword-info");
        List<String> arrayList = new ArrayList<>();

        if (list.isEmpty()) {
            arrayList.add(ChatColor.WHITE + "No information available");
            return arrayList;
        }

        String string = "";

        for (String str : list) {
            if (getOwner() == null) {
                str = str.replace("%owner%", "Unclaimed");
            } else {
                str = str.replace("%owner%", Bukkit.getOfflinePlayer(getOwner()).getName());
            }

            str = ChatColor.translateAlternateColorCodes('&', str);
            arrayList.add(str);

        }

        return arrayList;
    }

    public String infoToString() {
        String str = "";

        for (String s : getInfo()) {
            if (str.equalsIgnoreCase("")) {
                str += s + "";
            }  else {
                str += "\n" + s;
            }
        }

        return str;
    }

}
