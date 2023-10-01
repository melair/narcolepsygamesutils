package games.narcolepsy.minecraft.utils.features.playerhead;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import games.narcolepsy.minecraft.utils.features.Feature;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public class PlayerHead extends BaseFeature implements Listener {

    public PlayerHead(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void Enable() {
        plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public String getName() {
        return "Player Head";
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
        Player killer = playerDeathEvent.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        sm.setOwningPlayer(playerDeathEvent.getEntity());
        item.setItemMeta(sm);

        playerDeathEvent.getDrops().add(item);
    }
}
