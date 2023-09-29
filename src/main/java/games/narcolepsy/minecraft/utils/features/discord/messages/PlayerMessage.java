package games.narcolepsy.minecraft.utils.features.discord.messages;

import org.bukkit.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMessage extends BaseMessage {
    private final String playerName;
    private final UUID playerUUID;
    private final String title;
    private final String content;
    private final Color color;

    public PlayerMessage(String playerName, UUID playerUUID, String title, String content, Color color) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.title = title;
        this.color = color;
        this.content = content;
    }

    @Override
    protected Map<String, Object> contents() {
        var author = new HashMap<String, Object>();
        author.put("name", playerName);
        author.put("icon_url", String.format("https://cravatar.eu/avatar/%s/64.png", playerUUID));

        var embed = new HashMap<String, Object>();
        embed.put("author", author);
        embed.put("color", color.asRGB());
        embed.put("title", title);

        if (content != null) {
            embed.put("description", content);
        }

        return embed;
    }
}
