package com.mira.blockengine.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Utils {
    public static boolean itemsMatch(ItemStack item1, ItemStack item2) {
        if(item1 == null || item2 == null) return false;

        if(item1.getType() != item2.getType()) return false;

        if(item1.hasItemMeta() && item2.hasItemMeta()) {
            if(item1.getItemMeta().hasDisplayName() && item2.getItemMeta().hasDisplayName()) {
                if(!item1.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName())) return false;
            }
            if(item1.getItemMeta().hasLore() && item2.getItemMeta().hasLore()) {
                if(!item1.getItemMeta().getLore().equals(item2.getItemMeta().getLore())) return false;
            }

            if(item1.getItemMeta().hasCustomModelData() && item2.getItemMeta().hasCustomModelData()) {
                if(item1.getItemMeta().getCustomModelData() != item2.getItemMeta().getCustomModelData()) return false;
            }
        }

        return true;
    }

    public static Location calculatePlacingLocation(Block clickedBlock, BlockFace clickedFace) {
        Location location = clickedBlock.getLocation();

        if(!Utils.isSolid(clickedBlock)) return location;

        switch (clickedFace) {
            case UP -> {
                location.add(0, 1, 0);
            }
            case DOWN -> {
                location.add(0, -1, 0);
            }
            case NORTH -> {
                location.add(0, 0, -1);
            }
            case SOUTH -> {
                location.add(0, 0, 1);
            }
            case WEST -> {
                location.add(-1, 0, 0);
            }
            case EAST -> {
                location.add(1, 0, 0);
            }
        }

        return location;
    }

    public static boolean isSolid(Block block) {
        if(block.getType() == Material.WATER || block.getType() == Material.LAVA) return false;

        if(block.getType().isSolid()) return true;

        else {
            if(block.getType().getBlastResistance() > 0.2) return true;

            if(block.getType().name().contains("SAPLING")) return true;

            if(block.getType().name().contains("FLOWER") || block.getType().name().contains("TULIP")) return true;

            if(block.getType().name().contains("CARPET")) return true;

            if(block.getType().name().contains("MUSHROOM") || block.getType().name().contains("FUNGUS")) return true;

            if(block.getType().name().contains("BANNER")) return true;

            switch (block.getType()) {
                case TORCH, SOUL_TORCH, LANTERN, SOUL_LANTERN, REDSTONE_WIRE, REDSTONE, REDSTONE_TORCH, REDSTONE_WALL_TORCH, NETHER_PORTAL, END_PORTAL, BEETROOTS, CARROTS, POTATOES, WHEAT, SWEET_BERRY_BUSH, SCAFFOLDING, PUMPKIN_STEM, MELON_STEM, NETHER_WART, FLOWER_POT, END_ROD, KELP, DANDELION, POPPY, BLUE_ORCHID, ALLIUM, AZURE_BLUET, OXEYE_DAISY, CORNFLOWER, LILY_OF_THE_VALLEY, WITHER_ROSE, COBWEB -> {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * It's the same case as the isSolid() method, but for interactable blocks.
     * Because for SOME REASON STAIRS ARE INTERACTABLE??? WHAT THE HELL MOJANG
     * @param block The block to check
     * @return Whether the block is interactable or not
     */
    public static boolean isInteractable(Block block) {
        if(!block.getType().isInteractable()) return false;

        else {
            if(block.getType().name().contains("STAIRS")) return false;

            if(block.getType().name().contains("TNT")) return false;

            if(block.getType().name().contains("FENCE")) return false;

            if(block.getType().name().contains("IRON")) return false; // iron door, iron trapdoor

            if(block.getType() == Material.NOTE_BLOCK) return false;

            return true;
        }
    }

    public static boolean entityObstructing(Location location) {
        // Check if there is an entity obstructing the location (but item frames get ignored)
        for(Entity entity : location.getWorld().getNearbyEntities(location.add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5)) {
            if(entity.getType().isAlive() && entity.getType() != EntityType.ITEM_FRAME) {
                return true;
            }
        }

        return false;
    }

    public static BlockFace opposite(BlockFace blockFace) {
        switch (blockFace) {
            case UP -> {
                return BlockFace.DOWN;
            }
            case DOWN -> {
                return BlockFace.UP;
            }
            case NORTH -> {
                return BlockFace.SOUTH;
            }
            case SOUTH -> {
                return BlockFace.NORTH;
            }
            case WEST -> {
                return BlockFace.EAST;
            }
            case EAST -> {
                return BlockFace.WEST;
            }
        }

        return null;
    }

    public static int transform(int value, int min, int max, int newMin, int newMax) {
        // Swap min and max if min is greater than max
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }

        // Ensure that the value is within the range [min, max]
        value = Math.min(max, Math.max(min, value));

        // Calculate the percentage
        double percentage = 1.0 - (double) (value - min) / (max - min);

        // Scale the percentage to the new range
        return (int) (percentage * (newMax - newMin) + newMin);
    }
}
