package games.narcolepsy.minecraft.utils.features.mapurl;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapURL extends BaseFeature implements @Nullable CommandExecutor {
    private final JavaPlugin javaPlugin;
    private final ExecutorService pool = Executors.newFixedThreadPool(2);
    private final OkHttpClient client = new OkHttpClient();
    private final MapManager mapManager;

    public MapURL(JavaPlugin javaPlugin) {
        super(javaPlugin);
        this.javaPlugin = javaPlugin;
        var mapPath = new File(this.javaPlugin.getDataFolder(), "maps");
        mapPath.mkdirs();
        this.mapManager = new MapManager(this.server, logger, mapPath);
    }

    @Override
    public void Enable() {
        this.javaPlugin.getCommand("mapurl").setExecutor(this);

        try {
            mapManager.loadAndAttachMaps();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "Map URL";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (sender instanceof Player player) {
            if (strings.length == 0) {
                player.sendMessage(prefix(Component.text("You must provide a URL to create a map from.").color(NamedTextColor.RED)));
                return true;
            }

            pool.submit(() -> {
                Request request = new Request.Builder().url(strings[0]).get().build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        sendSyncMessage(player, Component.text("Failed to retrieve image: ").color(NamedTextColor.RED).append(Component.text("Status code was " + response.code())).color(NamedTextColor.YELLOW));
                        return;
                    }

                    try {
                        var img = ImageIO.read(response.body().byteStream());

                        if (img == null) {
                            sendSyncMessage(player, Component.text("Failed to read image: ").color(NamedTextColor.RED).append(Component.text("No image found at URL.")).color(NamedTextColor.YELLOW));
                            return;
                        }

                        sendSyncMessage(player, Component.text("Loaded image!").color(NamedTextColor.GREEN));
                        sendSyncMap(player, img);
                    } catch (RuntimeException e) {
                        sendSyncMessage(player, Component.text("Failed to read image: ").color(NamedTextColor.RED).append(Component.text(e.getMessage())).color(NamedTextColor.YELLOW));
                    }
                } catch (IOException e) {
                    sendSyncMessage(player, Component.text("Failed to retrieve image: ").color(NamedTextColor.RED).append(Component.text(e.getMessage())).color(NamedTextColor.YELLOW));
                }
            });

            return true;
        }

        return false;
    }

    private void sendSyncMessage(Player p, Component c) {
        this.server.getScheduler().runTask(this.plugin, () -> {
            var newPlayer = this.server.getPlayer(p.getUniqueId());

            if (newPlayer != null) {
                newPlayer.sendMessage(prefix(c));
            }
        });
    }

    private void sendSyncMap(Player p, BufferedImage img) {
        this.server.getScheduler().runTask(this.plugin, () -> {
            var newPlayer = this.server.getPlayer(p.getUniqueId());

            if (newPlayer == null) {
                return;
            }

            MapView mv = this.server.createMap(newPlayer.getWorld());
            try {
                mapManager.addImage(mv, img);
            } catch (IOException e) {
                this.logger.warning("Failed to add new image requested by " + p.getName());
                return;
            }

            ItemStack itemMap = new ItemStack(Material.FILLED_MAP, 1);
            MapMeta meta = (MapMeta) itemMap.getItemMeta();
            meta.setMapView(mv);
            itemMap.setItemMeta(meta);

            newPlayer.getInventory().addItem(itemMap);
        });
    }
}
