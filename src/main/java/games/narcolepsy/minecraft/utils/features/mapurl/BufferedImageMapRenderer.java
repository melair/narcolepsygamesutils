package games.narcolepsy.minecraft.utils.features.mapurl;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImageMapRenderer extends MapRenderer {
    private final BufferedImage src;

    private static final int MAP_SIZE = 128;

    public BufferedImageMapRenderer(BufferedImage src) {
        this.src = src;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        /* Write image to the output canvas. */
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                canvas.setPixelColor(x, y, new Color(src.getRGB(x, y)));
            }
        }
    }
}
