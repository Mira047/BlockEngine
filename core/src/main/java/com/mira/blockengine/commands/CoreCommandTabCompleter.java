package com.mira.blockengine.commands;

import com.mira.blockengine.blocks.BlockManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CoreCommandTabCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> autoCompletion = new ArrayList<>();

            autoCompletion.add("reload");
            autoCompletion.add("give");
            autoCompletion.add("get");
            autoCompletion.add("reload");

            return autoCompletion;
        }


        if(args[0].equals("give")) {
            List<String> autoCompletion = new ArrayList<>();

            if (args.length == 2) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    autoCompletion.add(p.getName());
                }

                return autoCompletion;
            }
            else if(args.length == 3) {
                autoCompletion = BlockManager.getInstance().getIds();

                return autoCompletion;
            }
            else if(args.length == 4) {
                autoCompletion.add("1");

                return autoCompletion;
            }

            return autoCompletion;
        }
        if(args[0].equals("get")) {
            List<String> autoCompletion = new ArrayList<>();

            if(args.length == 2) {
                autoCompletion = BlockManager.getInstance().getIds();

                return autoCompletion;
            }
            else if(args.length == 3) {
                autoCompletion.add("1");

                return autoCompletion;
            }

            return autoCompletion;
        }

        if(args[0].equals("reload")) {
            List<String> autoCompletion = new ArrayList<>();

            autoCompletion.add("blocks");
            autoCompletion.add("config");
            autoCompletion.add("all");

            return autoCompletion;
        }

        return null;
    }
}