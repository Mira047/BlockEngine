package com.mira.blockengine;

import com.mira.blockengine.blocks.BlockManager;
import com.mira.blockengine.commands.CoreCommand;
import com.mira.blockengine.commands.CoreCommandTabCompleter;
import com.mira.blockengine.functions.FunctionManager;
import com.mira.blockengine.functions.FunctionType;
import com.mira.blockengine.listeners.BlockListener;
import com.mira.blockengine.listeners.PlayerInteractListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import com.mira.blockengine.nms.NMS;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class BlockEngine extends JavaPlugin {
    private static BlockEngine instance;

    private NMS nms;

    public static BlockEngine getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if(loadNMS()) {
            // Check if protocol lib is installed
            if(getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
                getLogger().severe("ProtocolLib is not installed! BlockEngine requires ProtocolLib to function.");
                getServer().getPluginManager().disablePlugin(this);

                return;
            }

            instance = this;

            getLogger().info("BlockEngine has been enabled!");

            loadConfig();

            BlockManager.getInstance();
            FunctionManager.getInstance();

            getCommand("blockengine").setExecutor(new CoreCommand());
            getCommand("blockengine").setTabCompleter(new CoreCommandTabCompleter());

            getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
            getServer().getPluginManager().registerEvents(new BlockListener(), this);
        } else {
            getLogger().severe("BlockEngine failed to load! Your server version is not supported.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public NMS getNMS() {
        return nms;
    }

    private boolean loadNMS() {
        String version = getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            Class<?> clazz = Class.forName("com.mira.blockengine.nms.NMS_" + version);
            if (NMS.class.isAssignableFrom(clazz)) {
                nms = (NMS) clazz.getConstructor().newInstance();
            }
        } catch (Exception e) {
            return false;
        }

        return nms != null;
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
