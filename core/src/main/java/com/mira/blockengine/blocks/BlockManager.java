package com.mira.blockengine.blocks;

import com.mira.blockengine.BlockEngine;
import com.mira.blockengine.blocks.core.BlockData;
import com.mira.blockengine.blocks.core.CustomBlock;
import com.mira.blockengine.blocks.core.impl.SimpleCustomBlock;
import org.bukkit.block.data.type.NoteBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockManager {
    private static BlockManager instance;

    private static BlockEngine plugin = BlockEngine.getInstance();

    private final List<CustomBlock> customBlocks = new ArrayList<>();

    private BlockManager() {
        loadCustomBlocks(true);
    }

    public static BlockManager getInstance() {
        if(instance == null) {
            instance = new BlockManager();
        }

        return instance;
    }

    public void loadCustomBlocks(boolean log) {
        customBlocks.clear();

        for(String id : plugin.getConfig().getConfigurationSection("Blocks").getKeys(false)) {
            boolean directional = plugin.getConfig().getBoolean("Blocks." + id + ".directional");

            if(directional) {
                throw new IllegalArgumentException("Directional blocks are not supported yet!");
            } else {
                customBlocks.add(new SimpleCustomBlock(id));

                if(log) plugin.getLogger().info("Loaded block: " + id);
            }
        }
    }

    public CustomBlock getCustomBlock(String id) {
        for(CustomBlock customBlock : customBlocks) {
            if(customBlock instanceof SimpleCustomBlock) {
                if(customBlock.getId().equals(id)) {
                    return customBlock;
                }
            }
        }

        return null;
    }

    public List<CustomBlock> getCustomBlocks() {
        return customBlocks;
    }

    public List<String> getIds() {
        List<String> ids = new ArrayList<>();

        for(CustomBlock customBlock : customBlocks) {
            if(customBlock instanceof SimpleCustomBlock) {
                ids.add(customBlock.getId());
            }
        }

        return ids;
    }

    public CustomBlock getByBlockData(NoteBlock noteBlock) {
        for(CustomBlock customBlock : customBlocks) {
            BlockData blockData = customBlock.getBlockData();

            if(blockData.getInstrument() == noteBlock.getInstrument() && blockData.getNote().equals(noteBlock.getNote())) {
                return customBlock;
            }
        }

        return null;
    }
}
