package games.narcolepsy.minecraft.utils.features.compasshud;

import games.narcolepsy.minecraft.utils.features.Feature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class TextHUD implements Feature, Listener {
    private static final int UPDATE_INTERVAL = 3;
    private static final int SAVE_INTERVAL = 3 * 60 * 20;
    private final Plugin plugin;
    private final Server server;
    private final Set<UUID> hudPlayer = new HashSet<>();
    private final Map<UUID, Boolean> compassPlayer = new HashMap<>();
    private final Map<UUID, Boolean> clockPlayer = new HashMap<>();
    private File settingsFile;

    public TextHUD(Plugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public void Enable() {
        this.server.getPluginManager().registerEvents(this, this.plugin);
        this.server.getScheduler().runTaskTimer(this.plugin, this::updateHUDs, UPDATE_INTERVAL, UPDATE_INTERVAL);
        this.server.getScheduler().runTaskTimer(this.plugin, this::saveSettings, SAVE_INTERVAL, SAVE_INTERVAL);

        settingsFile = new File(this.plugin.getDataFolder(), "texthud.yml");
        loadSettings();
        saveSettings();
    }

    @Override
    public void Disable() {
        saveSettings();
    }

    private void loadSettings() {
        FileConfiguration settingsCfg = YamlConfiguration.loadConfiguration(settingsFile);

        if (settingsCfg.isList("compass")) {
            settingsCfg.getStringList("compass").forEach((s) -> {
                UUID uuid = UUID.fromString(s);
                compassPlayer.put(uuid, true);
            });
        }

        if (settingsCfg.isList("clock")) {
            settingsCfg.getStringList("clock").forEach((s) -> {
                UUID uuid = UUID.fromString(s);
                clockPlayer.put(uuid, true);
            });
        }
    }

    private void saveSettings() {
        FileConfiguration settingsCfg = new YamlConfiguration();

        List<String> compassList = compassPlayer.keySet().stream().map(UUID::toString).collect(Collectors.toList());
        List<String> clockList = clockPlayer.keySet().stream().map(UUID::toString).collect(Collectors.toList());

        settingsCfg.set("compass", compassList);
        settingsCfg.set("clock", clockList);

        try {
            settingsCfg.save(settingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Compass HUD";
    }

    public void updateHUDs() {
        Collection<World> sleepingWorlds = this.server.getWorlds().stream().filter((w) -> w.getPlayers().stream().anyMatch(Player::isSleeping)).collect(Collectors.toList());
        hudPlayer.stream().map(this.server::getPlayer).filter(Objects::nonNull).filter((p) -> !sleepingWorlds.contains(p.getWorld())).forEach(this::runPlayer);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
        Player p = playerInteractEvent.getPlayer();

        if ((playerInteractEvent.getAction() != Action.RIGHT_CLICK_AIR && playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK)
                || playerInteractEvent.getHand() == null
                || !p.isSneaking()) {
            return;
        }

        ItemStack i = p.getInventory().getItem(playerInteractEvent.getHand());
        if (i == null) {
            return;
        }

        if (i.getType() == Material.CLOCK) {
            boolean clockHud = clockPlayer.getOrDefault(p.getUniqueId(), false);

            if (!clockHud) {
                clockPlayer.put(p.getUniqueId(), true);
                hudPlayer.add(p.getUniqueId());
            } else {
                clockPlayer.remove(p.getUniqueId());
            }
        } else if (i.getType() == Material.COMPASS) {
            boolean compassHud = compassPlayer.getOrDefault(p.getUniqueId(), false);

            if (!compassHud) {
                compassPlayer.put(p.getUniqueId(), true);
                hudPlayer.add(p.getUniqueId());
            } else {
                compassPlayer.remove(p.getUniqueId());
            }
        }
    }

    private void runPlayer(Player player) {
        Component bar = Component.empty();

        if (compassPlayer.getOrDefault(player.getUniqueId(), false)) {
            Location pl = player.getLocation().clone();

            double playerRotation = (pl.getYaw() + 180) % 360;

            bar = bar
                    .append(Component.text("   "))
                    .append(Component.text("XYZ", NamedTextColor.GOLD))
                    .append(Component.text(" "))
                    .append(Component.text(pl.getBlockX(), NamedTextColor.WHITE))
                    .append(Component.text(" "))
                    .append(Component.text(pl.getBlockY(), NamedTextColor.WHITE))
                    .append(Component.text(" "))
                    .append(Component.text(pl.getBlockZ(), NamedTextColor.WHITE))
                    .append(Component.text(" "))
                    .append(Component.text(getNESW(playerRotation), NamedTextColor.GOLD))
                    .append(Component.text(" "));

            bar = bar.append(addDirectionArrow(player, player.getCompassTarget(), NamedTextColor.GREEN));

            bar = bar.append(Component.text("   "));
        }

        if (clockPlayer.getOrDefault(player.getUniqueId(), false)) {
            World w = this.server.getWorld("world");

            if (w != null) {
                long hours = (w.getTime() / 1000) + 6;
                if (hours > 23) {
                    hours -= 24;
                }

                long minutes = (w.getTime() % 1000) * 60 / 1000;

                bar = bar
                        .append(Component.text("   "))
                        .append(Component.text(String.format("%02d", hours) + ":" + String.format("%02d", minutes), NamedTextColor.GOLD))
                        .append(Component.text("   "));
            }
        }

        player.sendActionBar(bar);
    }

    private Component addDirectionArrow(Player p, Location l, NamedTextColor c) {
        Component bar = Component.empty();

        if (!p.getWorld().equals(l.getWorld())) {
            bar = bar.append(Component.text("?", c).decorate(TextDecoration.OBFUSCATED));
        } else {
            Vector cv = l.toVector();
            Vector pv = p.getLocation().toVector();

            double playerRotation = (p.getYaw() + 180) % 360;

            Vector diff = pv.clone().subtract(cv);

            double degrees = Math.toDegrees(Math.atan2(diff.getZ(), diff.getX()));
            degrees = (degrees + 270) % 360;

            degrees -= playerRotation;
            if (degrees < 0) {
                degrees += 360;
            }

            bar = bar.append(Component.text(getArrow(degrees), c));
        }

        return bar;
    }

    private String getNESW(double rotation) {
        if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        } else {
            return "?";
        }
    }

    private String getArrow(double rotation) {
        if (0 <= rotation && rotation < 22.5) {
            return "↑";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "⬈";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "→";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "⬊";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "↓";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "⬋";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "←";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "⬉";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "↑";
        } else {
            return "?";
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent pje) {
        if (compassPlayer.getOrDefault(pje.getPlayer().getUniqueId(), false) || clockPlayer.getOrDefault(pje.getPlayer().getUniqueId(), false)) {
            hudPlayer.add(pje.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPublicQuitEvent(PlayerQuitEvent pqe) {
        hudPlayer.remove(pqe.getPlayer().getUniqueId());
    }
}

