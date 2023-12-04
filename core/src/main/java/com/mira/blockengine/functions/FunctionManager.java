package com.mira.blockengine.functions;

import com.mira.blockengine.BlockEngine;
import com.mira.blockengine.blocks.core.CustomBlock;
import com.mira.blockengine.functions.internal.CommandFunction;
import com.mira.blockengine.functions.internal.SoundFunction;
import com.mira.blockengine.functions.internal.SwingFunction;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class FunctionManager {
    private static FunctionManager instance;

    HashMap<String, Function> functions = new HashMap<>();

    private FunctionManager() {
        register(new CommandFunction());
        register(new SoundFunction());
        register(new SwingFunction());
    }

    public static FunctionManager getInstance() {
        if(instance == null) {
            instance = new FunctionManager();
        }
        return instance;
    }

    public void register(Function function) {
        functions.put(function.getType(), function);

        BlockEngine.getInstance().getLogger().info("Registered function: " + function.getType());
    }

    public void call(String type, HashMap<String, Object> args, Player player, CustomBlock block, Location interactLocation) {
        HashMap<String, Object> argsCopy = new HashMap<>(args);

        argsCopy.put("player", player);

        argsCopy.put("block", block);

        argsCopy.put("location", interactLocation);

        try {
            functions.get(type).execute(argsCopy);
        } catch (IllegalArgumentException e) {
            BlockEngine.getInstance().getLogger().warning("Failed to execute function: " + e.getMessage());
        }
    }
}