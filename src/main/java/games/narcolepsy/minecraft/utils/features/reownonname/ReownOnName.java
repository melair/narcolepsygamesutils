package games.narcolepsy.minecraft.utils.features.reownonname;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import io.papermc.paper.event.player.PlayerNameEntityEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ReownOnName extends BaseFeature implements Listener {
    public ReownOnName(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void Enable() {
        this.server.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public String getName() {
        return "Reown On Name";
    }

    @EventHandler
    public void onPlayerNameEntityEvent(PlayerNameEntityEvent e) {
        if (e.getEntity() instanceof Tameable t) {
            if (t.isTamed()) {
                if (t.getOwner() != null) {
                    if (!t.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {
                        e.getPlayer().sendMessage(prefix(Component.text("Naming this creature change the owner to you from ").color(NamedTextColor.GOLD).append(Component.text(t.getOwner().getName()).color(NamedTextColor.RED).append(Component.text(".").color(NamedTextColor.GOLD)))));
                    }
                }

                t.setOwner(e.getPlayer());
            }
        }
    }
}
