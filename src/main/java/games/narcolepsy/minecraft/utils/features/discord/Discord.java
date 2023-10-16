package games.narcolepsy.minecraft.utils.features.discord;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import games.narcolepsy.minecraft.utils.features.discord.events.PlayerAdvancement;
import games.narcolepsy.minecraft.utils.features.discord.events.PlayerDeath;
import games.narcolepsy.minecraft.utils.features.discord.events.PlayerSession;
import games.narcolepsy.minecraft.utils.features.discord.events.ServerStartStop;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class Discord extends BaseFeature {
    private final Set<String> messages;
    private Manager manager = null;
    private Thread thread = null;
    private ServerStartStop serverStartStop = null;

    public Discord(Plugin plugin, List<String> messages) {
        super(plugin);
        this.messages = new HashSet<>(messages);
    }

    @Override
    public void Enable() {
        var discordWebhookURL = System.getenv().getOrDefault("NARCOLEPSY_DISCORD_WEBHOOK_URL", "");

        manager = new Manager(this.logger, discordWebhookURL);
        thread = new Thread(manager);
        thread.start();

        for (String m : this.messages) {
            this.logger.log(Level.INFO, "Adding message handler for '" + m + "'.");

            switch (m) {
                case ServerStartStop.TYPE -> {
                    serverStartStop = new ServerStartStop(manager);
                    this.server.getPluginManager().registerEvents(serverStartStop, this.plugin);
                }
                case PlayerSession.TYPE ->
                        this.server.getPluginManager().registerEvents(new PlayerSession(manager), this.plugin);
                case PlayerDeath.TYPE ->
                        this.server.getPluginManager().registerEvents(new PlayerDeath(manager), this.plugin);
                case PlayerAdvancement.TYPE ->
                        this.server.getPluginManager().registerEvents(new PlayerAdvancement(manager), this.plugin);
            }
        }
    }

    @Override
    public void Disable() {
        if (serverStartStop != null) {
            serverStartStop.onServerUnloadEvent();
        }

        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public String getName() {
        return "Discord";
    }
}
