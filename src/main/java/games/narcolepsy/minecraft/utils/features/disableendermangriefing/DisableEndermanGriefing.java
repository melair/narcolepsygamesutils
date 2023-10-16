package games.narcolepsy.minecraft.utils.features.disableendermangriefing;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;

public class DisableEndermanGriefing extends BaseFeature implements Listener {


    public DisableEndermanGriefing(Plugin plugin) {
        super(plugin);
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
