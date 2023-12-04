package com.mira.blockengine.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.mira.blockengine.BlockEngine;
import com.mira.blockengine.blocks.BlockManager;
import com.mira.blockengine.blocks.breaking.BlockBreakData;
import com.mira.blockengine.blocks.core.BlockData;
import com.mira.blockengine.blocks.core.CustomBlock;
import com.mira.blockengine.functions.FunctionType;
import com.mira.blockengine.nms.NMS;
import com.mira.blockengine.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockListener implements Listener {
    private HashMap<Player, BlockBreakData> blockBreakMap = new HashMap<>();

    public BlockListener() {
        registerPacketListener();
    }

    @EventHandler
    public void onBlockUpdate(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Block topBlock = block.getRelative(0, 1, 0);
        Block bottomBlock = block.getRelative(0, -1, 0);

        if(topBlock.getType() == Material.NOTE_BLOCK || bottomBlock.getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNotePlay(NotePlayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        List<Block> blocks = event.getBlocks();

        for(Block block : blocks) {
            if(block.getType() == Material.NOTE_BLOCK) {
                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        List<Block> blocks = event.getBlocks();

        for(Block block : blocks) {
            if(block.getType() == Material.NOTE_BLOCK) {
                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if(block.getType() == Material.NOTE_BLOCK) {
            if(event.getPlayer().getGameMode() == org.bukkit.GameMode.CREATIVE) {
                blockBreakMap.remove(event.getPlayer());
            }

            boolean contains = blockBreakMap.containsKey(event.getPlayer());

            if(!contains && event.getPlayer().getGameMode() != org.bukkit.GameMode.CREATIVE) {
                event.setCancelled(true);

                return;
            }


            if(contains && blockBreakMap.get(event.getPlayer()).getBlock() != block) {
                event.setCancelled(true);

                return;
            }

            if(contains && blockBreakMap.get(event.getPlayer()).timeLeft > 0) {
                event.setCancelled(true);

                return;
            }

            event.setDropItems(false);
            event.setExpToDrop(0);

            NoteBlock noteBlock = (NoteBlock) block.getBlockData();

            CustomBlock customBlock = BlockManager.getInstance().getByBlockData(noteBlock);

            if (customBlock != null) {
                customBlock.callFunction(
                        FunctionType.BREAK,
                        block.getLocation(),
                        event.getPlayer()
                );

                if(event.getPlayer().getGameMode() != org.bukkit.GameMode.CREATIVE) {
                    block.getWorld().playSound(
                            block.getLocation(),
                            customBlock.getBreakSound(),
                            SoundCategory.BLOCKS,
                            1,
                            1
                    );

                    BlockEngine.getInstance().getNMS().blockBreakAnimation(event.getPlayer(), block, 0, 10);

                    // Check if current tool is above minimum tool
                    ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

                    boolean offHand = false;

                    if (item.getType().isAir()) {
                        item = event.getPlayer().getInventory().getItemInOffHand();
                        offHand = true;
                    }

                    if (!item.getType().isAir()) {
                        if(item.getItemMeta() instanceof Damageable && customBlock.getBlockData().getHardness() > 0) {
                            if (item.getEnchantments().containsKey(Enchantment.DURABILITY)) {
                                int level = item.getEnchantmentLevel(Enchantment.DURABILITY);

                                int chance = 100 / (level + 1);

                                int random = Math.round((float) Math.random() * 100);

                                if (random <= chance) {
                                    Damageable damageable = (Damageable) item.getItemMeta();

                                    damageable.setDamage(damageable.getDamage() + 1);

                                    item.setItemMeta((ItemMeta) damageable);
                                }
                            } else {
                                Damageable damageable = (Damageable) item.getItemMeta();

                                damageable.setDamage(damageable.getDamage() + 1);

                                item.setItemMeta((ItemMeta) damageable);
                            }

                            if (((Damageable) item.getItemMeta()).getDamage() >= item.getType().getMaxDurability()) {
                                if (offHand) {
                                    event.getPlayer().getInventory().setItemInOffHand(null);
                                } else {
                                    event.getPlayer().getInventory().setItemInMainHand(null);
                                }
                            }
                        }
                    }

                    BlockData data = customBlock.getBlockData();

                    BlockData.BreakingTool tool = data.getBreakingTool();

                    if (tool == BlockData.BreakingTool.getTool(item.getType())) {
                        BlockData.ToolType toolType = data.getMinimumTool();

                        if (BlockData.ToolType.isAboveMinimumTool(toolType, BlockData.ToolType.fromMaterial(item.getType()))) {
                            ItemStack drop = customBlock.getGeneratedDropItem();

                            if (drop != null) {
                                block.getWorld().dropItemNaturally(block.getLocation(), drop);
                            }
                        }
                    } else if (tool == BlockData.BreakingTool.NONE) {
                        ItemStack drop = customBlock.getGeneratedDropItem();

                        if (drop != null) {
                            block.getWorld().dropItemNaturally(block.getLocation(), drop);
                        }
                    }
                }
            }
        }
    }

    private void registerPacketListener() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        // Cancelling block break packets
        protocolManager.addPacketListener(new PacketAdapter(BlockEngine.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                BlockPosition position = packet.getBlockPositionModifier().read(0);


                Block block = player.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());

                // Check if the block was broken
                if(blockBreakMap.containsKey(player)) {
                    // Update the packet to the correct block
                    Block currentBlock = blockBreakMap.get(player).getBlock();

                    if(!currentBlock.getLocation().equals(block.getLocation())) {
                        return;
                    }

                    WrappedBlockData blockData = WrappedBlockData.createData(currentBlock.getBlockData());
                    packet.getBlockData().write(0, blockData);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(BlockEngine.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                BlockPosition position = packet.getBlockPositionModifier().read(0);

                Block block = player.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());

                String action = packet.getModifier().read(2).toString();

                if(block.getType() != Material.NOTE_BLOCK) return;

                CustomBlock customBlock = BlockManager.getInstance().getByBlockData((NoteBlock) block.getBlockData());

                if(customBlock == null) return;

                if(action.equals("ABORT_DESTROY_BLOCK")) {
                    if(!blockBreakMap.containsKey(player)) return;

                    BlockEngine.getInstance().getNMS().blockBreakAnimation(player, block, 0, 10);

                    blockBreakMap.remove(player);
                } else if(action.equals("START_DESTROY_BLOCK")) {
                    if(blockBreakMap.containsKey(player)) return;

                    BlockBreakData data = new BlockBreakData(block, customBlock.getBlockData(), player);

                    blockBreakMap.put(player, data);
                }
            }
        });

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> toRemove = new ArrayList<>();
                for(Player player : blockBreakMap.keySet()) {
                    BlockBreakData data = blockBreakMap.get(player);

                    data.timeLeft--;

                    if(data.timeLeft <= 0) {
                        toRemove.add(player);

                        BlockBreakEvent event = new BlockBreakEvent(data.getBlock(), player);

                        BlockEngine.getInstance().getServer().getPluginManager().callEvent(event);


                        if(!event.isCancelled()) {
                            data.getBlock().getWorld().spawnParticle(
                                    Particle.BLOCK_CRACK,
                                    data.getBlock().getLocation().clone().add(0.5, 0.5, 0.5),
                                    80,
                                    0.25,
                                    0.25,
                                    0.25,
                                    0.2,
                                    data.getBlock().getBlockData()
                            );

                            data.getBlock().setType(Material.AIR);
                        }

                        continue;
                    }

                    player.addPotionEffect(
                            new PotionEffect(
                                    PotionEffectType.SLOW_DIGGING,
                                    3,
                                    6,
                                    false,
                                    false
                            )
                    );
                    player.addPotionEffect(
                            new PotionEffect(
                                    PotionEffectType.FAST_DIGGING,
                                    3,
                                    0,
                                    false,
                                    false
                            )
                    );

                    NMS nms = BlockEngine.getInstance().getNMS();

                    int stage = Utils.transform(data.timeLeft, 0, data.getMaxTime(), 0, 10);

                    nms.blockBreakAnimation(player, data.getBlock(), 0, 10);

                    nms.blockBreakAnimation(player, data.getBlock(), 0, stage);
                }

                for(Player player : toRemove) {
                    blockBreakMap.remove(player);
                }
            }
        };

        runnable.runTaskTimer(BlockEngine.getInstance(), 0, 1);
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();

        List<Block> extractedBlocks = new ArrayList<>();

        blocks.stream().filter(block -> block.getType() == Material.NOTE_BLOCK).forEach(extractedBlocks::add);

        for(Block block : extractedBlocks) {
            CustomBlock customBlock = BlockManager.getInstance().getByBlockData((NoteBlock) block.getBlockData());

            event.blockList().remove(block);

            block.setType(Material.AIR);

            if(customBlock != null) {
                customBlock.callFunction(
                        FunctionType.BREAK,
                        block.getLocation(),
                        null
                );

                ItemStack drop = customBlock.getGeneratedDropItem();

                if(drop != null) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if(block.getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
        }
    }
}
