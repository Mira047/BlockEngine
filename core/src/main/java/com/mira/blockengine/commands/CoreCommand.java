package com.mira.blockengine.commands;

import com.mira.blockengine.BlockEngine;
import com.mira.blockengine.blocks.BlockManager;
import com.mira.blockengine.blocks.core.CustomBlock;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CoreCommand implements CommandExecutor {
    BlockEngine plugin = BlockEngine.getInstance();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if(args.length==0) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Incorrect Command usage!");

            return true;
        }
        else {
            /* Reload */
            if(args[0].equals("reload")) {
                reload(sender, args.length>=2 ? args[1] : "");

                return true;
            }
            /* Give */
            if(args[0].equals("give")) {
                if(args.length==1) {
                    sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Incorrect Command usage!");
                    return false;
                } else {
                    if(args.length==2) {
                        sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Incorrect Command usage!");
                        return false;
                    }
                    else if(args.length==3) {
                        Player target = plugin.getServer().getPlayer(args[1]);

                        if(target==null) {
                            sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Player not found!");
                            return false;
                        }
                        else {
                            CustomBlock customBlock = BlockManager.getInstance().getCustomBlock(args[2]);

                            if(customBlock!=null) {
                                ItemStack item = customBlock.getGeneratedItem();

                                if(item!=null) {
                                    target.getInventory().addItem(item);
                                    sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.GREEN + "Given " + ChatColor.YELLOW + customBlock.getId() + ChatColor.GREEN + " to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");
                                }
                                else {
                                    sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Failed to generate item!");
                                }
                            }
                            else {
                                sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Block not found!");
                            }
                        }
                    }
                    else if(args.length==4) {
                        Player target = plugin.getServer().getPlayer(args[1]);

                        if(target==null) {
                            sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Player not found!");
                            return false;
                        }
                        else {
                            CustomBlock customBlock = BlockManager.getInstance().getCustomBlock(args[2]);

                            if(customBlock!=null) {
                                ItemStack item = customBlock.getGeneratedItem();

                                if(item!=null) {
                                    int amount = Integer.parseInt(args[3]);

                                    if(amount>0) {
                                        item.setAmount(amount);
                                        target.getInventory().addItem(item);
                                        sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.GREEN + "Given " + ChatColor.YELLOW + customBlock.getId() + ChatColor.GREEN + " to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");
                                    }
                                    else {
                                        sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Invalid amount!");
                                    }
                                }
                                else {
                                    sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Failed to generate item!");
                                }
                            }
                            else {
                                sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Block not found!");
                            }
                        }
                    }
                }

                return true;
            }
            if(args[0].equals("get")) {
                if(args.length==1) {
                    sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Incorrect Command usage!");
                    return false;
                } else {
                    if(sender instanceof Player player) {
                        if(args.length == 2) {
                            CustomBlock customBlock = BlockManager.getInstance().getCustomBlock(args[1]);

                            if(customBlock != null) {
                                ItemStack item = customBlock.getGeneratedItem();

                                if(item != null) {

                                    player.getInventory().addItem(item);
                                    sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.GREEN + "Given " + ChatColor.YELLOW + customBlock.getId() + ChatColor.GREEN + " to " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "!");
                                } else {
                                    sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Failed to generate item!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Invalid block!");
                            }
                        }
                        else if(args.length == 3) {
                            CustomBlock customBlock = BlockManager.getInstance().getCustomBlock(args[1]);

                            if(customBlock != null) {
                                ItemStack item = customBlock.getGeneratedItem();

                                if(item != null) {
                                    int amount = Integer.parseInt(args[2]);

                                    if(amount > 0) {
                                        item.setAmount(amount);
                                        player.getInventory().addItem(item);
                                        sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.GREEN + "Given " + ChatColor.YELLOW + customBlock.getId() + ChatColor.GREEN + " to " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "!");
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Invalid amount!");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Failed to generate item!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Invalid block!");
                            }
                        }
                    }
                }

                return true;
            }
        }
        return false;
    }

    public void reload(CommandSender sender, String arg) {
        // No label - Reload all
        // blocks - Reload blocks
        // config - Reload config
        // all - Reload all

        if(arg.isEmpty() || arg.equalsIgnoreCase("all")) {
            plugin.reloadConfig();
            BlockManager.getInstance().loadCustomBlocks(true);
            sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.GREEN + "Reloaded blocks + config!");
        } else if(arg.equalsIgnoreCase("blocks")) {
            BlockManager.getInstance().loadCustomBlocks(true);
            sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.GREEN + "Reloaded blocks!");
        } else if(arg.equalsIgnoreCase("config")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.GREEN + "Reloaded config!");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Block" + ChatColor.AQUA + "Engine" + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Invalid argument!");
        }
    }
}