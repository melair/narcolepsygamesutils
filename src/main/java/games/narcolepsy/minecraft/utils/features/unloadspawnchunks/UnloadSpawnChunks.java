package games.narcolepsy.minecraft.utils.features.unloadspawnchunks;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class UnloadSpawnChunks extends BaseFeature {
    public UnloadSpawnChunks(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void Enable() {
        this.logger.info("Clearing Keep Spawn In Memory:");
        for (World w : server.getWorlds()) {
            this.logger.info("- " + w.getName());
            w.setKeepSpawnInMemory(false);
        }
    }

    @Override
    public String getName() {
        return "Unload Spawn Chunks";
    }
}
