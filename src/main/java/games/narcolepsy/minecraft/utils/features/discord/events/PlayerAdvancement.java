package games.narcolepsy.minecraft.utils.features.discord.events;

import games.narcolepsy.minecraft.utils.features.discord.Manager;
import games.narcolepsy.minecraft.utils.features.discord.messages.PlayerMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerAdvancement implements Listener {
    public static final String TYPE = "player-advancement";

    private final Manager manager;

    public PlayerAdvancement(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerAdvancementDoneEvent(PlayerAdvancementDoneEvent e) {
        var display = e.getAdvancement().getDisplay();

        if (display == null) {
            return;
        }

        var advancementTitle = PlainTextComponentSerializer.plainText().serialize(display.title());
        var message = String.format("%s has made the advancement [%s]", e.getPlayer().getName(), advancementTitle);
        var advancementDescription = PlainTextComponentSerializer.plainText().serialize(display.description());
        manager.queue(new PlayerMessage(e.getPlayer().getName(), e.getPlayer().getUniqueId(), message, advancementDescription, Color.WHITE));
    }
}
