package games.narcolepsy.minecraft.utils.features.mappoiserver;

import org.bukkit.entity.Player;

import java.util.UUID;

public record PlayerPosition(UUID uuid, String name, String world, double x, double y, double z, double pitch,
                             double yaw, double roll) {
    public PlayerPosition(Player p) {
        this(p.getUniqueId(), p.getName(), p.getLocation().getWorld().getName(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getPitch(), p.getLocation().getYaw(), 0);
    }
}
