package com.mira.blockengine.blocks.breaking;

import com.mira.blockengine.blocks.core.BlockData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockBreakData {
    private Block block;
    private BlockData blockData;

    public int timeLeft;
    private int maxTime;

    public BlockBreakData(Block block, BlockData blockData, Player player) {
        this.block = block;
        this.blockData = blockData;

        this.timeLeft = calculateBreakingTime(blockData, player);
        this.maxTime = timeLeft;
    }

    public Block getBlock() {
        return block;
    }

    public BlockData getBlockData() {
        return blockData;
    }

    public int getMaxTime() {
        return maxTime;
    }

    private int calculateBreakingTime(BlockData blockData, Player player) {
        double speedMultiplier = 1.0f;

        // Get the item in the player's main hand.
        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType().isAir()) {
            // If the player is not holding anything, check off hand.
            item = player.getInventory().getItemInOffHand();
        }

        BlockData.BreakingTool tool = blockData.getBreakingTool();

        if(tool == BlockData.BreakingTool.getTool(item.getType())) {
            // If the player is using the correct tool, apply the speed multiplier.
            BlockData.ToolType toolType = BlockData.ToolType.fromMaterial(item.getType());

            if(toolType != null) {
                speedMultiplier = toolType.getMultiplier();
            }

            // Get efficiency level.
            int efficiencyLevel = item.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DIG_SPEED);

            if(efficiencyLevel > 0) {
                speedMultiplier += Math.pow(efficiencyLevel, 2) + 1;
            }
        }

        // Check if the player is in water
        if(player.getLocation().getBlock().isLiquid()) {
            // Check if player has aqua affinity
            ItemStack helmet = player.getInventory().getHelmet();

            if(helmet == null || helmet.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.WATER_WORKER) == 0) {
                speedMultiplier /= 5;
            }
        }

        // Check if touching the ground
        if(!player.isOnGround()) {
            speedMultiplier /= 5;
        }

        double value = speedMultiplier / blockData.getHardness();

        if(tool == BlockData.BreakingTool.getTool(item.getType())) {
            BlockData.ToolType toolType = BlockData.ToolType.fromMaterial(item.getType());

            if(BlockData.ToolType.isAboveMinimumTool(blockData.getMinimumTool(), toolType)) {
                value /= 30;
            } else {
                value /= 100;
            }
        } else {
            value /= 100;
        }

        // now get final time in ticks
        return ((Number)Math.round(1 / value)).intValue();
    }
}
