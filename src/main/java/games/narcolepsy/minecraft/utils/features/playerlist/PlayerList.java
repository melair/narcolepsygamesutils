package games.narcolepsy.minecraft.utils.features.playerlist;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import games.narcolepsy.minecraft.utils.features.Feature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerList extends BaseFeature implements Listener {


    public PlayerList(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void Enable() {
        this.server.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public String getName() {
        return "Player List";
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        e.getPlayer().sendPlayerListHeaderAndFooter(Component.text("Welcome to Narcolepsy Games PVE").color(NamedTextColor.RED), Component.text("See the map at: ").color(NamedTextColor.GOLD).append(Component.text("https://map.narcolepsy.games").color(NamedTextColor.BLUE)));
    }
}
