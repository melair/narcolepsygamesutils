package games.narcolepsy.minecraft.utils.features.betterbeacons;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Beacon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BetterBeacons extends BaseFeature implements Listener {
    private final double iron;
    private final double gold;
    private final double emerald;
    private final double diamond;
    private final double netherite;
    private final boolean respectVanillaMinimums;
    private final double maximumRange;

    public BetterBeacons(Plugin plugin, double iron, double gold, double emerald, double diamond, double netherite, boolean respectVanillaMinimums, double maximumRange) {
        super(plugin);
        this.iron = iron;
        this.gold = gold;
        this.emerald = emerald;
        this.diamond = diamond;
        this.netherite = netherite;
        this.respectVanillaMinimums = respectVanillaMinimums;
        this.maximumRange = maximumRange;
    }

    @Override
    public String getName() {
        return "Better Beacons";
    }

    @EventHandler
    public void onPlayerChangeBeaconEffectEvent(PlayerChangeBeaconEffectEvent e) {
        if (e.getBeacon().getState() instanceof Beacon b) {
            var range = calculateBeaconRange(b.getLocation(), b.getTier());
            b.setEffectRange(range);
            b.update();

            if (range == this.maximumRange) {
                e.getPlayer().sendMessage(prefix(Component.text("The beacon hums and you get the impression it's at maximum range.").color(NamedTextColor.RED)));
                e.getPlayer().getWorld().playSound(e.getBeacon().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, (float) 1.0, (float) 1.0);
            }
        }
    }

    public double calculateBeaconRange(Location l, int tiers) {
        double range = 0.0;

        for (var t = 0; t < tiers; t++) {
            int r = 1 + (t * 2);

            int y = l.getBlockY() - 1 - t;
            for (int x = l.getBlockX() - r; x <= l.getBlockX() + r; x++) {
                for (int z = l.getBlockZ() - r; z <= l.getBlockZ() + r; z++) {
                    var b = l.getWorld().getBlockAt(x, y, z);
                    range += lookupValue(b.getType());
                }
            }
        }

        if (this.respectVanillaMinimums) {
            range = Math.max(tierVanillaMinimum(tiers), range);
        }

        range = Math.min(this.maximumRange, range);

        return range;
    }

    private double tierVanillaMinimum(int tier) {
        return switch (tier) {
            case 1 -> 20.0;
            case 2 -> 30.0;
            case 3 -> 40.0;
            case 4 -> 50;
            default -> 20;
        };
    }

    private double lookupValue(Material type) {
        return switch (type) {
            case IRON_BLOCK -> this.iron;
            case GOLD_BLOCK -> this.gold;
            case EMERALD_BLOCK -> this.emerald;
            case DIAMOND_BLOCK -> this.diamond;
            case NETHERITE_BLOCK -> this.netherite;
            default -> 0.0;
        };
    }
}
