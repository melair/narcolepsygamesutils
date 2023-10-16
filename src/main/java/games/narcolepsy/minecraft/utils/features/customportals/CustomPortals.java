package games.narcolepsy.minecraft.utils.features.customportals;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;

public class CustomPortals extends BaseFeature implements Listener {
    private final String worldOverworldA = "world";
    private final String worldOverworldB = "world_two";
    private final String worldNether = "world_nether";
    private final Material portalMaterial = Material.OXIDIZED_COPPER;

    private Material nextCreateMaterial = Material.OBSIDIAN;

    public CustomPortals(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void Enable() {
        super.Enable();

        WorldCreator wc = WorldCreator.name(worldOverworldB);
        wc.seed(1855196926162235186L);
        this.server.createWorld(wc);
    }

    @Override
    public String getName() {
        return "Custom Portals";
    }

    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent e) {
        Location newTo = rewritePortal(e.getFrom().clone(), e.getTo().clone());
        e.setTo(newTo);
    }

    @EventHandler
    public void onEntityPortalEvent(EntityPortalEvent e) {
        Location newTo = rewritePortal(e.getFrom().clone(), e.getTo().clone());
        e.setTo(newTo);
    }

    BlockFace[] blockFaces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    private Location rewritePortal(Location from, Location to) {
        // Fuzz the location due to inaccurate from.
        Block fromBlock = from.getBlock();
        if (fromBlock.getType() != Material.NETHER_PORTAL) {
            for (BlockFace tryFace : blockFaces) {
                Block tryBlock = fromBlock.getRelative(tryFace);

                if (tryBlock.getType() == Material.NETHER_PORTAL) {
                    from = tryBlock.getLocation();
                    break;
                }
            }
        }

        Material sourcePortalMaterial = findPortalMaterial(from.clone());
        Location newTo = to.clone();

        if (sourcePortalMaterial != null) {
            if (from.getWorld().getName().equals(worldOverworldA)) {
                if (sourcePortalMaterial == portalMaterial) {
                    newTo = new Location(this.server.getWorld(worldOverworldB), from.getX(), from.getY(), from.getZ());
                    nextCreateMaterial = portalMaterial;
                }
            } else if (from.getWorld().getName().equals(worldOverworldB)) {
                if (sourcePortalMaterial == portalMaterial) {
                    newTo = new Location(this.server.getWorld(worldOverworldA), from.getX(), from.getY(), from.getZ());
                    nextCreateMaterial = portalMaterial;
                } else if (to.getWorld().getName().equals(worldNether)) {
                    nextCreateMaterial = portalMaterial;
                }
            } else if (from.getWorld().getName().equals(worldNether)) {
                if (sourcePortalMaterial == portalMaterial) {
                    newTo = new Location(this.server.getWorld(worldOverworldB), from.getX() * 8, from.getY() * 8, from.getZ() * 8);
                }
            }
        }

        return newTo;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() != Material.FLINT_AND_STEEL || e.getClickedBlock() == null || e.getClickedBlock().getType() != portalMaterial) {
            return;
        }

