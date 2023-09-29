package games.narcolepsy.minecraft.utils.features.nodefaultpermissions;

import games.narcolepsy.minecraft.utils.features.Feature;
import org.bukkit.Server;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NoDefaultPermissions implements Feature {
    private final Plugin plugin;
    private final Server server;
    private final Logger logger;

    public NoDefaultPermissions(Plugin plugin, Server server, Logger logger) {
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void Enable() {
        server.getScheduler().runTask(plugin, () -> {
            logger.log(Level.INFO, "Removing default permissions:");

            Set<Permission> defaults = server.getPluginManager().getDefaultPermissions(false);

            for (Permission def : defaults) {
                logger.log(Level.INFO, " - " + def.getName() + ": " + def.getDefault() + " -> " + PermissionDefault.FALSE);
                def.setDefault(PermissionDefault.FALSE);
                server.getPluginManager().recalculatePermissionDefaults(def);
            }
        });
    }

    @Override
    public String getName() {
        return "No Default Permissions";
    }
}
