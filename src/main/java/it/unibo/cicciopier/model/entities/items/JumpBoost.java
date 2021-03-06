package it.unibo.cicciopier.model.entities.items;

import it.unibo.cicciopier.controller.AudioController;
import it.unibo.cicciopier.controller.GameLoop;
import it.unibo.cicciopier.model.Sound;
import it.unibo.cicciopier.model.World;
import it.unibo.cicciopier.model.entities.Score;
import it.unibo.cicciopier.model.entities.base.EntityType;
import it.unibo.cicciopier.view.GameObjectView;
import it.unibo.cicciopier.view.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a jump boost
 */
public final class JumpBoost extends SimpleItem implements Boost {
    private static final Logger LOGGER = LoggerFactory.getLogger(JumpBoost.class);
    private static final int DURATION = 10 * GameLoop.TPS;
    private static final int BOOST_STRENGTH = 3;
    private boolean active;
    private long startOfBoost;

    /**
     * Constructor for this class
     *
     * @param world The game's world
     */
    public JumpBoost(final World world) {
        super(EntityType.JUMP_BOOST, world, Texture.JUMP_BOOST);
        this.active = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(final long ticks) {
        super.tick(ticks);
        if (ticks - this.startOfBoost >= JumpBoost.DURATION && isActive()) {
            this.getWorld().getPlayer().setJumpModifier(-JumpBoost.BOOST_STRENGTH);
            LOGGER.info("End of jump boost");
            this.remove();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPickup(final long ticks) {
        if (!this.active) {
            AudioController.getInstance().playSound(Sound.BOOST_PICKUP);
            //activate the boost
            this.active = true;
            this.startOfBoost = ticks;
            this.getWorld().getPlayer().addScore(Score.BOOST);
            this.getWorld().getPlayer().setJumpModifier(JumpBoost.BOOST_STRENGTH);
            LOGGER.info("Jump boost Activated");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
        return this.active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameObjectView getView() {
        if(this.isActive()) {
            return null;
        }
        return super.getView();
    }
}
