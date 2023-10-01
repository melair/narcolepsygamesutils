package games.narcolepsy.minecraft.utils.features.nodefaultpermissions;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import games.narcolepsy.minecraft.utils.features.Feature;
import org.bukkit.Server;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NoDefaultPermissions extends BaseFeature implements Feature {

    public NoDefaultPermissions(Plugin plugin) {
        super(plugin);
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
