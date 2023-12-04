package com.mira.blockengine.nms;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public interface NMS {
    void blockBreakAnimation(Player player, Block block, int animationID, int stage);

    void placeItem(Player player, ItemStack item, Block block, EquipmentSlot hand);
}
