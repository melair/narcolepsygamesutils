package games.narcolepsy.minecraft.utils.features.discord.messages;

import org.bukkit.Color;

import java.util.HashMap;
import java.util.Map;

public final class SimpleMessage extends BaseMessage {
    private final String text;
    private final Color color;

    public SimpleMessage(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    @Override
    protected Map<String, Object> contents() {
        var embed = new HashMap<String, Object>();
        embed.put("color", color.asRGB());
        embed.put("title", text);
        return embed;
    }
}