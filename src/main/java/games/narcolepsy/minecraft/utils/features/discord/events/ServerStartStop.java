package games.narcolepsy.minecraft.utils.features.discord.events;

import games.narcolepsy.minecraft.utils.features.discord.Manager;
import games.narcolepsy.minecraft.utils.features.discord.messages.SimpleMessage;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerStartStop implements Listener {
    public static final String TYPE = "start-stop";

    private final Manager manager;

    public ServerStartStop(Manager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onServerLoadEvent(ServerLoadEvent e) {
        manager.queue(new SimpleMessage("Server started.", Color.GREEN));
    }

    public void onServerUnloadEvent() {
        manager.queue(new SimpleMessage("Server stopped.", Color.RED));
    }
}
