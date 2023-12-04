package com.mira.blockengine.blocks.core;

import com.mira.blockengine.functions.FunctionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CustomBlock {
    boolean place(@NotNull Player player, @NotNull EquipmentSlot hand, @NotNull Location loc);

    String getId();

    String getDisplayName();

    List<String> getLore();

    Material getMaterial();

    int getModelData();

    ItemStack getGeneratedItem();

     ItemStack getGeneratedDropItem();

     BlockData getBlockData();

     String getPlaceSound();

     String getBreakSound();

    void callFunction(FunctionType type, Location clickedLocation, Player interactingPlayer);
}
