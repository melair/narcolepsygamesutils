/*
 * GameDirector (statistics) - Copyright © 2013-2016 - Alastria Networks Limited
 * All rights reserved, unauthorised distribution of source code, compiled
 * binary or usage is expressly prohibited.
 */
package games.narcolepsy.minecraft.utils.features.healthtrack;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

/**
 * Record of damage, designed to be serialised for JSON to website.
 */
public class DamageRecord {
    /**
     * Time damage was taken.
     */
    private final long time;
    /**
     * Damage cause.
     */
    private final EntityDamageEvent.DamageCause damageCause;
    /**
     * Amount of damage.
     */
    private double damageAmount;

    /**
     * Entity type which did the damage.
     */
    private final EntityType entityType;
    /**
     * Name of entity which did the damage.
     */
    private final String entityName;
    /**
     * UUID of entity which did damage.
     */
    private final UUID entityUUID;

    /**
     * Construct new damage record.
     *
     * @param damageCause  cause of damage
     * @param damageAmount amount of damage
     * @param entityType   type of entity
     * @param entityName   name of entity
     * @param entityUUID   UUID of entity
     */
    public DamageRecord(EntityDamageEvent.DamageCause damageCause, double damageAmount, EntityType entityType, String entityName, UUID entityUUID) {
        this.time = System.currentTimeMillis();
        this.damageCause = damageCause;
        this.damageAmount = damageAmount;
        this.entityType = entityType;
        this.entityName = entityName;
        this.entityUUID = entityUUID;
    }

    /**
     * Remove damage from record.
     *
     * @param damage damage to remove
     * @return remaining amount of that damage, if any
     */
    public double removeDamage(double damage) {
        double initialDamage = damageAmount;
        damageAmount -= damage;

        return damage - initialDamage;
    }

    /**
     * Get the time the damage was received.
     *
     * @return time damage was received in milliseconds since epoch
     */
    public long getTime() {
        return time;
    }

    /**
     * Get the damage cause.
     *
     * @return cause of damage
     */
    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }

    /**
     * Get the amount of damage.
     *
     * @return amount of damage
     */
    public double getDamageAmount() {
        return damageAmount;
    }

    /**
     * Get the entity type.
     *
     * @return entity type
     */
    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * Get the entities name.
     *
     * @return entities name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Get the entities UUID.
     *
     * @return entities UUID
     */
    public UUID getEntityUUID() {
        return entityUUID;
    }
}
