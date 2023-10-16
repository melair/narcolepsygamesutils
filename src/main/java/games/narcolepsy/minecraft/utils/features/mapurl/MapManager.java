package games.narcolepsy.minecraft.utils.features.mapurl;

import org.bukkit.Server;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class MapManager {
    public final Server server;
    public final Logger logger;
    public final File mapDirectory;

    public MapManager(Server server, Logger logger, File mapDirectory) {
        this.server = server;
        this.logger = logger;
        this.mapDirectory = mapDirectory;
    }

    public void loadAndAttachMaps() throws IOException {
        Files.walk(this.mapDirectory.toPath()).filter((p) -> p.toFile().isFile()).forEach((p) -> {
            this.logger.info("Loading: " + p);
            var file = p.getFileName().toString().split("\\.")[0];
            var mapId = Integer.parseInt(file);

            var mapView = this.server.getMap(mapId);
            if (mapView != null) {
                try {
                    var img = ImageIO.read(p.toFile());
                    attachRender(mapView, img);
                } catch (IOException e) {
                    this.logger.warning("Failed to load image as map: " + p);
                }
            }
        });
    }

    public void addImage(MapView mv, BufferedImage src) throws IOException {
        var dst = new BufferedImage(128, 128, src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dst.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(src, 0, 0, 128, 128, null);
        g2d.dispose();

        ImageIO.write(dst, "png", new File(mapDirectory, String.format("%05d.png", mv.getId())));

        attachRender(mv, dst);
    }

    private void attachRender(MapView mv, BufferedImage img) {
        mv.addRenderer(new BufferedImageMapRenderer(img));
        mv.setCenterX(Integer.MAX_VALUE);
        mv.setCenterZ(Integer.MAX_VALUE);
        mv.setLocked(true);
        mv.setTrackingPosition(false);
        mv.setUnlimitedTracking(false);
    }
}
