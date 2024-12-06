package me.binniboi.rare_swords;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SwordsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("swords")) {
            // /swords create <tier>

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("catalog")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;

                        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                        BookMeta bookMeta = (BookMeta) book.getItemMeta();

                        // Supreme Grade
                        int total1 = Main.SUPREME_GRADE;
                        int pages1;

                        if (total1 % 5 == 0) {
                            pages1 = total1 / 5;
                        } else {
                            pages1 = (total1/5) + 1;
                        }

                        for (int i = 1; i <= pages1; i++) {
                            ComponentBuilder components = new ComponentBuilder(ChatColor.RED + "     SUPREME GRADE\n");
                            int startID = 5 * (i - 1) + 1;
                            // 1, 6, 11, 16
                            for (int j = startID; j <= (startID+4); j++) {
                                SwordData data = new SwordData(SwordTier.SUPREME_GRADE, j);
                                if (j > Main.SUPREME_GRADE) break;
                                components = components.append(new ComponentBuilder("\nSWORD ID #" + j + "\n").
                                        event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "custom_rare_swords_info " + SwordTier.SUPREME_GRADE.getID() + " " + j)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(data.infoToString()).create())).create());
                            }

                            bookMeta.spigot().addPage(components.create());
                        }

                        // Great Grade
                        int total2 = Main.GREAT_GRADE;
                        int pages2;

                        if (total2 % 5 == 0) {
                            pages2 = total2 / 5;
                        } else {
                            pages2 = (total2/5) + 1;
                        }

                        for (int i = 1; i <= pages2; i++) {
                            ComponentBuilder components = new ComponentBuilder(ChatColor.RED + "      GREAT GRADE\n");
                            int startID = 5 * (i - 1) + 1;
                            // 1, 6, 11, 16
                            for (int j = startID; j <= (startID+4); j++) {
                                SwordData data = new SwordData(SwordTier.GREAT_GRADE, j);
                                if (j > Main.GREAT_GRADE) break;
                                components = components.append(new ComponentBuilder("\nSWORD ID #" + j + "\n").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "custom_rare_swords_info " + SwordTier.GREAT_GRADE.getID() + " " + j)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(data.infoToString())
                                        .create())).create());

                            }

                            bookMeta.spigot().addPage(components.create());
                        }

                        bookMeta.setTitle("Rare Swords Catalog");
                        bookMeta.setAuthor("Server Owner");
                        book.setItemMeta(bookMeta);
                        p.getInventory().addItem(book);
                        p.sendMessage(ChatColor.GREEN + "You have received the catalog.");
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("reset")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;

                        if (p.hasPermission("rareswords.reset")) {
                            SwordData data = SwordData.fromItem(p.getInventory().getItemInMainHand());

                            if (data == null) {
                                p.sendMessage(ChatColor.RED + "You must hold a rare sword in your hand!");
                                return true;
                            }

                            data.setOwner(null);
                            p.sendMessage(ChatColor.GREEN + "The rare sword has been reset!");
                            return true;

                        } else {
                            p.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                            return true;
                        }

                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("setspawn")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;

                        if (p.hasPermission("rareswords.setspawn")) {
                            SwordData data = SwordData.fromItem(p.getInventory().getItemInMainHand());

                            if (data == null) {
                                p.sendMessage(ChatColor.RED + "You must hold a rare sword in your hand!");
                                return true;
                            }

//                            if (data.isSpawnSet()) {
//                                Location oldSpawn = data.getCurrentSpawn();
//                                if (oldSpawn.getBlock().getType() == Material.CHEST) {
//                                    oldSpawn.getBlock().setType(Material.AIR);
//                                }
//                            }

                            Location loc = p.getLocation();
//                            data.setSpawn(loc);
                            data.addSpawn(loc);

                            p.sendMessage(ChatColor.GREEN + "Spawn location added!");
                            return true;

                        } else {
                            p.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                            return true;
                        }
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("spawn")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (p.hasPermission("rareswords.spawn")) {
                            ItemStack sword = p.getInventory().getItemInMainHand();
                            SwordData data = SwordData.fromItem(p.getInventory().getItemInMainHand());

                            if (data == null) {
                                p.sendMessage(ChatColor.RED + "You must hold a rare sword in your hand!");
                                return true;
                            }

                            if (!data.isSpawnSet()) {
                                p.sendMessage(ChatColor.RED + "Spawn is not set for the rare sword in your hand!");
                                return true;
                            }

                            if (data.getOwner() != null) {
                                p.sendMessage(ChatColor.RED + "You must reset the sword before you can respawn it!");
                                p.sendMessage(ChatColor.YELLOW + "/swords reset");
                                return true;
                            }

//                            Location spawn = data.getSpawn();
                            Location spawn = data.getRandomSpawn();
                            data.setCurrentSpawn(spawn);
                            spawn.getBlock().setType(Material.CHEST);

                            Chest c = (Chest) spawn.getBlock().getState();
                            c.getInventory().setItem(new Random().nextInt(c.getInventory().getSize()), sword);

                            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

                            p.sendMessage(ChatColor.GREEN + "Sword spawned!");
                            return true;

                        } else {
                            p.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                            return true;
                        }
                    }
                }
            }

            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /swords create <tier>");
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {

                if (sender instanceof Player) {
                    Player p = (Player) sender;

                    if (p.hasPermission("rareswords.create")) {

                        ItemStack hand = p.getInventory().getItemInMainHand();

                        if (hand.getType() == Material.DIAMOND_SWORD || hand.getType() == Material.DIAMOND_HOE) {
                            if (Main.isRareSword(hand)) {
                                p.sendMessage(ChatColor.RED + "The sword in your main hand is already a rare sword!");
                                return true;
                            }

                            String tierArg = args[1];
                            SwordTier tier = SwordTier.fromString(tierArg);

                            if (tier == null) {
                                p.sendMessage(new String[] {ChatColor.RED + "Invalid tier.",
                                ChatColor.YELLOW + "Available tiers: supreme_grade, great_grade, skillful_grade and grade"});
                                return true;
                            }

                            int id;

                            switch(tier) {
                                case SUPREME_GRADE:
                                    id = ++Main.SUPREME_GRADE;
                                    break;

                                case GREAT_GRADE:
                                    id = ++Main.GREAT_GRADE;
                                    break;

                                case SKILLFUL_GRADE:
                                    id = ++Main.SKILLFUL_GRADE;
                                    break;

                                case GRADE:
                                    id = ++Main.GRADE;
                                    break;

                                default:
                                    id = -1;
                            }

                            SwordData data = new SwordData(tier, id);
                            ItemMeta meta = hand.getItemMeta();

                            List<String> lore;

                            if (meta.hasLore()) {
                                lore = meta.getLore();
                            } else {
                                lore = new ArrayList<>();
                            }

                            lore.add(tier.getDisplayName() + " Sword #" + id);
                            meta.setLore(lore);
                            hand.setItemMeta(meta);

                            p.sendMessage(ChatColor.GREEN + "Rare Sword created successfully!");
                            return true;
                        } else {
                            p.sendMessage(ChatColor.RED + "Please hold a diamond sword in your primary hand!");
                            return true;
                        }

                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                        return true;
                    }
                }

            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /swords create <tier>");
                return true;
            }

        }
        return true;
    }

}
