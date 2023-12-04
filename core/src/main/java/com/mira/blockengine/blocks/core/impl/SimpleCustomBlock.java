package com.mira.blockengine.blocks.core.impl;

import com.mira.blockengine.BlockEngine;
import com.mira.blockengine.blocks.core.BlockData;
import com.mira.blockengine.blocks.core.CustomBlock;
import com.mira.blockengine.functions.FunctionManager;
import com.mira.blockengine.functions.FunctionType;
import com.mira.blockengine.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mira.blockengine.utils.FormatUtils.format;

public class SimpleCustomBlock implements CustomBlock {
    private final BlockEngine plugin = BlockEngine.getInstance();

    private final String id;

    /* In Inventory */
    private final String displayName;
    private final List<String> lore;

    private final Material material;
    private final int modelData;

    private ItemStack generatedItem;
    private ItemStack generatedDropItem;

    /* In World */
    private BlockData blockData;

    private String placeSound;
    private String breakSound;

    private final HashMap<FunctionType, List<HashMap<String, Object>>> functions = new HashMap<>();

    public SimpleCustomBlock(String id) {
        this.id = id;

        String tempDisplayName = plugin.getConfig().getString("Blocks." + id + ".display");
        if(tempDisplayName != null) {
            displayName = format(tempDisplayName);
        } else {
            displayName = null;
        }

        List<String> tempLore = plugin.getConfig().getStringList("Blocks." + id + ".lore");

        if(!tempLore.isEmpty()) {
            lore = format(tempLore);
        } else {
            lore = null;
        }

        material = Material.valueOf(plugin.getConfig().getString("Blocks." + id + ".item"));

        modelData = plugin.getConfig().getInt("Blocks." + id + ".model_data");

        // Block Data
        Instrument instrument = Instrument.valueOf(plugin.getConfig().getString("Blocks." + id + ".block_data.instrument"));
        int note = plugin.getConfig().getInt("Blocks." + id + ".block_data.note");

        BlockData.BreakingTool breakingTool;

        try {
            breakingTool = BlockData.BreakingTool.valueOf(plugin.getConfig().getString("Blocks." + id + ".block_data.breaking_tool"));
        } catch (Exception e) {
            breakingTool = BlockData.BreakingTool.NONE;
        }

        double hardness = 0;

        try {
            hardness = plugin.getConfig().getDouble("Blocks." + id + ".block_data.hardness");
        } catch (Exception e) {
            hardness = 0;
        }

        BlockData.ToolType toolType;

        try {
            toolType = BlockData.ToolType.valueOf(plugin.getConfig().getString("Blocks." + id + ".block_data.minimum_tool"));
        } catch (Exception e) {
            toolType = BlockData.ToolType.NOTHING;
        }

        blockData = new BlockData(instrument, new Note(note), breakingTool, hardness, toolType);

        // Now try to load the sounds
        try {
            placeSound = plugin.getConfig().getString("Blocks." + id + ".sounds.place");
        } catch (Exception e) {
            placeSound = null;
        }

        try {
            breakSound = plugin.getConfig().getString("Blocks." + id + ".sounds.break");
        } catch (Exception e) {
            breakSound = null;
        }

        try {
            for (FunctionType type : FunctionType.values()) {
                for(Object obj : plugin.getConfig().getList("Blocks." + id + ".functions." + type.name().toUpperCase(), new ArrayList<>())) {
                    if(obj instanceof Map<?, ?> map) {
                        // it contains a type ("type") and multiple arguments, which are stored in a map ("args")
                        String functionType = map.get("type") instanceof String string ? string : null;

                        if(functionType == null) {
                            throw new IllegalArgumentException("Function type for block " + id + " is null. This is not allowed.");
                        }

                        HashMap<String, Object> args = new HashMap<>();

                        args.put("type", functionType);

                        for(Map.Entry<?, ?> entry : map.entrySet()) {
                            if(entry.getKey().equals("type")) continue;

                            args.put(entry.getKey().toString(), entry.getValue());
                        }

                        if(!functions.containsKey(type)) {
                            ArrayList<HashMap<String, Object>> list = new ArrayList<>();

                            list.add(args);

                            functions.put(type, list);
                        } else {
                            functions.get(type).add(args);
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load functions for block " + id + ". Error: " + e.getMessage());

            throw new IllegalArgumentException("Failed to load functions for block " + id + ". Error: " + e.getMessage());
        }

        init();
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getMaterial() {
        return material;
    }

    public int getModelData() {
        return modelData;
    }

    public ItemStack getGeneratedItem() {
        return generatedItem;
    }

    public ItemStack getGeneratedDropItem() {
        return generatedDropItem;
    }

    public BlockData getBlockData() {
        return blockData;
    }

    private void init() throws IllegalArgumentException {
        // Generate itemstack
        generatedItem = new ItemStack(material);
        generatedItem.setAmount(1);

        ItemMeta meta = generatedItem.getItemMeta();

        if (meta == null) {
            throw new IllegalArgumentException("Failed to generate item for block " + id + ". ItemMeta is null. (Material: " + material + ")");
        }

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        if (lore != null) {
            meta.setLore(lore);
        }
        meta.setCustomModelData(modelData);

        generatedItem.setItemMeta(meta);

        // Until overrides are added, set the drop item to the generated item
        generatedDropItem = generatedItem.clone();
        generatedDropItem.setAmount(1);
    }

    @Override
    public boolean place(@NotNull Player player, @NotNull EquipmentSlot hand, @NotNull Location location) {
        if(Utils.isSolid(location.getBlock()) || Utils.entityObstructing(location)) {
            return false;
        }

        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(location.getBlock(), location.getBlock().getState(), location.getBlock().getRelative(BlockFace.UP), player.getInventory().getItemInMainHand(), player, true, hand);
        plugin.getServer().getPluginManager().callEvent(blockPlaceEvent);

        if(blockPlaceEvent.isCancelled()) {
            return false;
        }

        if(!location.getBlock().getType().isAir()) {
            location.getBlock().setType(Material.AIR);

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    location.getBlock().setType(Material.NOTE_BLOCK);

                    NoteBlock noteBlock = (NoteBlock) location.getBlock().getBlockData();

                    noteBlock.setInstrument(blockData.getInstrument());
                    noteBlock.setNote(blockData.getNote());

                    location.getBlock().setBlockData(noteBlock);
                }
            };

            runnable.runTaskLater(plugin, 1);
        } else {
            location.getBlock().setType(Material.NOTE_BLOCK);

            NoteBlock noteBlock = (NoteBlock) location.getBlock().getBlockData();

            noteBlock.setInstrument(blockData.getInstrument());
            noteBlock.setNote(blockData.getNote());

            location.getBlock().setBlockData(noteBlock);
        }

        if(hand == EquipmentSlot.HAND) {
            player.swingMainHand();

            if(!player.getGameMode().equals(GameMode.CREATIVE)) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            }
        } else {
            player.swingOffHand();

            if(!player.getGameMode().equals(GameMode.CREATIVE)) {
                player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount() - 1);
            }
        }

        location.getWorld().playSound(
                location,
                placeSound,
                SoundCategory.BLOCKS,
                1,
                1
        );

        callFunction(
                FunctionType.PLACE,
                location,
                player
        );

        return true;
    }

    @Override
    public String getPlaceSound() {
        return placeSound;
    }

    @Override
    public String getBreakSound() {
        return breakSound;
    }

    @Override
    public void callFunction(FunctionType type, Location clickedLocation, Player interactingPlayer) {
        if(!functions.containsKey(type)) return;

        List<HashMap<String, Object>> funList = functions.get(type);

        for(HashMap<String, Object> args : funList) {
            args.put("functionType", type);

            FunctionManager.getInstance().call(
                    args.get("type").toString(),
                    args,
                    interactingPlayer,
                    this,
                    clickedLocation
            );
        }

    }
}