        this.server.getScheduler().runTask(this.plugin, () -> createPortalIfNeeded(e.getClickedBlock(), e.getBlockFace()));
    }

    private void createPortalIfNeeded(Block block, BlockFace face) {
        Collection<Block> portalBlocks = new ArrayList<>();

        boolean portalX = walkPortal(block, face, portalBlocks);

        for (Block b : portalBlocks) {
            BlockState bs = b.getState();
            bs.setType(Material.NETHER_PORTAL);
            BlockData bd = bs.getBlockData();

            if (bd instanceof Orientable o) {
                if (portalX) {
                    o.setAxis(Axis.X);
                } else {
                    o.setAxis(Axis.Z);
                }
            }

            bs.setBlockData(bd);
            bs.update(true);
        }
    }

    private boolean walkPortal(Block startBlock, BlockFace face, Collection<Block> portalBlocks) {
        boolean portalX = false;

        // Find Portal Alignment X/Z
        if (face == BlockFace.EAST || face == BlockFace.WEST) {
            portalX = true;
        } else if (face == BlockFace.UP || face == BlockFace.DOWN) {
            if (startBlock.getRelative(BlockFace.EAST).getType() == portalMaterial || startBlock.getRelative(BlockFace.WEST).getType() == portalMaterial) {
                portalX = true;
            }
        }

        int yUpper = Integer.MIN_VALUE;
        int yLower = Integer.MAX_VALUE;

        int hUpper = Integer.MIN_VALUE;
        int hLower = Integer.MAX_VALUE;

        // Find upper/lower bounds
        Block b = startBlock.getRelative(face);

        // Seek Up
        for (int i = 0; i < 8; i++) {
            Block rel = b.getRelative(0, i, 0);

            if (rel.getType() == portalMaterial) {
                yUpper = i;
                break;
            }
        }

        // Seek Down
        for (int i = -1; i > -8; i--) {
            Block rel = b.getRelative(0, i, 0);

            if (rel.getType() == portalMaterial) {
                yLower = i;
                break;
            }
        }

        // Seek "Left"
        for (int i = -1; i > -8; i--) {
            Block rel;

            if (portalX) {
                rel = b.getRelative(i, 0, 0);
            } else {
                rel = b.getRelative(0, 0, i);
            }

            if (rel.getType() == portalMaterial) {
                hLower = i;
                break;
            }
        }

        // Seek "Right"
        for (int i = 0; i < 8; i++) {
            Block rel;

            if (portalX) {
                rel = b.getRelative(i, 0, 0);
            } else {
                rel = b.getRelative(0, 0, i);
            }

            if (rel.getType() == portalMaterial) {
                hUpper = i;
                break;
            }
        }

        if (yUpper == Integer.MIN_VALUE || yLower == Integer.MAX_VALUE || hUpper == Integer.MIN_VALUE || hLower == Integer.MAX_VALUE) {
            return portalX;
        }

        // Validate frame is complete
        for (int h = hLower; h <= hUpper; h++) {
            for (int y = yLower; y <= yUpper; y++) {
                // Skip corner pieces, we don't care.
                if (h == hLower || h == hUpper) {
                    if (y == yLower || y == yUpper) {
                        continue;
                    }
                }


                boolean requireFrame = (h == hLower || h == hUpper || y == yLower || y == yUpper);

                Block t;

                if (portalX) {
                    t = b.getRelative(h, y, 0);
                } else {
                    t = b.getRelative(0, y, h);
                }

                Material tt = t.getType();

                if (requireFrame) {
                    if (tt != portalMaterial) {
                        return portalX;
                    }
                } else {
                    if (tt != Material.AIR && tt != Material.FIRE) {
                        return portalX;
                    }
                }
            }
        }

        for (int h = hLower + 1; h < hUpper; h++) {
            for (int y = yLower + 1; y < yUpper; y++) {
                if (portalX) {
                    portalBlocks.add(b.getRelative(h, y, 0));
                } else {
                    portalBlocks.add(b.getRelative(0, y, h));
                }
            }
        }

        return portalX;
    }

    private Material findPortalMaterial(Location l) {
        Block b = l.getBlock();

        for (; b.getY() > 0; b = b.getRelative(BlockFace.DOWN)) {
            if (b.getType() != Material.NETHER_PORTAL) {
                return b.getType();
            }
        }

        return null;
    }


    @EventHandler
    public void onPortalCreateEvent(PortalCreateEvent portalCreateEvent) {
        for (BlockState bs : portalCreateEvent.getBlocks()) {
            if (bs.getType() == Material.OBSIDIAN) {
                bs.setType(nextCreateMaterial);
            }
        }

        nextCreateMaterial = Material.OBSIDIAN;
    }
}
