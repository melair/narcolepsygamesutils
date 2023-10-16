package games.narcolepsy.minecraft.utils.features.boatnames;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public class BoatNames extends BaseFeature implements Listener {
    public BoatNames(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "Boat Names";
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Boat b) {
            var item = e.getPlayer().getInventory().getItem(e.getHand());

            if (item.getType() == Material.NAME_TAG && item.getItemMeta().hasDisplayName()) {
                var name = item.getItemMeta().displayName();
                b.customName(name);
                b.setCustomNameVisible(true);
                e.getPlayer().getInventory().remove(item);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDestroyEvent(VehicleDestroyEvent e) {
        if (e.getVehicle() instanceof Boat b) {
            if (b.customName() != null) {
                e.setCancelled(true);
                e.getVehicle().remove();

                var item = new ItemStack(b.getBoatMaterial(), 1);
                var meta = item.getItemMeta();
                meta.lore(Collections.singletonList(b.customName()));
                item.setItemMeta(meta);

                e.getVehicle().getLocation().getWorld().dropItemNaturally(e.getVehicle().getLocation(), item);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (e.hasItem()) {
            var item = e.getItem();

            if (item.getType().toString().endsWith("_BOAT")) {
                if (item.getItemMeta().lore() != null && item.getItemMeta().lore().size() == 1) {
                    e.setCancelled(true);
                    e.getPlayer().getInventory().remove(item);

                    var entity = (item.getType().toString().endsWith("_CHEST_BOAT") ? EntityType.CHEST_BOAT : EntityType.BOAT);
                    var woodType = item.getType().toString().split("_")[0];

                    var be = e.getPlayer().getLocation().getWorld().spawnEntity(e.getInteractionPoint(), entity);
                    if (be instanceof Boat b) {
                        b.setBoatType(Boat.Type.valueOf(woodType));
                    }

                    be.customName(item.getItemMeta().lore().get(0));
                    be.setCustomNameVisible(true);
                }
            }
        }
    }
}
