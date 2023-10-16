/*
 * GameDirector (statistics) - Copyright © 2013-2016 - Alastria Networks Limited
 * All rights reserved, unauthorised distribution of source code, compiled
 * binary or usage is expressly prohibited.
 */
package games.narcolepsy.minecraft.utils.features.healthtrack;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Track a users damage.
 */
public class DamageTrackingState {
    /** History of users damage. */
    private List<DamageRecord> damageHistory = new ArrayList<>();

    public DamageTrackingState(double initialDamage) {
        damageHistory.add(new DamageRecord(EntityDamageEvent.DamageCause.CUSTOM, initialDamage, null, null, null));
    }

    /**
     * Mark the user as having been damaged.
     *
     * @param entityDamageEvent entity damage event
     */
    public void damage(EntityDamageEvent entityDamageEvent) {
        EntityDamageEvent.DamageCause damageCause = entityDamageEvent.getCause();
        double damageAmount = entityDamageEvent.getFinalDamage();
        EntityType entityType = null;
        String entityName = null;
        UUID entityUUID = null;

        if (entityDamageEvent instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Entity entity = entityDamageByEntityEvent.getDamager();

            if (entity instanceof Projectile p) {
                if (p.getShooter() instanceof Entity) {
                    entity = (Entity) ((Projectile) entity).getShooter();
                } else {
                    entity = null;
                }
            }

            if (entity != null) {
                entityType = entity.getType();
                if (entityType == EntityType.PLAYER) {
                    entityName = entity.getName();
                } else {
                    if (entity.customName() != null) {
                        entityName = PlainTextComponentSerializer.plainText().serialize(entity.customName());
                    }
                }

                entityUUID = entity.getUniqueId();
            }
        }

        damageHistory.add(new DamageRecord(damageCause, damageAmount, entityType, entityName, entityUUID));
    }

    /**
     * Mark the user as having healed.
     *
     * @param entityRegainHealthEvent entity regain health event
     */
    public void heal(EntityRegainHealthEvent entityRegainHealthEvent) {
        double healAmount = entityRegainHealthEvent.getAmount();

        while (healAmount > 0 && damageHistory.size() > 0) {
            DamageRecord damageRecord = damageHistory.get(0);
            healAmount = damageRecord.removeDamage(healAmount);

            if (healAmount > 0) {
                damageHistory.remove(0);
            }
        }
    }

    /**
     * Mark the user as having died.
     */
    public void death() {
        damageHistory = new ArrayList<>();
    }

    /**
     * Get ordered history of damage which is still applicable to user.
     *
     * @return damage of user
     */
    public List<DamageRecord> getDamageHistory() {
        return damageHistory;
    }
}