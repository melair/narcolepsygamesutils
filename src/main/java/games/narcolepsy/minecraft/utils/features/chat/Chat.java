package games.narcolepsy.minecraft.utils.features.chat;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import games.narcolepsy.minecraft.utils.features.Feature;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class Chat extends BaseFeature implements Listener {
    public Chat(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void Enable() {
        this.server.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public String getName() {
        return "Chat";
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent e) {
        e.renderer((source, sourceDisplayName, message, viewer) -> sourceDisplayName.color(NamedTextColor.WHITE).append(Component.text(": ").color(NamedTextColor.DARK_GRAY)).append(message.color(NamedTextColor.WHITE)));
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        e.joinMessage(Component.text("»» ").color(NamedTextColor.GRAY).append(e.getPlayer().displayName().color(NamedTextColor.WHITE)).append(Component.text(" joined.").color(NamedTextColor.GRAY)));
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        e.quitMessage(Component.text("»» ").color(NamedTextColor.GRAY).append(e.getPlayer().displayName().color(NamedTextColor.WHITE)).append(Component.text(" left.").color(NamedTextColor.GRAY)));
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent e) {
        e.leaveMessage(Component.text("»» ").color(NamedTextColor.GRAY).append(e.getPlayer().displayName().color(NamedTextColor.WHITE)).append(Component.text(" kicked.").color(NamedTextColor.GRAY)));
    }
}
