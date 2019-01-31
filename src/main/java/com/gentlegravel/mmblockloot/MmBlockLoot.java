package com.gentlegravel.mmblockloot;

import io.lumine.xikage.mythicmobs.MythicMobs;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MmBlockLoot extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private boolean enabled;
    private Map<BlockType, Set<Drop>> drops = new HashMap<>();
    @Getter private static MmBlockLoot instance;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        config = this.getConfig();
        enabled = config.getBoolean("enabled");

        if (enabled) {
            loadItems();
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Block block = event.getBlock();
        BlockType type = new BlockType(block.getType(), block.getData());
        Set<Drop> blockDrops = null;

        for (BlockType t : drops.keySet()) {
            if (type.equals(t)) {
                blockDrops = drops.get(t);
                break;
            }
        }

        if (blockDrops == null) return;

        for (Drop drop : blockDrops) {
            if (Math.random() < drop.getChance()) {
                block.getWorld().dropItemNaturally(
                        block.getLocation(),
                        drop.getItem()
                );
            }
        }
    }

    private void loadItems() {
        ConfigurationSection items = config.getConfigurationSection("items");
        int totalDrops = 0;

        for (String key : items.getKeys(false)) {
            ConfigurationSection blockDrops = items.getConfigurationSection(key);
            HashSet<Drop> set = new HashSet<>();
            BlockType type = BlockType.fromString(key);

            if (type == null) continue;

            for (String item : blockDrops.getKeys(false)) {
                if (!MythicMobs.inst().getItemManager().getItem(item).isPresent()) {
                    MmBlockLoot.getInstance().getLogger().warning("MythicMobs Item \"" + item + "\" not found");
                    continue;
                }

                set.add(new Drop(item, blockDrops.getConfigurationSection(item)));

                totalDrops++;
            }

            drops.put(type, set);
        }

        getLogger().info(String.format("Loaded %d blocks & %d drops", drops.size(), totalDrops));
    }
}
