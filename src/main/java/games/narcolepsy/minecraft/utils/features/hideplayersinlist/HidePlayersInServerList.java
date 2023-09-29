package games.narcolepsy.minecraft.utils.features.hideplayersinlist;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import games.narcolepsy.minecraft.utils.features.Feature;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class HidePlayersInServerList implements Feature, Listener {
    private final Server server;
    private final Plugin plugin;

    public HidePlayersInServerList(Server server, Plugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void Enable() {
        this.server.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getName() {
        return "Hide Players In Server List";
    }

    @EventHandler
    public void onPaperServerListPingEvent(PaperServerListPingEvent e) {
        e.getPlayerSample().clear();
    }
}
