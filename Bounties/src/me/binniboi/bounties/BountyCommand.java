package me.binniboi.bounties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class BountyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bounty")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                if (args.length == 0) {
                    DecimalFormat formatter = new DecimalFormat("#,###");
                    double bounty = new BountyPlayer(p.getUniqueId()).getBounty();

//                    p.sendMessage(ChatColor.YELLOW + op.getName() + "'s bounty is:" + ChatColor.AQUA + " " +  bounty);
                    p.sendMessage(ChatColor.YELLOW + "Your bounty is:" + ChatColor.AQUA + " Ⓑ" + formatter.format((int) bounty));
                    return true;
                }

                if (args.length == 1) {

                    if (args[0].equalsIgnoreCase("list")) {

                        TreeMap<UUID, Double> sortedMap = Main.getAllBounties();
                        int index = 0;

                        int pages;
                        // = sortedMap.size() % 5;
                        if (sortedMap.size() % 5 == 0) {
                            pages = sortedMap.size() / 5;
                        } else {
                            pages = (sortedMap.size() / 5) + 1;
                        }
                        p.sendMessage(ChatColor.GRAY + "Showing page (1/" + pages + ")");

                        for (int i = 0; i < 5; i++) {
                            Map.Entry<UUID, Double> entry = sortedMap.pollFirstEntry();
                            if (entry == null) break;
                            String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                            double bounty = entry.getValue();
                            DecimalFormat formatter = new DecimalFormat("#,###");

                            if (i < 4) {
                                p.sendMessage(ChatColor.RED + "" + (i+1) + ". " +
                                        ChatColor.RED + "[Yonko] " + ChatColor.YELLOW + playerName + ChatColor.AQUA + " Ⓑ" + formatter.format((int) bounty));
                            } else {

                                p.sendMessage(ChatColor.RED + "" + (i + 1) + ". " +
                                        ChatColor.YELLOW + playerName + ChatColor.AQUA + " Ⓑ" + formatter.format((int) bounty));
                            }
                        }
                        p.sendMessage(ChatColor.LIGHT_PURPLE + "/bounty list 2" + ChatColor.GRAY + " for next page.");

                        return true;
                    }

                    OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);

                    if (!op.hasPlayedBefore()) {
                        p.sendMessage(ChatColor.RED + "Unknown player.");
                        return true;
                    }

                    BountyPlayer bp = new BountyPlayer(op.getUniqueId());
                    double bounty = bp.getBounty();
                    DecimalFormat formatter = new DecimalFormat("#,###");

//                    p.sendMessage(ChatColor.YELLOW + op.getName() + "'s bounty is:" + ChatColor.AQUA + " " +  bounty);
                    p.sendMessage(ChatColor.YELLOW + op.getName() + "'s bounty is:" + ChatColor.AQUA + " Ⓑ" + formatter.format((int) bounty));

                    return true;
                 }

                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("list")) {

                        int page;

                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ex) {
                            p.sendMessage(ChatColor.RED + "Page number must be an integer!");
                            return true;
                        }

                        TreeMap<UUID, Double> sortedMap = Main.getAllBounties();
                        int pages;
                        // = sortedMap.size() % 5;
                        if (sortedMap.size() % 5 == 0) {
                            pages = sortedMap.size() / 5;
                        } else {
                            pages = (sortedMap.size() / 5) + 1;
                        }

                        if (page > pages) {
                            p.sendMessage(ChatColor.RED + "Max pages are " + pages + "!");
                            return true;
                        }

                        int startIndex = 5 * (page - 1) - 1;
                        p.sendMessage(ChatColor.GRAY + "Showing page (" + page + "/" + pages + ")");

                        for (int i = 0; i < startIndex+5; i++) {
                            if (i >= startIndex) {
                                Map.Entry<UUID, Double> entry = sortedMap.pollFirstEntry();
                                if (entry == null) break;
                                String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                                double bounty = entry.getValue();

                                DecimalFormat formatter = new DecimalFormat("#,###");

                                if (i < 4) {
                                    p.sendMessage(ChatColor.RED + "" + (i+1) + ". " +
                                            ChatColor.RED + "[Yonko] " + ChatColor.YELLOW + playerName + ChatColor.AQUA + " Ⓑ" + formatter.format((int) bounty));
                                } else {

                                    p.sendMessage(ChatColor.RED + "" + (i + 1) + ". " +
                                            ChatColor.YELLOW + playerName + ChatColor.AQUA + " Ⓑ" + formatter.format((int) bounty));
                                }
                            }
                        }

                        p.sendMessage(ChatColor.LIGHT_PURPLE + "/bounty list " + (page+1) + " " + ChatColor.GRAY + " for next page.");
                        return true;
                    } else {
                        p.sendMessage(ChatColor.RED + "Invalid command.");
                        p.sendMessage(ChatColor.YELLOW + "Available commands:");
                        p.sendMessage(ChatColor.AQUA + "/bounty");
                        p.sendMessage(ChatColor.AQUA + "/bounty list <page>");
                        p.sendMessage(ChatColor.AQUA + "/bounty <player>");
                        p.sendMessage(ChatColor.AQUA + "/bounty add <player> <bounty>");
                        return true;
                    }
                }

                if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("add")) {

                        Player target = Bukkit.getPlayer(args[1]);

                        if (target == null) {
                            p.sendMessage(ChatColor.RED + "Specified player is not online!");
                            return true;
                        }

                        if (target.getUniqueId().equals(p.getUniqueId())) {
                            p.sendMessage(ChatColor.RED + "You cannot add bounty on yourself!");
                            return true;
                        }

                        double toAdd;

                        try {
                            toAdd = Double.parseDouble(args[2]);
                        } catch (NumberFormatException ex) {
                            p.sendMessage(ChatColor.RED + "The bounty must be a number!");
                            return true;
                        }

                        if (Main.econ.getBalance(p) < toAdd) {
                            p.sendMessage(ChatColor.RED + "You must have Ⓑ" + toAdd + " in your balance to add this much bounty!");
                            return true;
                        }

                        if (Main.econ.withdrawPlayer(p, toAdd).transactionSuccess()) {

                            BountyPlayer bp = new BountyPlayer(target.getUniqueId());
                            bp.addBounty(toAdd);

                            p.sendMessage(ChatColor.GREEN + "Bounty added!");
                            target.sendMessage(ChatColor.GREEN + "You have got the bounty of " + (int) toAdd + ".");
                        }

                    } else {
                        // TODO: Send list of all available commands...
                        p.sendMessage(ChatColor.RED + "Invalid command.");
                        p.sendMessage(ChatColor.YELLOW + "Available commands:");
                        p.sendMessage(ChatColor.AQUA + "/bounty");
                        p.sendMessage(ChatColor.AQUA + "/bounty list <page>");
                        p.sendMessage(ChatColor.AQUA + "/bounty <player>");
                        p.sendMessage(ChatColor.AQUA + "/bounty add <player> <bounty>");
                        return true;
                    }
                }

            }
        }
        return true;
    }

}
