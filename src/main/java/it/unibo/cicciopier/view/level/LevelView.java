package it.unibo.cicciopier.view.level;

import it.unibo.cicciopier.controller.Engine;
import it.unibo.cicciopier.model.settings.DeveloperMode;
import it.unibo.cicciopier.model.blocks.base.Block;
import it.unibo.cicciopier.model.entities.Player;
import it.unibo.cicciopier.model.entities.base.Entity;
import it.unibo.cicciopier.model.settings.Screen;
import it.unibo.cicciopier.view.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Custom {@link JPanel} class for game rendering.
 */
public class LevelView extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelView.class);
    private final Engine engine;
    private final GameCam cam;
    private Texture background;

    /**
     * Constructor for this class.
     *
     * @param engine the instance of the engine
     */
    public LevelView(final Engine engine) {
        this.engine = engine;
        this.cam = new GameCam();
        this.background = null;
    }

    /**
     * Load panel components.
     */
    public void load() {
        // Setup cam
        this.cam.setViewportWidth((int) this.getPreferredSize().getWidth());
        this.cam.setViewportHeight((int) this.getPreferredSize().getHeight());
        this.cam.setOffsetMax(Screen.scale(this.engine.getWorld().getWidth() * Block.SIZE) - this.cam.getViewportWidth());
        this.cam.setOffsetMin(0);
        // Setup panel
        this.setBounds(0, 0, (int) this.getPreferredSize().getWidth(), (int) this.getPreferredSize().getHeight());
        this.setLayout(null);
        // Get background texture
        try {
            this.background = Texture.valueOf(this.engine.getWorldLoader().getBackground());
        } catch (IllegalArgumentException | NullPointerException e) {
            LOGGER.error("Invalid background id {}, ignoring it...", this.engine.getWorldLoader().getBackground());
            // Set background color if no valid background has been declared
            this.setBackground(Color.CYAN);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Player p = this.engine.getWorld().getPlayer();
        this.cam.translate(p, g);
        // render background if exists
        if (this.background != null) {
            final int w = (int) (this.background.getTexture().getWidth() * this.getPreferredSize().getHeight() / this.background.getTexture().getHeight());
            for (int x = 0; x < Screen.scale(this.engine.getWorld().getWidth() * Block.SIZE); x += w) {
                g.drawImage(this.background.getTexture(), x, 0, w, (int) this.getPreferredSize().getHeight(), null);
            }
        }
        // render blocks
        for (Block b : this.engine.getWorld()) {
            b.getView().render(g);
        }
        // render entities
        for (Entity e : this.engine.getWorld().getEntities()) {
            if (e.getView() != null) {
                e.getView().render(g);
            } else if (DeveloperMode.isActive()) {
                // Render even if the view is null - developing purposes
                g.setColor(Color.RED);
                g.drawRect(Screen.scale(e.getPos().getX()), Screen.scale(e.getPos().getY()), Screen.scale(e.getWidth() - 1), Screen.scale(e.getHeight() - 1));
            }
        }
        // render player
        if (p.getView() != null) {
            p.getView().render(g);
        } else if (DeveloperMode.isActive()) {
            // Render even if the view is null - developing purposes
            g.setColor(Color.BLACK);
            g.drawRect(Screen.scale(p.getPos().getX()), Screen.scale(p.getPos().getY()), Screen.scale(p.getWidth() - 1), Screen.scale(p.getHeight() - 1));
        }
        // dispose
        g.dispose();
    }

}
