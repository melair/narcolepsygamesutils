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
import games.narcolepsy.minecraft.utils.features.launchcontrol.LaunchControl;
import games.narcolepsy.minecraft.utils.features.mappoiserver.MapPOIServer;
import games.narcolepsy.minecraft.utils.features.mapurl.MapURL;
import games.narcolepsy.minecraft.utils.features.nodefaultpermissions.NoDefaultPermissions;
import games.narcolepsy.minecraft.utils.features.placelightingonleaves.PlaceLightingOnLeaves;
import games.narcolepsy.minecraft.utils.features.playerhead.PlayerHead;
import games.narcolepsy.minecraft.utils.features.playerlist.PlayerList;
import games.narcolepsy.minecraft.utils.features.reownonname.ReownOnName;
import games.narcolepsy.minecraft.utils.features.serverlist.ServerList;
import games.narcolepsy.minecraft.utils.features.sit.Sit;
import games.narcolepsy.minecraft.utils.features.unloadspawnchunks.UnloadSpawnChunks;
import games.narcolepsy.minecraft.utils.features.villagerheal.VillagerHeal;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public final class UtilsPlugin extends JavaPlugin {
    private final Collection<Feature> features = new HashSet<>();

    @Override
    public void onEnable() {
        Logger l = getLogger();
        l.info("Narcolepsy Games: Utils Plugin");

        /* Save out default configuration, if a config does not exist. */
        saveDefaultConfig();

        configFeature("no-default-permissions", (cfg) -> new NoDefaultPermissions(this));
        configFeature("reown-on-name", (cfg) -> new ReownOnName(this));
        configFeature("mapurl", (cfg) -> new MapURL(this));
        configFeature("health-track", (cfg) -> new HealthTrack(this));
        configFeature("villager-heal", (cfg) -> new VillagerHeal(this));
        configFeature("unload-spawn-chunks", (cfg) -> new UnloadSpawnChunks(this));
        configFeature("sit", (cfg) -> new Sit(this));
        configFeature("texthud", (cfg) -> new TextHUD(this));
        configFeature("player-head", (cfg) -> new PlayerHead(this));
        configFeature("custom-portals", (cfg) -> new CustomPortals(this));
        configFeature("place-lighting-on-leaves", (cfg) -> new PlaceLightingOnLeaves(this));
        configFeature("boat-names", (cfg) -> new BoatNames(this));
        configFeature("chat", (cfg) -> new Chat(this));
        configFeature("player-list", (cfg) -> new PlayerList(this));
        configFeature("disable-enderman-griefing", (cfg) -> new DisableEndermanGriefing(this));

        configFeature("map-poi-server", (cfg) -> {
            var port = cfg.getInt("port", 8080);
            var bindAddress = cfg.getString("bindAddress", "localhost");

            return new MapPOIServer(this, port, bindAddress);
        });

        configFeature("launch-control", (cfg) -> {
            var year = cfg.getInt("launch-at.year");
            var month = cfg.getInt("launch-at.month");
            var day = cfg.getInt("launch-at.day");
            var hour = cfg.getInt("launch-at.hour");
            var minute = cfg.getInt("launch-at.minute");

            LocalDateTime launchAt = LocalDateTime.of(year, month, day, hour, minute);
            return new LaunchControl(this, launchAt);
        });

        configFeature("auto-restart", (cfg) -> {
            var restartAfter = cfg.getInt("restart-after", 4 * 3600);
            var forceRestartAfter = cfg.getInt("eager-time", 3600);

            return new AutoRestart(this, restartAfter, forceRestartAfter);
        });

        configFeature("server-list", (cfg) -> {
            var hidePlayers = cfg.getBoolean("hide-players", true);
            return new ServerList(this, hidePlayers);
        });

        configFeature("discord", (cfg) -> {
            var messages = cfg.getStringList("message-types");
            return new Discord(this, messages);
        });

        configFeature("better-beacons", (cfg) -> {
            var iron = cfg.getDouble("values.iron", 0.5);
            var gold = cfg.getDouble("values.gold", 0.75);
            var emerald = cfg.getDouble("values.emerald", 1.25);
            var diamond = cfg.getDouble("values.diamond", 1.75);
            var netherite = cfg.getDouble("values.netherite", 2.5);

            var respectVanillaMinimums = cfg.getBoolean("respect-vanilla-minimums", true);
            var limitToLoadDistance = cfg.getBoolean("limit-to-view-distance", true);

            var maxDistance = (limitToLoadDistance ? this.getServer().getViewDistance() : Double.MAX_VALUE);

            return new BetterBeacons(this, iron, gold, emerald, diamond, netherite, respectVanillaMinimums, maxDistance);
        });

        for (Feature f : features) {
            l.info("Enabling: " + f.getName());
            f.Enable();
        }
    }

    private void configFeature(String name, Function<ConfigurationSection, Feature> callback) {
        var globalCfg = getConfig();
        var cfgSectionName = String.format("features.%s", name);
        var featureCfg = globalCfg.getConfigurationSection(cfgSectionName);

        if (featureCfg != null) {
            if (featureCfg.getBoolean("enabled", false)) {
                addFeature(callback.apply(featureCfg));
            }
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
