package games.narcolepsy.minecraft.utils.features.villagerheal;

import games.narcolepsy.minecraft.utils.features.BaseFeature;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.plugin.Plugin;

public class VillagerHeal extends BaseFeature implements Listener {
    public VillagerHeal(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "Villager Heal";
    }

    @EventHandler
    public void onEntityPoseChangeEvent(EntityPoseChangeEvent e) {
        if (e.getEntity() instanceof Villager v) {
            if (e.getPose() == Pose.SLEEPING) {
                var maxHealth = v.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                if (v.getHealth() < maxHealth) {
                    v.setHealth(Math.min(maxHealth, v.getHealth() + 2));
                    v.getWorld().spawnParticle(Particle.HEART, e.getEntity().getLocation(), 1, 0.25, 0.25, 0.25);
                }
            }
        }
    }
}
