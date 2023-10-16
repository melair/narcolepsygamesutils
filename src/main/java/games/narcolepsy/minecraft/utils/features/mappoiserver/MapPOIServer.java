package games.narcolepsy.minecraft.utils.features.mappoiserver;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import games.narcolepsy.minecraft.utils.features.BaseFeature;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MapPOIServer extends BaseFeature {
    private final Map<String, String> worldMapping = new HashMap<>();
    private final int port;
    private final String bindAddress;
    private final Gson gson;

    public MapPOIServer(Plugin plugin, int port, String bindAddress) {
        super(plugin);
        this.port = port;
        this.bindAddress = bindAddress;
        this.gson = new Gson();

        worldMapping.put("overworld", "world");
        worldMapping.put("nether", "world_nether");
        worldMapping.put("end", "world_the_end");
    }

    @Override
    public void Enable() {
        RoutingHandler router = new RoutingHandler();
        router.get("/maps/{map}/live/players.json", this::handlePlayerPOI);
        router.get("/maps/{map}/assets/playerheads/{uuid}", this::handlePlayerHead);

        Undertow server = Undertow.builder().addHttpListener(port, bindAddress).setHandler(router).build();

        this.logger.info("Starting HTTP server: " + bindAddress + ":" + port);
        new Thread(server::start).start();
    }

    @Override
    public String getName() {
        return "Map POI Server";
    }

    public void handlePlayerHead(HttpServerExchange exchange) {
        var uuid = Optional.ofNullable(exchange.getQueryParameters().get("uuid")).map(Deque::getFirst).orElse("b7ffce14-24d6-482e-8acb-77c9aec3bb4e.png");
        uuid = uuid.replace(".png", "");

        exchange.getResponseHeaders().put(Headers.LOCATION, "https://cravatar.eu/avatar/" + uuid + "/48.png");
        exchange.setStatusCode(302);
    }

    public void handlePlayerPOI(HttpServerExchange exchange) {
        var world = Optional.ofNullable(exchange.getQueryParameters().get("map")).map(Deque::getFirst).orElse("world");
        var mappedWorld = worldMapping.getOrDefault(world, world);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

        var futurePositions = this.server.getScheduler().callSyncMethod(this.plugin, () -> this.getPlayerPOI(mappedWorld));

        try {
            var playerPositions = futurePositions.get();

            var root = new JsonObject();
            var players = new JsonArray();
            root.add("players", players);

            for (var pp : playerPositions) {
                var player = new JsonObject();
                player.addProperty("uuid", pp.uuid().toString());
                player.addProperty("name", pp.name());
                player.addProperty("foreign", false);

                var pos = new JsonObject();
                pos.addProperty("x", pp.x());
                pos.addProperty("y", pp.y());
                pos.addProperty("z", pp.z());
                player.add("position", pos);

                var rotation = new JsonObject();
                rotation.addProperty("pitch", pp.pitch());
                rotation.addProperty("yaw", pp.yaw());
                rotation.addProperty("roll", pp.roll());
                player.add("rotation", rotation);

                players.add(player);
            }

            exchange.getResponseSender().send(gson.toJson(root));
        } catch (InterruptedException | ExecutionException e) {
            exchange.setStatusCode(500);
        }
    }

    public List<PlayerPosition> getPlayerPOI(String world) {
        return this.server.getOnlinePlayers().stream().filter((p) -> p.getWorld().getName().equals(world)).map(PlayerPosition::new).collect(Collectors.toList());
    }
}
