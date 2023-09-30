package games.narcolepsy.minecraft.utils;

import games.narcolepsy.minecraft.utils.features.Feature;
import games.narcolepsy.minecraft.utils.features.autorestart.AutoRestart;
import games.narcolepsy.minecraft.utils.features.chat.Chat;
import games.narcolepsy.minecraft.utils.features.compasshud.TextHUD;
import games.narcolepsy.minecraft.utils.features.customportals.CustomPortals;
import games.narcolepsy.minecraft.utils.features.disableendermangriefing.DisableEndermanGriefing;
import games.narcolepsy.minecraft.utils.features.discord.Discord;
import games.narcolepsy.minecraft.utils.features.serverlist.ServerList;
import games.narcolepsy.minecraft.utils.features.launchcontrol.LaunchControl;
import games.narcolepsy.minecraft.utils.features.nodefaultpermissions.NoDefaultPermissions;
import games.narcolepsy.minecraft.utils.features.placelightingonleaves.PlaceLightingOnLeaves;
import games.narcolepsy.minecraft.utils.features.playerhead.PlayerHead;
import games.narcolepsy.minecraft.utils.features.playerlist.PlayerList;
import games.narcolepsy.minecraft.utils.features.sit.Sit;
import games.narcolepsy.minecraft.utils.features.unloadspawnchunks.UnloadSpawnChunks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public final class UtilsPlugin extends JavaPlugin {
    private final Collection<Feature> features = new HashSet<>();

    @Override
    public void onEnable() {
        Logger l = getLogger();
        l.info("Narcolepsy Games: Utils Plugin");

        /* Save out default configuration, if a config does not exist. */
        saveDefaultConfig();

        /* Load configuration. */
        ConfigurationSection cfg = getConfig();

        if (cfg.getBoolean("features.no-default-permissions", false)) {
            addFeature(new NoDefaultPermissions(this, this.getServer(), l));
        }

        if (cfg.isConfigurationSection("features.launch-control")) {
            ConfigurationSection lcCfg = cfg.getConfigurationSection("features.launch-control");

            if (lcCfg.getBoolean("enabled", false)) {
                int year = lcCfg.getInt("launch-at.year");
                int month = lcCfg.getInt("launch-at.month");
                int day = lcCfg.getInt("launch-at.day");
                int hour = lcCfg.getInt("launch-at.hour");
                int minute = lcCfg.getInt("launch-at.minute");

                LocalDateTime launchAt = LocalDateTime.of(year, month, day, hour, minute);

                addFeature(new LaunchControl(this, this.getServer(), l, launchAt));
            }
        }

        if (cfg.getBoolean("features.unload-spawn-chunks", false)) {
            addFeature(new UnloadSpawnChunks(this.getServer(), l));
        }

        if (cfg.isConfigurationSection("features.auto-restart")) {
            ConfigurationSection lcCfg = cfg.getConfigurationSection("features.auto-restart");

            if (lcCfg.getBoolean("enabled", false)) {
                int restartAfter = lcCfg.getInt("restart-after", 3 * 3600);
                int forceRestartAfter = lcCfg.getInt("force-restart-after", 4 * 3600);
                int idleTime = lcCfg.getInt("idle-time", 300);

                addFeature(new AutoRestart(this, this.getServer(), restartAfter, forceRestartAfter, idleTime));
            }
        }

        if (cfg.getBoolean("features.sit", true)) {
            addFeature(new Sit(this, this.getServer()));
        }

        if (cfg.getBoolean("features.compasshud", true)) {
            addFeature(new TextHUD(this, this.getServer()));
        }

        if (cfg.getBoolean("features.playerhead", true)) {
            addFeature(new PlayerHead(this));
        }

        if (cfg.getBoolean("features.custom-portals", true)) {
            addFeature(new CustomPortals(this, this.getServer()));
        }

        if (cfg.isConfigurationSection("features.server-list")) {
            ConfigurationSection slCfg = cfg.getConfigurationSection("features.server-list");
            boolean hidePlayers = slCfg.getBoolean("hide-players", true);
            addFeature(new ServerList(this.getServer(), this, hidePlayers));
        }

        if (cfg.getBoolean("features.place-lighting-on-leaves", true)) {
            addFeature(new PlaceLightingOnLeaves(this, this.getServer()));
        }

        if (cfg.isConfigurationSection("features.discord")) {
            ConfigurationSection dCfg = cfg.getConfigurationSection("features.discord");
            assert dCfg != null;
            List<String> messages = dCfg.getStringList("message-types");
            addFeature(new Discord(this, this.getServer(), this.getLogger(), messages));
        }

        if (cfg.getBoolean("features.chat", true)) {
            addFeature(new Chat(this, this.getServer()));
        }

        if (cfg.getBoolean("features.serverlist", true)) {
            addFeature(new PlayerList(this, this.getServer()));
        }

        if (cfg.getBoolean("features.disableendermangriefing", true)) {
            addFeature(new DisableEndermanGriefing(this.getServer(), this));
        }

        for (Feature f : features) {
            l.info("Enabling: " + f.getName());
            f.Enable();
        }
    }

    private void addFeature(Feature feature) {
        Logger l = getLogger();

        features.add(feature);
        l.info("Loading: " + feature.getName());
    }

    @Override
    public void onDisable() {
        Logger l = getLogger();

        for (Feature f : features) {
            l.info("Disabling: " + f.getName());
            f.Disable();
        }
    }
}
