package it.unibo.cicciopier.model.entities.base;

import it.unibo.cicciopier.model.entities.Stamina;

/**
 * Represents a type of Entity with its details
 */
public enum EntityType {
    /**
     * Represents the Player
     */
    PLAYER(32, 64, 1000, 60),
    /**
     * Represents the Missile that the enemy will launch
     */
    MISSILE(10, 10, 0, 80),
    /**
     * Represents a meteor from boss attack
     */
    METEOR(64, 64, 0, 60),
    /**
     * Represents the laser that the Boss will shoot
     */
    LASER(0, 0, 0, 10),
    /**
     * Represents a coin
     */
    COIN(20, 20, 0, 0),
    /**
     * Represents a fried chicken, an unhealthy food
     */
    CHICKEN(20, 20, 0, Stamina.JUNK_FOOD_DAMAGE),
    /**
     * Represents potatoes chips, an unhealthy food
     */
    POTATOES(20, 20, 0, Stamina.JUNK_FOOD_DAMAGE),
    /**
     * Represents a burger, an unhealthy food
     */
    BURGER(20, 20, 0, Stamina.JUNK_FOOD_DAMAGE),
    /**
     * Represents jumping power-up
     */
    JUMP_BOOST(14, 32, 0, 0),
    /**
     * Represents jumping power-up
     */
    SPEED_BOOST(14, 32, 0, 0),
    /**
     * Represents jumping power-up
     */
    INVULNERABILITY_BOOST(14, 32, 0, 0),
    /**
     * Represents an explosion
     */
    EXPLOSION(64, 64, 0, 0),
    /**
     * Represents a bite attack
     */
    BITE(128, 128, 0, 0),
    /**
     * Represents a ShootingPea
     */
    SHOOTING_PEA(32, 64, 130, 100),
    /**
     * Represents a NinjaPotato
     */
    NINJA_POTATO(64, 64, 120, 100),
    /**
     * Represents a RollingPeach
     */
    ROLLING_PEACH(64, 64, 120, 100),
    /**
     * Represents a CryingPotato
     */
    CRYING_ONION(64, 64, 60, 100),
    /**
     * Represents a MindPineapple
     */
    MIND_PINEAPPLE(64, 96, 240, 100),
    /**
     * Represents a Nut
     */
    NUT(32, 32, 0, 450),
    /**
     * Represents a Pea
     */
    PEA(16, 16, 0, 200),
    /**
     * Represents a Slash
     */
    SLASH(16, 32, 0, 150),
    /**
     * Represents a Spikes
     */
    SPIKES(32, 64, 0, 300),
    /**
     * Represents the boss of the game
     */
    BROCCOLI(96, 320, 2000, 10);

    private final int width;
    private final int height;
    private final int maxHp;
    private final int attackDamage;

    /**
     * Entity's types constructor
     *
     * @param width        The Entity's width
     * @param height       The Entity's height
     * @param maxHp        The Entity's total hp
     * @param attackDamage The Entity's attack damage
     */
    EntityType(final int width, final int height, final int maxHp, final int attackDamage) {
        this.width = width;
        this.height = height;
        this.maxHp = maxHp;
        this.attackDamage = attackDamage;
    }

    /**
     * Returns the Entity width
     *
     * @return Entity's width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Returns the Entity height
     *
     * @return Entity's height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Returns the Entity total hp
     *
     * @return Entity's maximum hp
     */
    public int getMaxHp() {
        return this.maxHp;
    }

    /**
     * Returns the Entity attack damage
     *
     * @return Entity's damage
     */
    public int getAttackDamage() {
        return this.attackDamage;
    }
}
