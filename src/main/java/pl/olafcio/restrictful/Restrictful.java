package pl.olafcio.restrictful;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public final class Restrictful extends JavaPlugin implements Listener {
    private FileConfiguration config;

    private ListType         entitySpawnMode;
    private List<EntityType> entitySpawnValues;

    private ListType       blockPlaceMode;
    private List<Material> blockPlaceValues;

    private ListType         entityInteractMode;
    private List<EntityType> entityInteractValues;

    private ListType       itemUseMode;
    private List<Material> itemUseValues;

    private ListType       creativeGetMode;
    private List<Material> creativeGetValues;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        config = getConfig();
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            var mainCommand = Commands.literal("restrictful")
                    .then(Commands.literal("reload")
                            .requires(src -> src.getSender().hasPermission("restrictful.reload"))
                            .executes(ctx -> {
                                var now = System.currentTimeMillis();

                                reloadConfig();
                                config = getConfig();

                                ctx.getSource().getSender().sendMessage(
                                        "§3[Restrictful]§7 " +
                                        "Reloaded the configuration in " +
                                        "§2" + (System.currentTimeMillis() - now) + "ms§7."
                                );

                                return SINGLE_SUCCESS;
                            })
                    )
                    .executes(ctx -> {
                        ctx.getSource().getSender().sendMessage("§3[Restrictful]§7 Made by §2Olafcio§7 with §4❤");
                        return SINGLE_SUCCESS;
                    })
            .build();

            commands.registrar().register(mainCommand, Collections.singleton("rm"));
        });

        entitySpawnMode = ListType.of(config.getString("entity-spawn.mode"));
        entitySpawnValues = config.getStringList("entity-spawn." + entitySpawnMode.id).stream().map(EntityType::fromName).toList();

        blockPlaceMode = ListType.of(config.getString("block-place.mode"));
        blockPlaceValues = config.getStringList("block-place." + blockPlaceMode.id)
                .stream()
                        .map(String::toUpperCase)
                        .map(Material::getMaterial)
                .toList();

        entityInteractMode = ListType.of(config.getString("entity-interact.mode"));
        entityInteractValues = config.getStringList("entity-interact." + entityInteractMode.id).stream().map(EntityType::fromName).toList();

        itemUseMode = ListType.of(config.getString("item-use.mode"));
        itemUseValues = config.getStringList("item-use." + itemUseMode.id)
                .stream()
                        .map(String::toUpperCase)
                        .map(Material::getMaterial)
                .toList();

        creativeGetMode = ListType.of(config.getString("creative-get.mode"));
        creativeGetValues = config.getStringList("creative-get." + itemUseMode.id)
                .stream()
                        .map(String::toUpperCase)
                        .map(Material::getMaterial)
                .toList();
    }


    @Override
    public void onDisable() {}

    @EventHandler
    public void onSpawnEntity(EntitySpawnEvent event) {
        var entityType = event.getEntity().getType();
        if (entitySpawnValues.contains(entityType) == entitySpawnMode.cancel)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        var blockType = event.getBlock().getType();
        if (blockPlaceValues.contains(blockType) == blockPlaceMode.cancel)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        var entityType = event.getEntity().getType();
        if (entityInteractValues.contains(entityType) == entityInteractMode.cancel)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        var item = event.getItem();
        if (item == null)
            return;

        var itemType = item.getType();
        if (itemUseValues.contains(itemType) == itemUseMode.cancel)
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        var stack = event.getCursor();
        if (creativeGetValues.contains(stack.getType()) == creativeGetMode.cancel)
            event.setCancelled(true);
    }
}
