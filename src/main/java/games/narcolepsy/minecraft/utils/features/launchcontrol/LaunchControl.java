package games.narcolepsy.minecraft.utils.features.launchcontrol;

import games.narcolepsy.minecraft.utils.features.Feature;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.Logger;

public class LaunchControl implements Feature, Runnable, Listener {
    private final Plugin plugin;
    private final Server server;
    private final Logger logger;
    private final LocalDateTime launchAt;
    private final BossBar bar;
    private boolean hasLaunched;

    public LaunchControl(Plugin plugin, Server server, Logger logger, LocalDateTime launchAt) {
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;
        this.launchAt = launchAt;
        this.hasLaunched = this.launchAt.isBefore(LocalDateTime.now());

        logger.info("Launching at: " + launchAt);
        logger.info("Currently: " + LocalDateTime.now());
        logger.info("Already Launched: " + hasLaunched);

        this.bar = this.server.createBossBar("Server Launch", BarColor.RED, BarStyle.SEGMENTED_12);
        bar.setVisible(true);

        if (!this.hasLaunched) {
            this.server.getScheduler().runTask(this.plugin, () -> {
                logger.info("Not launched, locking down 'world'.");

                World w = this.server.getWorld("world");
                if (w != null) {
                    w.setDifficulty(Difficulty.PEACEFUL);
                    w.setSpawnFlags(false, true);
                    w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                    w.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
                    w.getWorldBorder().setCenter(w.getSpawnLocation().getX(), w.getSpawnLocation().getZ());
                    w.getWorldBorder().setSize(32);
                    w.setStorm(false);
                    w.setThundering(false);
                    w.setFullTime(0);
                    w.setTime(0);
                }
            });
        }
    }

    @Override
    public void Enable() {
        this.server.getScheduler().runTaskTimer(this.plugin, this, 20, 20);
        this.server.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public String getName() {
        return "Launch Control";
    }

    @Override
    public void run() {
        if (!this.hasLaunched) {
            if (this.launchAt.isBefore(LocalDateTime.now())) {
                this.hasLaunched = true;

                logger.info("Launching!");

                for (Player p : this.server.getOnlinePlayers()) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.setInvulnerable(false);
                    p.sendTitle(ChatColor.GREEN + "Go!", ChatColor.AQUA + "Good luck, have fun!", 0, 60, 20);
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
                    p.getInventory().clear();
                    p.clearActivePotionEffects();
                }

                World w = this.server.getWorld("world");

                if (w != null) {
                    w.setDifficulty(Difficulty.HARD);
                    w.setSpawnFlags(true, true);
                    w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                    w.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
                    w.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
                    w.getWorldBorder().setCenter(0, 0);
                    w.getWorldBorder().setSize(3072);
                    w.setTime(0);
                    w.setFullTime(0);
                }

                this.bar.removeAll();
            } else {
                long remainingSeconds = launchAt.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

                if (remainingSeconds > 3600) {
                    remainingSeconds = 3600;
                }

                bar.setProgress(1.0 - (remainingSeconds / 3600.0));

                if (remainingSeconds <= 10 && remainingSeconds > 0) {
                    for (Player p : this.server.getOnlinePlayers()) {
                        p.sendTitle(ChatColor.RED + String.valueOf(remainingSeconds), null, 0, 10, 10);
                        p.playNote(p.getLocation(), Instrument.STICKS, Note.natural(0, Note.Tone.G));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent pje) {
        Player p = pje.getPlayer();

        if (this.hasLaunched) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setInvulnerable(false);
            this.bar.removePlayer(p);
        } else {
            p.setGameMode(GameMode.ADVENTURE);
            p.setInvulnerable(true);
            this.server.getScheduler().runTaskLater(this.plugin, () -> {
                p.teleport(p.getWorld().getSpawnLocation());
            }, 1);
            this.bar.addPlayer(p);
        }
    }
}
