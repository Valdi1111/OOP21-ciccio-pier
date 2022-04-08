package it.unibo.cicciopier.model.items;

import it.unibo.cicciopier.controller.AudioController;
import it.unibo.cicciopier.controller.GameLoop;
import it.unibo.cicciopier.controller.menu.MainMenuController;
import it.unibo.cicciopier.model.Sound;
import it.unibo.cicciopier.model.World;
import it.unibo.cicciopier.model.entities.base.EntityType;
import it.unibo.cicciopier.model.entities.base.SimpleMovingEntity;
import it.unibo.cicciopier.view.GameObjectView;
import it.unibo.cicciopier.view.items.CoinView;
import it.unibo.cicciopier.view.items.JumpBoostView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a coin object
 */
public class JumpBoost extends SimpleMovingEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(JumpBoost.class);
    private final Item jumpBoost;
    private JumpBoostView jumpBoostView;
    private boolean isActive;
    private int duration = 10 * GameLoop.TPS;

    /**
     * Constructor for this class
     *
     * @param world The game's world
     */
    public JumpBoost(final World world) {
        super(EntityType.JUMP_BOOST, world);
        this.jumpBoost = Item.JUMP_BOOST;
        this.jumpBoostView = new JumpBoostView(this);
        this.isActive = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick() {

        if (this.checkCollision(this.getWorld().getPlayer()) && !this.isActive) {
            AudioController.getAudioController().playSound(Sound.ITEM);
            //remove the boost view
            this.jumpBoostView = null;
            //activate the boost
            this.isActive = true;
            this.getWorld().getPlayer().setJumpModifier(jumpBoost.getBoost());
            LOGGER.info("Boost Activated");

        }
        if(this.isActive){
            this.duration--;
            LOGGER.info("duration remaining in tick:" + duration);
        }
        if (duration == 0){
            this.getWorld().getPlayer().setJumpModifier(-jumpBoost.getBoost());
            LOGGER.info("End of the boost");
            this.remove();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameObjectView getView() {
        return this.jumpBoostView;
    }

}