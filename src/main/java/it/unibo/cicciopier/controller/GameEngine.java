package it.unibo.cicciopier.controller;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import it.unibo.cicciopier.controller.menu.MenuController;
import it.unibo.cicciopier.model.GameWorld;
import it.unibo.cicciopier.model.Level;
import it.unibo.cicciopier.model.Music;
import it.unibo.cicciopier.model.World;
import it.unibo.cicciopier.model.entities.base.Entity;
import it.unibo.cicciopier.model.entities.enemies.boss.Boss;
import it.unibo.cicciopier.view.GameView;
import it.unibo.cicciopier.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Simple implementation of the interface {@link Engine}.
 */
public final class GameEngine implements Engine {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameEngine.class);
    private final MenuController menu;
    private final Level level;
    private final InputController input;
    private final ControllerManager controllers;
    private final WorldLoader loader;
    private final World world;
    private final View view;
    private final Loop loop;
    private GameState state;
    private Music music;
    private long ticks;

    /**
     * Constructor for this class, it instantiates world, world loader, view, loop and game state.
     *
     * @param menu  the menu instance
     * @param level the level to play
     */
    public GameEngine(final MenuController menu, final Level level) {
        this.menu = menu;
        this.level = level;
        this.input = new InputController();
        this.controllers = new ControllerManager();
        this.world = new GameWorld();
        this.loader = new TmxWorldLoader(this.getWorld(), this.level.getFileName());
        this.view = new GameView(this);
        this.loop = new GameLoop(this);
        this.state = GameState.LOADING;
        this.ticks = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws Exception {
        LOGGER.info("Loading game...");
        this.getWorldLoader().load();
        this.view.load();
        // Get sound
        try {
            this.music = Music.valueOf(this.getWorldLoader().getMusic());
        } catch (Exception e) {
            LOGGER.error("Invalid music id {}, ignoring it...", this.getWorldLoader().getMusic());
        }
        // Init controllers
        this.controllers.initSDLGamepad();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void start() {
        LOGGER.info("Starting game...");
        this.getWorldLoader().create();
        this.state = GameState.RUNNING;
        this.getLoop().startLoop();
        this.view.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void pause() {
        if (this.state == GameState.RUNNING) {
            LOGGER.info("Pausing game...");
            this.state = GameState.PAUSED;
        } else if (this.state == GameState.PAUSED) {
            LOGGER.info("Resuming game...");
            this.state = GameState.RUNNING;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void restart() {
        LOGGER.info("Restarting game...");
        this.getWorldLoader().create();
        this.getMusic().ifPresent(m -> AudioController.getInstance().restartMusic());
        this.state = GameState.RUNNING;
        this.ticks = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void stop() {
        LOGGER.info("Stopping game...");
        this.getLoop().stopLoop();
        this.menu.endOfLevel(this.getWorld().getPlayer().getScore(), this.getState(), this.getLevel());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if (this.getState() == GameState.RUNNING) {
            // check if player is dead
            if (this.getWorld().getPlayer().isDead()) {
                this.state = GameState.OVER;
                return;
            }
            // check if player has won
            if (this.getWorld().getPlayer().hasWon()) {
                this.state = GameState.WON;
                return;
            }
            // check if the player killed the boss
            this.getWorld().getEntities().stream()
                    .filter(Entity::isRemoved)
                    .filter(Boss.class::isInstance)
                    .findFirst()
                    .ifPresent(e -> this.getWorld().getPlayer().win());
            // remove entities marked as removed
            this.getWorld().getEntities().stream()
                    .filter(Entity::isRemoved)
                    .forEach(this.getWorld()::removeEntity);
            // update every entity
            this.getWorld().getEntities().forEach(e -> e.tick(this.ticks));
            // update player
            this.getWorld().getPlayer().tick(this.ticks);
            // process keyboard
            this.processInput();
            // update ticks
            this.ticks++;
        }
        // process controllers
        this.processControllers();
        // update view
        this.view.render();
    }

    /**
     * Process the input from keyboard.
     */
    private void processInput() {
        int velX = 0;
        // Handle movements
        if (this.getInput().isPressed(Input.RIGHT)) {
            velX += this.getWorld().getPlayer().getSpeed();
        }
        if (this.getInput().isPressed(Input.LEFT)) {
            velX -= this.getWorld().getPlayer().getSpeed();
        }
        this.getWorld().getPlayer().getVel().setX(velX);
        // Handle jump
        if (this.getInput().isPressed(Input.JUMP)) {
            this.getWorld().getPlayer().jump();
        }
        // Handle attack
        if (this.getInput().isPressed(Input.ATTACK)) {
            this.getWorld().getPlayer().attackNearest();
        }
    }

    /**
     * Process the input from controllers.
     */
    private void processControllers() {
        final ControllerState state = this.controllers.getState(0);
        if (!state.isConnected) {
            return;
        }
        if (this.getState() == GameState.RUNNING) {
            int velX = 0;
            // Handle movements
            if (state.dpadRight || state.leftStickX > 0.5) {
                velX += this.getWorld().getPlayer().getSpeed();
            }
            if (state.dpadLeft || state.leftStickX < -0.5) {
                velX -= this.getWorld().getPlayer().getSpeed();
            }
            this.getWorld().getPlayer().getVel().setX(velX);
            // Handle jump
            if (state.a) {
                this.getWorld().getPlayer().jump();
            }
            // Handle attack
            if (state.x) {
                this.getWorld().getPlayer().attackNearest();
            }
        }
        // Press Start to enter and exit paused menu
        if (state.startJustPressed) {
            this.pause();
        }
        // Press Back to return to menu from paused/over/won menus
        if ((this.getState() == GameState.PAUSED ||
                this.getState() == GameState.OVER ||
                this.getState() == GameState.WON) &&
                state.backJustPressed) {
            this.action(LevelMenuAction.HOME);
        }
        // Press A to restart level in paused/over menus
        if ((this.getState() == GameState.PAUSED ||
                this.getState() == GameState.OVER) &&
                state.aJustPressed) {
            this.action(LevelMenuAction.RESTART);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTicks() {
        return this.ticks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void action(final LevelMenuAction action) {
        switch (action) {
            case RESTART:
                this.restart();
                break;
            case RESUME:
                this.pause();
                break;
            case HOME:
                this.stop();
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Loop getLoop() {
        return this.loop;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameState getState() {
        return this.state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputController getInput() {
        return this.input;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldLoader getWorldLoader() {
        return this.loader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World getWorld() {
        return this.world;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView() {
        return this.view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Level getLevel() {
        return this.level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Music> getMusic() {
        return Optional.ofNullable(this.music);
    }
}
