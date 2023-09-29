package games.narcolepsy.minecraft.utils.features.discord.events;

import games.narcolepsy.minecraft.utils.features.discord.Manager;
import games.narcolepsy.minecraft.utils.features.discord.messages.PlayerMessage;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerSession implements Listener {
    public static final String TYPE = "player-session";

    private final Manager manager;

    public PlayerSession(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        manager.queue(new PlayerMessage(e.getPlayer().getName(), e.getPlayer().getUniqueId(), String.format("%s has joined the server.", e.getPlayer().getName()), null, Color.BLUE));
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        manager.queue(new PlayerMessage(e.getPlayer().getName(), e.getPlayer().getUniqueId(), String.format("%s has left the server.", e.getPlayer().getName()), null, Color.NAVY));
    }
}
