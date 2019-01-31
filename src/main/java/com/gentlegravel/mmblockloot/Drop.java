package com.gentlegravel.mmblockloot;

import io.lumine.xikage.mythicmobs.MythicMobs;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class Drop {
    @Getter private String name;
    @Getter private int amount;
    @Getter private double chance;

    Drop(String key, ConfigurationSection config) {
        this.name = key;
        this.amount = config.getInt("amount", 1);
        this.chance = config.getDouble("chance", 1);
    }

    ItemStack getItem() {
        ItemStack item = MythicMobs.inst().getItemManager().getItemStack(name);

        if (null != item) {
            item.setAmount(amount);
        }

        return item;
    }
}
