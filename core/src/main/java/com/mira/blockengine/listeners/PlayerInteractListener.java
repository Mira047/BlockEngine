package com.mira.blockengine.listeners;

import com.mira.blockengine.BlockEngine;
import com.mira.blockengine.blocks.BlockManager;
import com.mira.blockengine.blocks.core.CustomBlock;
import com.mira.blockengine.functions.FunctionType;
import com.mira.blockengine.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Ignore on offhand
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = event.getPlayer();


        // 1. Placing
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!player.isSneaking()) {
                if(event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
                    CustomBlock customBlock = BlockManager.getInstance().getByBlockData((NoteBlock) event.getClickedBlock().getBlockData());

                    if (customBlock != null) {
                        customBlock.callFunction(
                                FunctionType.RIGHT_CLICK,
                                event.getClickedBlock().getLocation(),
                                player
                        );
                    }

                    if (Utils.isInteractable(event.getClickedBlock())) return;
                }
            }

            else {
                if(event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
                    CustomBlock customBlock = BlockManager.getInstance().getByBlockData((NoteBlock) event.getClickedBlock().getBlockData());

                    if (customBlock != null) {
                        customBlock.callFunction(
                                FunctionType.SHIFT_RIGHT_CLICK,
                                event.getClickedBlock().getLocation(),
                                player
                        );
                    }
                }
            }

            EquipmentSlot hand = EquipmentSlot.HAND;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                item = player.getInventory().getItemInOffHand();
                hand = EquipmentSlot.OFF_HAND;
            }

            if(event.getClickedBlock().getType() == Material.NOTE_BLOCK && !player.isSneaking()) {
                event.setCancelled(true);

                Material material = item.getType();

                if(material.isBlock()) {
                    if(material != Material.NOTE_BLOCK && material != Material.AIR) {
                        Block block = event.getClickedBlock().getRelative(event.getBlockFace());

                        BlockEngine.getInstance().getNMS().placeItem(player, item, block, hand);
                    }
                }
            }

            if (item.getType() == Material.AIR) return;

            if (player.getGameMode() == GameMode.ADVENTURE) return;

            if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                for (CustomBlock customBlock : BlockManager.getInstance().getCustomBlocks()) {
                    if (Utils.itemsMatch(item, customBlock.getGeneratedItem())) {
                        customBlock.place(player, hand, Utils.calculatePlacingLocation(event.getClickedBlock(), event.getBlockFace()));
                        return;
                    }
                }
            }
        }

        // 2. Interacting
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(event.getClickedBlock().getType() != Material.NOTE_BLOCK) return;

            CustomBlock customBlock = BlockManager.getInstance().getByBlockData((NoteBlock)event.getClickedBlock().getBlockData());

            if (customBlock != null) {
                if(player.isSneaking()) {
                    customBlock.callFunction(
                            FunctionType.SHIFT_LEFT_CLICK,
                            event.getClickedBlock().getLocation(),
                            player
                    );
                } else {
                    customBlock.callFunction(
                            FunctionType.LEFT_CLICK,
                            event.getClickedBlock().getLocation(),
                            player
                    );
                }
            }
        }
    }
}
