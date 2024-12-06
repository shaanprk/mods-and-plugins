package me.binniboi.rare_swords;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin implements Listener {

    public static int SUPREME_GRADE, GREAT_GRADE, SKILLFUL_GRADE, GRADE;
    public static List<ItemStack> drops = new ArrayList<>();

    @Override
    public void onEnable() {
        getCommand("swords").setExecutor(new SwordsCommand());
        getServer().getPluginManager().registerEvents(this, this);
        SUPREME_GRADE = getConfig().getInt("supreme-grade-count");
        GREAT_GRADE = getConfig().getInt("great-grade-count");
        SKILLFUL_GRADE = getConfig().getInt("skillful-grade-count");
        GRADE = getConfig().getInt("grade-count");

        saveDefaultConfig();

        FileManager.getInstance().setup();
    }

    @Override
    public void onDisable() {
        getConfig().set("supreme-grade-count", SUPREME_GRADE);
        getConfig().set("great-grade-count", GREAT_GRADE);
        getConfig().set("skillful-grade-count", SKILLFUL_GRADE);
        getConfig().set("grade-count", GRADE);
        saveConfig();

    }

    public static boolean isRareSword(ItemStack item) {
        if (item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.DIAMOND_HOE) {
            if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                List<String> lore = item.getItemMeta().getLore();

                for (String str : lore) {
                    for (SwordTier tier : SwordTier.values()) {
                        str = ChatColor.stripColor(str);
                        if (str.contains(tier.getDisplayName())) return true;
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        ItemStack item = e.getItem();

        if (isRareSword(item)) {
            e.setCancelled(true);
            e.getEnchanter().sendMessage(ChatColor.RED + "You cannot add enchantment to the rare sword!");
        }
    }

    @EventHandler
    public void onDie(PlayerDeathEvent e) {
        for (ItemStack stack : e.getDrops()) {
            boolean flag = false;
            if (isRareSword(stack)) {
                flag = true;
                SwordData data = SwordData.fromItem(stack);
                data.setOwner(null);
            }
            if (flag) {
                Location loc = e.getEntity().getLocation();
                e.getEntity().sendMessage(ChatColor.RED + "You are no longer the owner of your rare swords!");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Entity en : loc.getWorld().getNearbyEntities(loc, 4, 4, 4)) {
                            if (en instanceof Item) {
                                Item it = (Item) en;
                                ItemStack sword = it.getItemStack();
                                if (isRareSword(it.getItemStack())) {
                                    SwordData data = SwordData.fromItem(sword);
                                    data.setOwner(null);
                                    it.remove();

                                    Location spawn = data.getRandomSpawn();
                                    data.setCurrentSpawn(spawn);
                                    spawn.getBlock().setType(Material.CHEST);

                                    Chest c = (Chest) spawn.getBlock().getState();
                                    c.getInventory().setItem(new Random().nextInt(c.getInventory().getSize()), sword);
                                }
                            }
                        }
                    }
                }.runTaskLater(this, 20*30);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (e.getMessage().startsWith("custom_rare_swords_info")) {
            e.setCancelled(true);
            String[] split = e.getMessage().split(" ");
            SwordTier tier = SwordTier.fromString(split[1]);
            int id = Integer.parseInt(split[2]);

            SwordData data = new SwordData(tier, id);
            String owner = data.getOwner() == null ? "Unclaimed" : Bukkit.getOfflinePlayer(data.getOwner()).getName();

            p.sendMessage(ChatColor.YELLOW + "Sword Owner: " + ChatColor.AQUA + owner);
//            for (String str : data.getInfo()) {
//                p.sendMessage(str);
//            }

        }
    }

    @EventHandler
    public void onPickUp(InventoryPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        if (isRareSword(item)) {
            if (e.getInventory().getType() == InventoryType.HOPPER) e.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onSpawn(ItemSpawnEvent e) {
//        Item i = e.getEntity();
//        ItemStack item = e.getEntity().getItemStack();
//        if (isRareSword(item)) {
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    for (Entity en : e.getLocation().getWorld().getNearbyEntities(e.getLocation(), 1, 1, 1)) {
//                        if (en instanceof Item) {
//                            Item it = (Item) en;
//                            getServer().broadcastMessage(it.toString());
//                        }
//                    }
//                }
//            }.runTaskLaterAsynchronously(this, 80);
//        }
//    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDrop(PlayerDropItemEvent e) {
        if (e.isCancelled()) return;
        ItemStack item = e.getItemDrop().getItemStack();
        Player p = e.getPlayer();
        Location loc = p.getLocation();

        if (isRareSword(item)) {
            SwordData data = SwordData.fromItem(item);
            data.setOwner(null);
            p.sendMessage(ChatColor.RED + "You dropped the rare sword, you are no longer its owner!");

            new BukkitRunnable() {
                @Override
                public void run() { for (Entity en : loc.getWorld().getNearbyEntities(loc, 4, 4, 4)) {
                        if (en instanceof Item) {
                            Item it = (Item) en;
                            ItemStack sword = it.getItemStack();
                            if (isRareSword(it.getItemStack())) {
                                SwordData data = SwordData.fromItem(sword);
                                data.setOwner(null);
                                it.remove();

                                Location spawn = data.getRandomSpawn();
                                data.setCurrentSpawn(spawn);
                                spawn.getBlock().setType(Material.CHEST);

                                Chest c = (Chest) spawn.getBlock().getState();
                                c.getInventory().setItem(new Random().nextInt(c.getInventory().getSize()), sword);
                            }
                        }
                    }
                }
            }.runTaskLater(this, 20*30);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(EntityPickupItemEvent e) {
        if (e.isCancelled()) return;

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            ItemStack item = e.getItem().getItemStack();

            if (isRareSword(item)) {
                SwordData data = SwordData.fromItem(item) ;
//                if (data.getOwner() == null) {
                    data.setOwner(p.getUniqueId());
                    p.sendMessage(ChatColor.GREEN + "You are now the owner of this rare sword!");
//                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof ItemFrame) {
            if (isRareSword(e.getPlayer().getInventory().getItem(e.getHand()))) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChestMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();

            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getType() == Material.DIAMOND_SWORD || e.getCurrentItem().getType() == Material.DIAMOND_HOE) {
                    if (isRareSword(e.getCurrentItem())) {

                        if (p.getOpenInventory().getTopInventory().getType() == InventoryType.CHEST
                                || p.getOpenInventory().getTopInventory().getType() == InventoryType.ENDER_CHEST
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.FURNACE
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.BLAST_FURNACE
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.ENCHANTING
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.SHULKER_BOX
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.BARREL
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.SMITHING
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.GRINDSTONE
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.DISPENSER
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.DROPPER
                        || p.getOpenInventory().getTopInventory().getType() == InventoryType.ANVIL
                                || p.getOpenInventory().getTopInventory().getType() == InventoryType.SMOKER) {

                            if (SwordData.fromItem(e.getCurrentItem()).getOwner() == null) {
                                e.setCancelled(true);

                                if (p.getInventory().firstEmpty() == -1) {
                                    p.sendMessage(ChatColor.RED + "You cannot own this sword because your inventory is empty.");
                                    return;
                                }

                                SwordData data = SwordData.fromItem(e.getCurrentItem());

                                p.getInventory().addItem(e.getCurrentItem());
                                e.getCurrentItem().setType(Material.AIR);
                                data.getCurrentSpawn().getBlock().setType(Material.AIR);
                                data.setOwner(p.getUniqueId());

                                p.sendMessage(ChatColor.GREEN + "You now own this rare sword!");
                                return;
                            }

                            if (SwordData.fromItem(e.getCurrentItem()).getOwner().equals(p.getUniqueId())) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(PlayerItemDamageEvent e) {
        if (isRareSword(e.getItem())) {
            e.setDamage(0);
        }
    }
}
