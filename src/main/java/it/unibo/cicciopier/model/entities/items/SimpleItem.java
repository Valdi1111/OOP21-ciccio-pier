package it.unibo.cicciopier.model.entities.items;

import it.unibo.cicciopier.model.World;
import it.unibo.cicciopier.model.entities.base.EntityType;
import it.unibo.cicciopier.model.entities.base.SimpleEntity;
import it.unibo.cicciopier.view.GameObjectView;
import it.unibo.cicciopier.view.Texture;
import it.unibo.cicciopier.view.entities.items.StaticItemView;

/**
 * Simple class to reunite all the items
 */
public abstract class SimpleItem extends SimpleEntity implements Item {
    private final GameObjectView view;

    /**
     * Constructor for this class
     *
     * @param world the game's world
     */
    public SimpleItem(final EntityType type, final World world, final Texture texture) {
        super(type, world);
        this.view = new StaticItemView(this, texture);
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
    public void tick(final long ticks) {
        //check if item collides with the player
        if (this.checkCollision(this.getWorld().getPlayer())) {
            this.onPickup(ticks);
        }
    }

}
