package games.narcolepsy.minecraft.utils.features.discord.events;

import games.narcolepsy.minecraft.utils.features.discord.Manager;
import games.narcolepsy.minecraft.utils.features.discord.messages.PlayerMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    public static final String TYPE = "player-death";

    private final Manager manager;

    public PlayerDeath(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        var title = PlainTextComponentSerializer.plainText().serialize(e.deathMessage());
        manager.queue(new PlayerMessage(e.getPlayer().getName(), e.getPlayer().getUniqueId(), title, null, Color.RED));
    }
}
