package it.unibo.cicciopier.model.entities.base;

import it.unibo.cicciopier.model.World;
import it.unibo.cicciopier.model.entities.EntityState;
import it.unibo.cicciopier.utility.Vector2d;

/**
 * Class that abstracts a living entity, affected by both gravity and death
 */
public abstract class SimpleLivingEntity extends SimpleMovingEntity implements LivingEntity {
    private static final int MAX_GRAVITY = 20;
    private static final int JUMP_FORCE = 16;
    private static final int MAX_TIME = 35;

    private final Vector2d gravity;
    private final int maxHp;
    private int hp;
    private boolean ground;
    private boolean dead;
    private boolean isReady;
    private boolean facingRight;
    private int time;
    private EntityState oldState;
    private EntityState currentState;

    /**
     * Constructor for this class
     *
     * @param type  The Entity's type
     * @param world The game's world
     */
    protected SimpleLivingEntity(final EntityType type, final World world) {
        super(type, world);
        this.ground = false;
        this.maxHp = this.getType().getMaxHp();
        this.hp = this.maxHp;
        this.dead = false;
        this.gravity = new Vector2d(0, 1);
        this.isReady = true;
        this.facingRight = true;
        this.currentState = EntityState.IDLE;
        this.oldState = EntityState.IDLE;
        this.time = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHp() {
        return this.hp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxHp() {
        return this.maxHp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void damage(final int amount) {
        this.hp -= amount;
        if (this.hp <= 0) {
            this.hp = 0;
            this.die();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void heal(final int amount) {
        this.hp += amount;
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFacingRight() {
        return this.facingRight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFacingRight(final boolean bool) {
        this.facingRight = bool;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDead() {
        return this.dead;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void die() {
        this.setCurrentState(EntityState.DEAD);
        this.dead = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getJumpForce() {
        return JUMP_FORCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean jump() {
        if (this.isReady && this.ground) {
            this.setCurrentState(EntityState.JUMPING);
            this.getVel().setY(-this.getJumpForce());
            this.isReady = false;
            this.time = 0;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityState getOldState() {
        return this.oldState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityState getCurrentState() {
        return this.currentState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentState(final EntityState state) {
        if (this.currentState == EntityState.IDLE || state == EntityState.DEAD) {
            this.currentState = state;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetCurrentState(final EntityState state) {
        this.currentState = state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(final long ticks) {
        this.oldState = this.getCurrentState();
        if (this.time >= MAX_TIME) {
            //is ready to jump
            this.isReady = true;
        } else {
            //update the time
            this.time++;
        }
    }

    /**
     * What to do when entity collides
     *
     * @param collision type of collision
     */
    protected void onCollision(final Collision collision) {
        if (collision == Collision.FALLING) {
            this.die();
        }
    }

    /**
     * Method that check collisions and moves the entity
     */
    protected void move() {
        if (this.getVel().getX() > 0) {
            this.facingRight = true;
            //check right collision
            final int rightOffset = this.rightCollision();
            if (rightOffset == 0) {
                this.getVel().setX(0);
                this.onCollision(Collision.COLLIDING_RIGHT);
            } else if (rightOffset > 0) {
                this.getVel().setX(rightOffset);
                this.onCollision(Collision.NEAR_COLLIDING_RIGHT);
            }
        } else if (this.getVel().getX() < 0) {
            this.facingRight = false;
            //check left collision
            final int leftOffset = this.leftCollision();
            if (leftOffset == 0) {
                this.getVel().setX(0);
                this.onCollision(Collision.COLLIDING_LEFT);
            } else if (leftOffset < 0) {
                this.getVel().setX(leftOffset);
                this.onCollision(Collision.NEAR_COLLIDING_LEFT);
            }
        }
        if (this.getVel().getY() > 0) {
            //check bottom collision
            final int bottomOffset = this.bottomCollision();
            if (bottomOffset == 0) {
                this.getVel().setY(0);
                this.ground = true;
                if (this.getCurrentState() == EntityState.JUMPING) {
                    this.resetCurrentState(EntityState.IDLE);
                }
                this.onCollision(Collision.COLLIDING_DOWN);
            } else if (bottomOffset > 0) {
                this.getVel().setY(bottomOffset);
                this.onCollision(Collision.NEAR_COLLIDING_DOWN);
            } else if (bottomOffset == -1) {
                this.ground = false;
            } else if (bottomOffset == -2) {
                this.onCollision(Collision.FALLING);
            }
        } else if (this.getVel().getY() < 0) {
            //check up collision
            final int upOffset = this.upCollision();
            if (upOffset == 0) {
                this.getVel().setY(0);
                this.onCollision(Collision.COLLIDING_UP);
            } else if (upOffset < 0) {
                this.getVel().setY(upOffset);
                this.onCollision(Collision.NEAR_COLLIDING_UP);
            }
            this.ground = false;
        }
        this.getPos().add(this.getVel());
        //add gravity to the entity
        if (this.getVel().getDoubleY() < SimpleLivingEntity.MAX_GRAVITY) {
            this.getVel().add(gravity);
            //check bottom collision
            if (this.bottomCollision() == 0) {
                this.getVel().setY(0);
                this.ground = true;
                if (this.getCurrentState() == EntityState.JUMPING) {
                    this.resetCurrentState(EntityState.IDLE);
                }
                this.onCollision(Collision.COLLIDING_DOWN);
            }
        }
    }
}
