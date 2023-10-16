package games.narcolepsy.minecraft.utils.features.sit;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Wall;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Sit extends BaseFeature implements Listener, @Nullable CommandExecutor {
    private final JavaPlugin javaPlugin;

    public Sit(JavaPlugin plugin) {
        super(plugin);
        this.javaPlugin = plugin;
    }

    @Override
    public void Enable() {
        super.Enable();
        javaPlugin.getCommand("sit").setExecutor(this);
    }

    @Override
    public String getName() {
        return "Sit";
    }

    /**
     * UUIDs of sit entities.
     */
    private final Map<UUID, BlockReference> chairEntities = new HashMap<>();

    /**
     * Listen for players clicking on stairs to sit on.
     *
     * @param playerInteractEvent player interact event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK
                || playerInteractEvent.getPlayer().isInsideVehicle()
                || playerInteractEvent.getMaterial() != Material.AIR) {
            return;
        }

        Block block = playerInteractEvent.getClickedBlock();

        if (block != null) {
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Stairs bi) {
                if (bi.getHalf() == Bisected.Half.BOTTOM) {
                    sitInChair(playerInteractEvent.getClickedBlock(), playerInteractEvent.getPlayer());
                    playerInteractEvent.setCancelled(true);
                }
            }
        }
    }

    /**
     * Listen for players quitting to remove their sit.
     *
     * @param playerQuitEvent player quit event
     */
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
        if (playerQuitEvent.getPlayer().isInsideVehicle()) {
            removeMountPoint(playerQuitEvent.getPlayer().getVehicle());
        }
    }

    /**
     * Listen for players dismounting.
     *
     * @param entityDismountEvent entity dismount event
     */
    @EventHandler
    public void onEntityDismountEvent(EntityDismountEvent entityDismountEvent) {
        if (entityDismountEvent.getEntity() instanceof Player player) {
            if (removeMountPoint(entityDismountEvent.getDismounted())) {
                entityDismountEvent.getEntity().teleport(entityDismountEvent.getDismounted().getLocation());
                player.teleport(player.getLocation().add(0, 1.75, 0));
            }
        }
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent cle) {
        this.server.getScheduler().runTaskLater(this.plugin, () -> {
            for (Entity e : cle.getChunk().getEntities()) {
                if (e instanceof ArmorStand as) {
                    if (as.isInvulnerable()) {
                        as.remove();
                    }
                }
            }
        }, 1);
    }

    /**
     * Add a new sit, mount the player
     *
     * @param block  block to sit on
     * @param player player to mount
     */
    private void sitInChair(Block block, Player player) {
        if (!(block.getBlockData() instanceof Stairs stair)) {
            return;
        }

        Location location = centerLocation(block.getLocation());
        BlockReference blockReference = new BlockReference(location);

        if (chairEntities.containsValue(blockReference)) {
            return;
        }

        double faceAdjust = 0.3;
        double heightAdjust = -1.7;

        switch (stair.getFacing()) {
            case SOUTH -> {
                location.setYaw(180);
                location.add(0, heightAdjust, -faceAdjust);
            }
            case WEST -> {
                location.setYaw(-90);
                location.add(faceAdjust, heightAdjust, 0);
            }
            case NORTH -> {
                location.setYaw(0);
                location.add(0, heightAdjust, faceAdjust);
            }
            case EAST -> {
                location.setYaw(90);
                location.add(-faceAdjust, heightAdjust, 0);
            }
        }

        addMountPoint(player, location);
    }

    /**
     * Add a new sit entity vehicle.
     *
     * @param player   player sitting
     * @param location location to sit
     */
    private void addMountPoint(Player player, Location location) {
        Entity entity = location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        entity.teleport(location);

        ArmorStand armorStand = (ArmorStand) entity;
        armorStand.addPassenger(player);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.0);

        chairEntities.put(armorStand.getUniqueId(), new BlockReference(location));
    }

    /**
     * Remove a sit entity vehicle, if it's one of ours.
     *
     * @param entity entity to check and remove
     */
    private boolean removeMountPoint(Entity entity) {
        if (chairEntities.remove(entity.getUniqueId()) != null) {
            this.server.getScheduler().runTaskLater(this.plugin, entity::remove, 1);
            return true;
        }

        return false;
    }

    /**
     * Calculate the centre of a block.
     *
     * @param location location to get centre of
     * @return new location with centre
     */
    private Location centerLocation(Location location) {
        double x = location.getBlockX() + 0.5;
        double y = location.getBlockY() + 0.5;
        double z = location.getBlockZ() + 0.5;

        return new Location(location.getWorld(), x, y, z, location.getYaw(), location.getPitch());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!((Entity) player).isOnGround() || player.isInsideVehicle()) {
            return true;
        }

        Location location = player.getLocation();
        double diffY = -1.65;

        Block blockBelow = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        BlockData belowData = blockBelow.getBlockData();

        if (belowData instanceof Wall || belowData instanceof Fence) {
            diffY -= 0.5;
        }

        addMountPoint(player, location.add(0, diffY, 0));

        return true;
    }
}
