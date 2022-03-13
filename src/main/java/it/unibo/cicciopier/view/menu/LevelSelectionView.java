package it.unibo.cicciopier.view.menu;

import it.unibo.cicciopier.controller.Engine;
import it.unibo.cicciopier.view.View;

public class LevelSelectionView implements View {
    private final Engine engine;

    public LevelSelectionView(Engine engine) {
        this.engine = engine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws Exception {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Engine getEngine() {
        return this.engine;
    }

}