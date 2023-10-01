package games.narcolepsy.minecraft.utils.features.autorestart;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import games.narcolepsy.minecraft.utils.features.Feature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.time.Instant;

public class AutoRestart extends BaseFeature implements Runnable, Listener {
    private final BossBar bossBar;
    private final long restartAfter;
    private final long eagerTime;
    private final long startTime;
    private long remainingTime;


    public AutoRestart(Plugin plugin, long restartAfter, long eagerTime) {
        super(plugin);

        this.restartAfter = restartAfter;
        this.eagerTime = eagerTime;
        this.startTime = getCurrentTime();
        this.remainingTime = this.startTime + this.restartAfter + this.eagerTime;

        this.bossBar = server.createBossBar("Server Restart", BarColor.BLUE, BarStyle.SEGMENTED_12);
    }

    @Override
    public void Enable() {
        server.getScheduler().runTaskTimer(plugin, this, 20, 20);
        server.getPluginManager().registerEvents(this, plugin);

        plugin.getLogger().info("Auto Restart after " + this.restartAfter + " and eager time of " + this.eagerTime + ".");
    }

    @Override
    public String getName() {
        return "Auto Restart";
    }

    @Override
    public void run() {
        this.remainingTime = this.restartAfter - (this.getCurrentTime() - this.startTime);

        if (this.remainingTime == this.eagerTime) {
            for (var p : this.server.getOnlinePlayers()) {
                this.bossBar.addPlayer(p);
            }

            server.broadcast(prefix(Component.text("Server will restart next time it is empty or in an another ").color(NamedTextColor.RED).append(Component.text(this.eagerTime).color(NamedTextColor.GOLD).append(Component.text(" seconds...").color(NamedTextColor.RED)))));
        } else if (this.remainingTime < this.eagerTime) {
            if (server.getOnlinePlayers().size() == 0) {
                plugin.getLogger().warning("Restarting due to length of time server has run.");
                server.shutdown();
                return;
            }

            this.bossBar.setProgress(Math.min(1.0, 1.0 - (this.remainingTime / (this.eagerTime * 1.0))));

            if (this.remainingTime <= this.eagerTime) {
                if (this.remainingTime < 10 || this.remainingTime % 5 == 0) {
                    server.broadcast(prefix(Component.text("Server restarting in ").color(NamedTextColor.RED).append(Component.text(this.remainingTime).color(NamedTextColor.GOLD).append(Component.text(" seconds...").color(NamedTextColor.RED)))));
                }
            }

            if (this.remainingTime <= 0) {
                for (var p : this.server.getOnlinePlayers()) {
                    p.kick(Component.text("Server restarting..."));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (this.remainingTime < this.eagerTime) {
            this.bossBar.addPlayer(e.getPlayer());
        }
    }

    private long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }
}
