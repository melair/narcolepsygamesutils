package games.narcolepsy.minecraft.utils.features.discord.messages;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseMessage implements Embedder {
    protected abstract Map<String, Object> contents();

    @Override
    public Map<String, Object> embed() {
        var root = new HashMap<String, Object>();
        root.put("embeds", new Object[]{contents()});
        return root;
    }
}
