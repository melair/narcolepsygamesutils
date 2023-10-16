package games.narcolepsy.minecraft.utils;

import games.narcolepsy.minecraft.utils.features.Feature;
import games.narcolepsy.minecraft.utils.features.autorestart.AutoRestart;
import games.narcolepsy.minecraft.utils.features.betterbeacons.BetterBeacons;
import games.narcolepsy.minecraft.utils.features.boatnames.BoatNames;
import games.narcolepsy.minecraft.utils.features.chat.Chat;
import games.narcolepsy.minecraft.utils.features.compasshud.TextHUD;
import games.narcolepsy.minecraft.utils.features.customportals.CustomPortals;
import games.narcolepsy.minecraft.utils.features.disableendermangriefing.DisableEndermanGriefing;
import games.narcolepsy.minecraft.utils.features.discord.Discord;
import games.narcolepsy.minecraft.utils.features.healthtrack.HealthTrack;
import games.narcolepsy.minecraft.utils.features.mappoiserver.MapPOIServer;
import games.narcolepsy.minecraft.utils.features.mapurl.MapURL;
import games.narcolepsy.minecraft.utils.features.reownonname.ReownOnName;
import games.narcolepsy.minecraft.utils.features.serverlist.ServerList;
import games.narcolepsy.minecraft.utils.features.launchcontrol.LaunchControl;
import games.narcolepsy.minecraft.utils.features.nodefaultpermissions.NoDefaultPermissions;
import games.narcolepsy.minecraft.utils.features.placelightingonleaves.PlaceLightingOnLeaves;
import games.narcolepsy.minecraft.utils.features.playerhead.PlayerHead;
import games.narcolepsy.minecraft.utils.features.playerlist.PlayerList;
import games.narcolepsy.minecraft.utils.features.sit.Sit;
import games.narcolepsy.minecraft.utils.features.unloadspawnchunks.UnloadSpawnChunks;
import games.narcolepsy.minecraft.utils.features.villagerheal.VillagerHeal;
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
            addFeature(new NoDefaultPermissions(this));
        }

        if (cfg.getBoolean("features.reown-on-name", true)) {
            addFeature(new ReownOnName(this));
        }

        if (cfg.getBoolean("features.mapurl", true)) {
            addFeature(new MapURL(this));
        }

        if (cfg.getBoolean("features.healthtrack", true)) {
            addFeature(new HealthTrack(this));
        }

        if (cfg.getBoolean("features.villagerheal", true)) {
            addFeature(new VillagerHeal(this));
        }

        if (cfg.isConfigurationSection("features.mappoiserver")) {
            ConfigurationSection lcCfg = cfg.getConfigurationSection("features.mappoiserver");

            if (lcCfg.getBoolean("enabled", true)) {
                int port = lcCfg.getInt("port", 8080);
                String bindAddress = lcCfg.getString("bindAddress", "localhost");
                addFeature(new MapPOIServer(this, port, bindAddress));
            }
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

                addFeature(new LaunchControl(this, launchAt));
            }
        }

        if (cfg.getBoolean("features.unload-spawn-chunks", false)) {
            addFeature(new UnloadSpawnChunks(this));
        }

        if (cfg.isConfigurationSection("features.auto-restart")) {
            ConfigurationSection lcCfg = cfg.getConfigurationSection("features.auto-restart");

            if (lcCfg.getBoolean("enabled", false)) {
                int restartAfter = lcCfg.getInt("restart-after", 4 * 3600);
                int forceRestartAfter = lcCfg.getInt("eager-time", 3600);

                addFeature(new AutoRestart(this, restartAfter, forceRestartAfter));
            }
        }

        if (cfg.getBoolean("features.sit", true)) {
            addFeature(new Sit(this));
        }

        if (cfg.getBoolean("features.compasshud", true)) {
            addFeature(new TextHUD(this));
        }

        if (cfg.getBoolean("features.playerhead", true)) {
            addFeature(new PlayerHead(this));
        }

        if (cfg.getBoolean("features.custom-portals", true)) {
            addFeature(new CustomPortals(this));
        }

        if (cfg.isConfigurationSection("features.server-list")) {
            ConfigurationSection slCfg = cfg.getConfigurationSection("features.server-list");
            boolean hidePlayers = slCfg.getBoolean("hide-players", true);
            addFeature(new ServerList(this, hidePlayers));
        }

        if (cfg.getBoolean("features.place-lighting-on-leaves", true)) {
            addFeature(new PlaceLightingOnLeaves(this));
        }

        if (cfg.getBoolean("features.boat-names", true)) {
            addFeature(new BoatNames(this));
        }

        if (cfg.isConfigurationSection("features.discord")) {
            ConfigurationSection dCfg = cfg.getConfigurationSection("features.discord");
            assert dCfg != null;
            List<String> messages = dCfg.getStringList("message-types");
            addFeature(new Discord(this, messages));
        }

        if (cfg.getBoolean("features.chat", true)) {
            addFeature(new Chat(this));
        }

        if (cfg.getBoolean("features.serverlist", true)) {
            addFeature(new PlayerList(this));
        }

        if (cfg.getBoolean("features.disableendermangriefing", true)) {
            addFeature(new DisableEndermanGriefing(this));
        }

        if (cfg.isConfigurationSection("features.better-beacons")) {
            ConfigurationSection bCfg = cfg.getConfigurationSection("features.better-beacons");
            assert bCfg != null;

            if (bCfg.getBoolean("enabled", false)) {
                double iron = bCfg.getDouble("iron-value", 0.5);
                double gold = bCfg.getDouble("gold-value", 0.75);
                double emerald = bCfg.getDouble("emerald-value", 1.25);
                double diamond = bCfg.getDouble("diamond-value", 1.75);
                double netherite = bCfg.getDouble("netherite-value", 2.5);

                boolean respectVanillaMinimums = bCfg.getBoolean("respect-vanilla-minimums", true);
                boolean limitToLoadDistance = bCfg.getBoolean("limit-to-load-distance", true);

                double maxDistance = (limitToLoadDistance ? this.getServer().getViewDistance() : Double.MAX_VALUE);

                addFeature(new BetterBeacons(this, iron, gold, emerald, diamond, netherite, respectVanillaMinimums, maxDistance));
            }
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
