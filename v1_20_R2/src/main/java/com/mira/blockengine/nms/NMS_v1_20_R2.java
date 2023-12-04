package com.mira.blockengine.nms;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class NMS_v1_20_R2 implements NMS {
    @Override
    public void blockBreakAnimation(Player player, Block block, int animationID, int stage) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ServerGamePacketListenerImpl serverGamePacketListener = serverPlayer.connection;
        BlockPos blockPosition = new BlockPos(block.getX(), block.getY(), block.getZ());

        serverGamePacketListener.send(new ClientboundBlockDestructionPacket(animationID, blockPosition, stage));

    }

    @Override
    public void placeItem(Player player, ItemStack item, Block block, EquipmentSlot hand) {
        ServerPlayer human = ((CraftPlayer) player).getHandle();

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        BlockPos blockPosition = new BlockPos(block.getX(), block.getY(), block.getZ());

        InteractionHand enumHand = hand == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        Location eyeLocation = player.getEyeLocation();

        BlockHitResult blockHitResult = new BlockHitResult(
                new net.minecraft.world.phys.Vec3(eyeLocation.getX(), eyeLocation.getY(), eyeLocation.getZ()),
                human.getDirection(),
                blockPosition,
                false
        );

        nmsItem.useOn(
                new UseOnContext(
                        human.level(),
                        human,
                        enumHand,
                        nmsItem,
                        blockHitResult
                )
        );

        // update item
        player.getInventory().setItemInMainHand(CraftItemStack.asBukkitCopy(nmsItem));

        // Get sound effect of the used item
        BlockState blockState = human.level().getBlockState(blockPosition);

        net.minecraft.world.level.block.Block nmsBlock = blockState.getBlock();

        SoundEvent soundEffect = nmsBlock.getSoundType(blockState).getPlaceSound();

        human.level().playSound(null, blockPosition, soundEffect, net.minecraft.sounds.SoundSource.BLOCKS, nmsBlock.getSoundType(blockState).getVolume() * 0.5F, nmsBlock.getSoundType(blockState).getPitch() * 0.75F);
    }
}
