package com.mira.blockengine.blocks.core;

import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;

public class BlockData {
    private Instrument instrument;
    private Note note;

    private BreakingTool breakingTool;
    private double hardness;

    private ToolType minimumTool;

    public BlockData(Instrument instrument, Note note, BreakingTool breakingTool, double hardness, ToolType minimumTool) {
        this.instrument = instrument;
        this.note = note;
        this.breakingTool = breakingTool;
        this.hardness = hardness;
        this.minimumTool = minimumTool;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public Note getNote() {
        return note;
    }

    public BreakingTool getBreakingTool() {
        return breakingTool;
    }

    public double getHardness() {
        return hardness;
    }

    public ToolType getMinimumTool() {
        return minimumTool;
    }

    public enum BreakingTool {
        SWORD,
        PICKAXE,
        AXE,
        SHOVEL,
        HOE,
        SHEARS,
        NONE;

        public static BreakingTool getTool(Material m) {
            switch(m) {
                case WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {
                    return SWORD;
                }
                case WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE -> {
                    return PICKAXE;
                }
                case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE -> {
                    return AXE;
                }
                case WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL -> {
                    return SHOVEL;
                }
                case WOODEN_HOE, STONE_HOE, IRON_HOE, GOLDEN_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    return HOE;
                }
                case SHEARS -> {
                    return SHEARS;
                }
                default -> {
                    return NONE;
                }
            }
        }
    }

    public enum ToolType {
        NOTHING(1),
        WOOD(2),
        STONE(4),
        IRON(6),
        GOLD(12),
        DIAMOND(8),
        NETHERITE(9);

        private final int multiplier;

        ToolType(int multiplier){
            this.multiplier = multiplier;
        }

        public int getMultiplier(){
            return multiplier;
        }

        public static ToolType fromMaterial(Material M) {
            switch(M) {
                case WOODEN_SWORD, WOODEN_PICKAXE, WOODEN_AXE, WOODEN_SHOVEL, WOODEN_HOE -> {
                    return WOOD;
                }
                case STONE_SWORD, STONE_PICKAXE, STONE_AXE, STONE_SHOVEL, STONE_HOE -> {
                    return STONE;
                }
                case IRON_SWORD, IRON_PICKAXE, IRON_AXE, IRON_SHOVEL, IRON_HOE -> {
                    return IRON;
                }
                case GOLDEN_SWORD, GOLDEN_PICKAXE, GOLDEN_AXE, GOLDEN_SHOVEL, GOLDEN_HOE -> {
                    return GOLD;
                }
                case DIAMOND_SWORD, DIAMOND_PICKAXE, DIAMOND_AXE, DIAMOND_SHOVEL, DIAMOND_HOE -> {
                    return DIAMOND;
                }
                case NETHERITE_SWORD, NETHERITE_PICKAXE, NETHERITE_AXE, NETHERITE_SHOVEL, NETHERITE_HOE -> {
                    return NETHERITE;
                }
                default -> {
                    return NOTHING;
                }
            }
        }

        public static boolean isAboveMinimumTool(ToolType minimum, ToolType tool) {
            if (minimum == null) return true;

            switch (minimum) {
                case NOTHING -> {
                    return true;
                }
                case WOOD -> {
                    return tool == ToolType.WOOD || tool == ToolType.STONE || tool == ToolType.IRON || tool == ToolType.GOLD || tool == ToolType.DIAMOND || tool == ToolType.NETHERITE;
                }
                case STONE -> {
                    return tool == ToolType.STONE || tool == ToolType.IRON || tool == ToolType.GOLD || tool == ToolType.DIAMOND || tool == ToolType.NETHERITE;
                }
                case IRON -> {
                    return tool == ToolType.IRON || tool == ToolType.GOLD || tool == ToolType.DIAMOND || tool == ToolType.NETHERITE;
                }
                case GOLD -> {
                    return tool == ToolType.GOLD || tool == ToolType.DIAMOND || tool == ToolType.NETHERITE;
                }
                case DIAMOND -> {
                    return tool == ToolType.DIAMOND || tool == ToolType.NETHERITE;
                }
                case NETHERITE -> {
                    return tool == ToolType.NETHERITE;
                }
                default -> {
                    return false;
                }
            }
        }
    }
}
