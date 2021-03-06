package it.unibo.cicciopier.model.entities.enemies;

import it.unibo.cicciopier.model.World;
import it.unibo.cicciopier.model.entities.base.Entity;
import it.unibo.cicciopier.model.entities.base.EntityType;
import it.unibo.cicciopier.model.entities.base.SimpleMovingEntity;
import it.unibo.cicciopier.utility.Vector2d;

import java.util.Optional;

/**
 * Abstract class for Projectiles
 */
public abstract class SimpleProjectile extends SimpleMovingEntity {
    private final int durationTicks;
    private int localTicks;

    /**
     * Constructor for this class
     *
     * @param type          The type of this projectile
     * @param world         The game's world
     * @param durationTicks The duration ticks of this projectile
     */
    protected SimpleProjectile(final EntityType type, final World world, final int durationTicks) {
        super(type, world);
        this.durationTicks = durationTicks;
        this.localTicks = 0;
    }

    /**
     * Method to check if projectile hit something, eventually destroying it
     */
    private void checkCollisionsHit() {
        if (this.getVel().getX() < 0 && this.leftCollision() <= 0 ||
                this.getVel().getX() > 0 && this.rightCollision() >= 0) {
            this.createExplosion();
            this.remove();
        }
    }

    /**
     * Method to check if projectile hit the Player, eventually damaging it
     */
    private void checkPlayerHit() {
        if (this.checkCollision(this.getWorld().getPlayer())) {
            this.getWorld().getPlayer().damage(this.getType().getAttackDamage());
            this.createExplosion();
            this.remove();
        }
    }

    /**
     * Utility method to generate an explosion, needed when the projectile gets destroyed
     */
    protected void createExplosion() {
        Optional<Entity> opt = this.getWorld().getEntityFactory().createEntity(EntityType.EXPLOSION);
        if (opt.isPresent()) {
            Entity e = opt.get();
            e.setPos(this.getPos().addVector(new Vector2d(-e.getWidth() / 2d, -e.getHeight() / 2d)));
            this.getWorld().addEntity(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(final long ticks) {
        this.localTicks++;
        if (this.localTicks > this.durationTicks) {
            this.remove();
        }
        this.getPos().add(this.getVel());
        this.checkPlayerHit();
        this.checkCollisionsHit();
    }

}
