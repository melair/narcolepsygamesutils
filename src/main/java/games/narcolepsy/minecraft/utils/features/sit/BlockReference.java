package games.narcolepsy.minecraft.utils.features.sit;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Reference to a block which equality can be checked based on the integer coordinates.
 */
public class BlockReference {
    /**
     * World.
     */
    public World world;
    /**
     * X position.
     */
    public int x;
    /**
     * Y position.
     */
    public int y;
    /**
     * Z position
     */
    public int z;

    /**
     * Construct a new block reference from a bukkit location.
     *
     * @param location bukkit location
     */
    public BlockReference(Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Construct a block reference from coordinates.
     *
     * @param x x position
     * @param y y position
     * @param z z position
     */
    public BlockReference(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !BlockReference.class.isAssignableFrom(o.getClass())) return false;

        BlockReference that = (BlockReference) o;
        return world == that.world && x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        int result = world.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    /**
     * Get a location representation.
     *
     * @return location of block
     */
    public Location getLocation() {
        return new Location(world, x + 0.5, y + 0.5, z + 0.5);
    }
}
