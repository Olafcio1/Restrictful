package pl.olafcio.restrictionmaster;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RestrictionMaster extends JavaPlugin implements Listener {
    private FileConfiguration config;

    private ListType         entitySpawnMode;
    private List<EntityType> entitySpawnValues;

    private ListType       blockPlaceMode;
    private List<Material> blockPlaceValues;

    private ListType         entityInteractMode;
    private List<EntityType> entityInteractValues;

    private ListType       itemUseMode;
    private List<Material> itemUseValues;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        config = getConfig();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        entitySpawnMode = ListType.of(config.getString("entity-spawn.mode"));
        entitySpawnValues = config.getStringList("entity-spawn." + entitySpawnMode.id).stream().map(EntityType::fromName).toList();

        blockPlaceMode = ListType.of(config.getString("block-place.mode"));
        blockPlaceValues = config.getStringList("block-place." + blockPlaceMode.id).stream().map(Material::getMaterial).toList();

        entityInteractMode = ListType.of(config.getString("entity-interact.mode"));
        entityInteractValues = config.getStringList("entity-interact." + entityInteractMode.id).stream().map(EntityType::fromName).toList();

        itemUseMode = ListType.of(config.getString("item-use.mode"));
        itemUseValues = config.getStringList("item-use." + itemUseMode.id).stream().map(Material::getMaterial).toList();
    }

    @Override
    public void onDisable() {}

    @EventHandler
    public void onSpawnEntity(EntitySpawnEvent event) {
        var entityType = event.getEntity().getType();
        if (entitySpawnMode == ListType.WHITE) {
            if (!entitySpawnValues.contains(entityType))
                event.setCancelled(true);
        } else if (entitySpawnMode == ListType.BLACK) {
            if (entitySpawnValues.contains(entityType))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        var blockType = event.getBlock().getType();
        if (blockPlaceMode == ListType.WHITE) {
            if (!blockPlaceValues.contains(blockType))
                event.setCancelled(true);
        } else if (blockPlaceMode == ListType.BLACK) {
            if (blockPlaceValues.contains(blockType))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        var entityType = event.getEntity().getType();
        if (entityInteractMode == ListType.WHITE) {
            if (!entityInteractValues.contains(entityType))
                event.setCancelled(true);
        } else if (blockPlaceMode == ListType.BLACK) {
            if (entityInteractValues.contains(entityType))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEntityEvent event) {
        var itemType = event.getPlayer().getInventory().getItemInMainHand().getType();
        if (itemUseMode == ListType.WHITE) {
            if (!itemUseValues.contains(itemType))
                event.setCancelled(true);
        } else if (blockPlaceMode == ListType.BLACK) {
            if (itemUseValues.contains(itemType))
                event.setCancelled(true);
        }
    }
}
