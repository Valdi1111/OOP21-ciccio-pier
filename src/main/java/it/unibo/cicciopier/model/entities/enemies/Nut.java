package it.unibo.cicciopier.model.entities.enemies;

import it.unibo.cicciopier.model.World;
import it.unibo.cicciopier.model.entities.base.EntityType;
import it.unibo.cicciopier.model.entities.base.SimpleEntity;
import it.unibo.cicciopier.view.GameObjectView;

public class Nut extends SimpleProjectile {


    /**
     * Constructor for this class
     *
     * @param world The game's world
     */
    public Nut(final World world) {
        super(EntityType.NUT,world,Projectiles.NUT);
    }

    @Override
    public void tick() {
        if (this.checkCollision(this.getWorld().getPlayer())){
            this.remove();
            this.createExplosion();
            this.getWorld().getPlayer().damage(this.getType().getAttackDamage());
        }
    }
}