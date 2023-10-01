package games.narcolepsy.minecraft.utils.features;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.units.qual.C;

import java.util.logging.Logger;

public abstract class BaseFeature  implements Feature {
    protected final Plugin plugin;
    protected final Server server;
    protected final Logger logger;

    protected BaseFeature(Plugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.logger = plugin.getLogger();
    }

    protected Component prefix(Component c) {
        return Component.text("(").color(NamedTextColor.GRAY).append(Component.text("\uD83D\uDCA4").color(NamedTextColor.WHITE)).append(Component.text(") ").color(NamedTextColor.GRAY)).append(c);
    }
}
