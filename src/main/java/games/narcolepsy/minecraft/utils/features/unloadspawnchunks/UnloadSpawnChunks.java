package games.narcolepsy.minecraft.utils.features.unloadspawnchunks;

import games.narcolepsy.minecraft.utils.features.Feature;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class UnloadSpawnChunks implements Feature {
    private final Server server;
    private final Logger l;

    public UnloadSpawnChunks(Server server, Logger l) {
        this.server = server;
        this.l = l;
    }

    @Override
    public void Enable() {
        l.info("Clearing Keep Spawn In Memory:");
        for (World w : server.getWorlds()) {
            l.info("- " + w.getName());
            w.setKeepSpawnInMemory(false);
        }
    }

    @Override
    public String getName() {
        return "Unload Spawn Chunks";
    }
}
