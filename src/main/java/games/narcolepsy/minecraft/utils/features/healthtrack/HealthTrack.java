package games.narcolepsy.minecraft.utils.features.healthtrack;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HealthTrack extends BaseFeature implements Listener {
    private final Map<UUID, DamageTrackingState> playerState = new HashMap<>();

    public HealthTrack(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "HealthTrack";
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        playerState.put(e.getPlayer().getUniqueId(), new DamageTrackingState(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - e.getPlayer().getHealth()));
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        playerState.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            playerState.get(p.getUniqueId()).damage(e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        var history = playerState.get(e.getPlayer().getUniqueId()).getDamageHistory();
        playerState.get(e.getPlayer().getUniqueId()).death();

        var historyHeader = Component.text("Damage History").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE).appendNewline();

        List<Component> historyList = new ArrayList<>();

        for (var event : history) {
            List<Component> damageLine = new ArrayList<>();

            var damageCount = Component.text(String.format("%.01f❤ ", event.getDamageAmount() / 2.0)).color(NamedTextColor.RED);
            var damageReason = Component.text(event.getDamageCause().toString() + " ").color(NamedTextColor.WHITE);

            damageLine.add(damageCount);
            damageLine.add(damageReason);

            if (event.getEntityType() != null) {
                var damageEntity = Component.text("by ").append(Component.translatable(event.getEntityType()).color(NamedTextColor.YELLOW));
                damageLine.add(damageEntity);
            }

            if (event.getEntityName() != null) {
                var damageEntity = Component.text("(" + event.getEntityName() + ") ").color(NamedTextColor.YELLOW);
                damageLine.add(damageEntity);
            }

            historyList.add(Component.join(JoinConfiguration.noSeparators(), damageLine));
        }

        var historyPopup = historyHeader.append(Component.join(JoinConfiguration.newlines(), historyList).decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE));

        e.deathMessage(e.deathMessage().hoverEvent(HoverEvent.showText(historyPopup)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player p) {
            playerState.get(p.getUniqueId()).heal(e);
        }
    }
}
