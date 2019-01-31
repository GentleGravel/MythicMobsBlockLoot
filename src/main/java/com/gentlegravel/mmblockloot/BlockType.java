package com.gentlegravel.mmblockloot;

import lombok.Getter;
import org.bukkit.Material;

class BlockType {
    @Getter private final Material material;
    @Getter private final int damage;

    BlockType(Material material, int damage) {
        this.material = material;
        this.damage = damage;
    }

    static BlockType fromString(String str) {
        String[] pieces = str.split(":");
        String name = pieces[0];
        int damage = pieces.length > 1 ? Integer.valueOf(pieces[1]) : 0;

        Material material = Material.matchMaterial(name);

        if (material == null) {
            MmBlockLoot.getInstance().getLogger().warning("Material \"" + name + "\" is invalid");
            return null;
        }

        return new BlockType(material, damage);
    }

    public String toString() {
        return String.format("%s (%d)", material, damage);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !BlockType.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final BlockType other = (BlockType) obj;

        return this.material == other.material && this.damage == other.damage;
    }
}
