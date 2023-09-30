package games.narcolepsy.minecraft.utils.features.serverlist;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import games.narcolepsy.minecraft.utils.features.Feature;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

public class ServerList implements Feature, Listener {
    private final Server server;
    private final Plugin plugin;
    private final boolean hidePlayers;

    public ServerList(Server server, Plugin plugin, boolean hidePlayers) {
        this.server = server;
        this.plugin = plugin;
        this.hidePlayers = hidePlayers;
    }

    @Override
    public void Enable() {
        this.server.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getName() {
        return "Server List";
    }

    @EventHandler
    public void onPaperServerListPingEvent(PaperServerListPingEvent e) {
        if (this.hidePlayers) {
            e.getPlayerSample().clear();
        }
    }

    @EventHandler
    public void onServerListPingEvent(ServerListPingEvent e) {

    }
}