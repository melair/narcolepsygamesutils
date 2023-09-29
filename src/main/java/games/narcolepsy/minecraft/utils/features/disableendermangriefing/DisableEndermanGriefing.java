package games.narcolepsy.minecraft.utils.features.disableendermangriefing;

import games.narcolepsy.minecraft.utils.features.Feature;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;

public class DisableEndermanGriefing implements Feature, Listener {
    private final Server server;
    private final Plugin plugin;

    public DisableEndermanGriefing(Server server, Plugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void Enable() {
        server.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public String getName() {
        return "Disable Enderman Griefing";
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent e) {
        if (e.getEntity().getType() == EntityType.ENDERMAN) {
            e.setCancelled(true);
        }
    }
}
