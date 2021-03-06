package it.unibo.cicciopier.model.entities.enemies;

import it.unibo.cicciopier.controller.GameLoop;
import it.unibo.cicciopier.model.World;
import it.unibo.cicciopier.model.blocks.base.Block;
import it.unibo.cicciopier.model.entities.base.Collision;
import it.unibo.cicciopier.model.entities.base.Entity;
import it.unibo.cicciopier.model.entities.base.EntityType;
import it.unibo.cicciopier.utility.Vector2d;
import it.unibo.cicciopier.view.GameObjectView;
import it.unibo.cicciopier.view.entities.enemies.RollingPeachView;

import java.util.Optional;

/**
 * Represents the enemy RollingPeach, a walking peach whom attack consists into rolling
 * towards the player.
 */
public class RollingPeach extends SimplePathEnemy {
    public final int ATTACK_RANGE = 4 * Block.SIZE;
    public final double IDLE_DURATION = 1.5* GameLoop.TPS;
    public final double MOVEMENT_SPEED = 1;
    public static final double ROLLING_SPEED = (8.5d * Block.SIZE) / GameLoop.TPS;
    public static final int ANGER_DURATION_TICKS = 40;
    public final int MAX_RIGHT_OFFSET = 5 * Block.SIZE;
    private static final int LOCAL_TICK_COUNT_DELIMITER = 3000;

    private final RollingPeachView view;
    private boolean angered;
    private boolean jumped;
    private boolean attacking;
    private boolean suicidal;
    private int localTicks;

    /**
     * Constructor for this class
     *
     * @param world The game's world
     */
    public RollingPeach(final World world) {
        super(EntityType.ROLLING_PEACH, world);
        this.localTicks = 0;
        this.attacking = false;
        this.jumped = false;
        this.angered = false;
        this.suicidal = false;
        this.view = new RollingPeachView(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameObjectView getView() {
        return this.view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHealValue() {
        if (this.suicidal) {
            return 0;
        }
        return super.getHealValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStaminaValue() {
        if (this.suicidal) {
            return 0;
        }
        return super.getStaminaValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScoreValue() {
        if (this.suicidal) {
            return 0;
        }
        return super.getScoreValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMovementSpeed() {
        return MOVEMENT_SPEED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getIdleDuration() {
        return IDLE_DURATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAttackRange() {
        return ATTACK_RANGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxRightOffset() {
        return MAX_RIGHT_OFFSET;
    }

    /**
     * Utility method used to update the local ticks when needed
     */
    private void updateLocalTicks() {
        if (this.localTicks < LOCAL_TICK_COUNT_DELIMITER) {
            this.localTicks++;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean jump() {
        this.jumped = true;
        return super.jump();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void shoot(final double projectileSpeed, final EntityType type) {
        Optional<Entity> opt = this.getWorld().getEntityFactory().createEntity(type);
        if (opt.isPresent()) {
            SimpleProjectile e = ((SimpleProjectile) opt.get());
            e.setPos(this.getPos().addVector(new Vector2d(0, this.getType().getHeight() - type.getHeight())));
            e.getVel().setX(projectileSpeed);
            this.getWorld().addEntity(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void die() {
        if (!this.suicidal) {
            this.shoot(0, EntityType.NUT);
        }
        super.die();
    }

    /**
     * Method used to make the RollingPeach get angry before attacking
     */
    private void anger() {
        this.resetCurrentState(EnemyState.ANGERED);
        this.updateLocalTicks();
        if (this.localTicks == ANGER_DURATION_TICKS) {
            this.localTicks = 0;
            this.angered = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void attacking() {
        if (!this.angered) {
            this.anger();
            return;
        }
        if (!this.jumped) {
            this.jump();
            return;
        }
        this.resetCurrentState(EnemyState.ANGERED_RUNNING);
        this.getVel().setX(this.isFacingRight() ? ROLLING_SPEED : -ROLLING_SPEED);
        this.move();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notAttacking() {
        //do nothing
    }

    /**
     * Utility method used to spawn an explosion
     */
    private void createExplosion() {
        Optional<Entity> opt = this.getWorld().getEntityFactory().createEntity(EntityType.EXPLOSION);
        if (opt.isPresent()) {
            Entity e = opt.get();
            e.setPos(this.getPos());
            this.getWorld().addEntity(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCollision(final Collision collision) {
        if (this.angered) {
            if ((this.isFacingRight() && collision == Collision.COLLIDING_RIGHT) ||
                    (!this.isFacingRight() && collision == Collision.COLLIDING_LEFT)) {
                this.suicidal = true;
                this.die();
            }
            return;
        }
        super.onCollision(collision);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkPlayerCollision() {
        super.checkPlayerCollision();
        if (this.angered) {
            if (this.getWorld().getPlayer().checkCollision(this)) {
                this.suicidal = true;
                this.die();
                this.createExplosion();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean attackBehaviour() {
        this.checkAttackConditions();
        if (this.isAttacking() && !this.attacking) {
            this.attacking = true;
            this.getVel().setX(0);
        }
        if (attacking) {
            this.attacking();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(final long ticks) {
        super.tick(ticks);
    }
}