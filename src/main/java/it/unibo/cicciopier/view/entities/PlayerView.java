package it.unibo.cicciopier.view.entities;

import it.unibo.cicciopier.model.entities.PlayerImpl;
import it.unibo.cicciopier.view.GameObjectView;

import java.awt.*;

public class PlayerView implements GameObjectView {
    private final PlayerImpl player;

    public PlayerView(final PlayerImpl player) {
        this.player = player;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(
                this.player.getPos().getX(),
                this.player.getPos().getY(),
                this.player.getWidth() - 1,
                this.player.getHeight() - 1
        );
    }
}