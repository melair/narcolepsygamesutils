package games.narcolepsy.minecraft.utils.features.autorestart;

import games.narcolepsy.minecraft.utils.features.Feature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.time.Instant;

public class AutoRestart implements Feature, Runnable, Listener {
    private final Plugin plugin;
    private final Server server;
    private final long restartAfter;
    private final long forceRestartAfter;
    private final long idleTime;
    private final long startTime;
    private long lastLeaveTime;
    private boolean shutdownStarted;
    private int shutdownTimer = 30;

    public AutoRestart(Plugin plugin, Server server, long restartAfter, long forceRestartAfter, long idleTime) {
        this.plugin = plugin;
        this.server = server;
        this.restartAfter = restartAfter;
        this.forceRestartAfter = forceRestartAfter;
        this.idleTime = idleTime;
        this.startTime = getCurrentTime();
        this.shutdownStarted = false;
    }

    @Override
    public void Enable() {
        server.getScheduler().runTaskTimer(plugin, this, 20, 20);
        server.getPluginManager().registerEvents(this, plugin);

        plugin.getLogger().info("Auto Restart after " + this.restartAfter + " and idle time of " + this.idleTime + ".");
    }

    @Override
    public String getName() {
        return "Auto Restart";
    }

    @Override
    public void run() {
        if (this.shutdownStarted) {
            handleShutdown();
            return;
        }

        long currentTime = getCurrentTime();
        long forceShutdown = startTime + forceRestartAfter;

        if (currentTime >= forceShutdown) {
            this.shutdownStarted = true;
            return;
        }

        if (server.getOnlinePlayers().size() > 0) {
            return;
        }

        long wantShutdown = startTime + restartAfter;

        if (currentTime < wantShutdown) {
            return;
        }

        long idleTime = this.lastLeaveTime + this.idleTime;

        if (currentTime >= idleTime) {
            this.shutdownStarted = true;
        }
    }

    private void handleShutdown() {
        if (server.getOnlinePlayers().size() == 0 || this.shutdownTimer == 0) {
            plugin.getLogger().warning("Restarting due to length of time server has run.");
            server.shutdown();
            return;
        }

        if (this.shutdownTimer % 5 == 0 || this.shutdownTimer < 5) {
            server.broadcast(Component.text("Server restarting in ").color(NamedTextColor.RED).append(Component.text(this.shutdownTimer).color(NamedTextColor.GOLD).append(Component.text(" seconds...").color(NamedTextColor.RED))));
        }

        shutdownTimer--;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent pqe) {
        lastLeaveTime = getCurrentTime();
    }

    private long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }
}
