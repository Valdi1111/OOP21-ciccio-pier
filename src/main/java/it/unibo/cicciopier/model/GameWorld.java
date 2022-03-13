package it.unibo.cicciopier.model;

import it.unibo.cicciopier.model.blocks.BlockFactory;
import it.unibo.cicciopier.model.blocks.SimpleBlockFactory;
import it.unibo.cicciopier.model.blocks.base.Block;
import it.unibo.cicciopier.model.entities.EntityFactory;
import it.unibo.cicciopier.model.entities.Player;
import it.unibo.cicciopier.model.entities.base.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Simple implementation of the interface {@link World}.
 */
public class GameWorld implements World {
    private final EntityFactory entityFactory;
    private final BlockFactory blockFactory;

    private int height;
    private int width;

    private Block[][] blocks;
    private List<Entity> entities;
    private Player player;

    /**
     * Constructor for this class, it instantiates entity and block factories.
     * {@link #setHeight(int)}, {@link #setWidth(int)} and {@link #clear()} must be called after this.
     */
    public GameWorld() {
        // TODO create entity factory instance.
        this.entityFactory = null;
        this.blockFactory = new SimpleBlockFactory();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public EntityFactory getEntityFactory() {
        return this.entityFactory;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public BlockFactory getBlockFactory() {
        return this.blockFactory;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int getHeight() {
        return this.height;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int getWidth() {
        return this.width;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Block getBlock(final int x, final int y) {
        return this.blocks[y][x];
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setBlock(final int x, final int y, final Block block) {
        this.blocks[y][x] = block;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public List<Entity> getEntities() {
        return new ArrayList<>(this.entities);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void addEntity(final Entity entity) {
        this.entities.add(entity);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Player getPlayer() {
        return this.player;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void clear() {
        this.blocks = new Block[this.getHeight()][this.getWidth()];
        this.entities = new ArrayList<>();
        this.player = this.getEntityFactory().createPlayer();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Iterator<Block> iterator() {
        return new BlockIterator();
    }

    /**
     * Simple utility class for the block iterator.
     */
    private class BlockIterator implements Iterator<Block> {
        private int column;
        private int row;

        @Override
        public boolean hasNext() {
            return this.row < GameWorld.this.getHeight() &&
                    (this.column < GameWorld.this.getWidth() || this.row < GameWorld.this.getHeight() - 1);
        }

        @Override
        public Block next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (this.column >= GameWorld.this.blocks[this.row].length) {
                this.column = 0;
                this.row++;
            }
            return GameWorld.this.blocks[this.row][this.column++];
        }
    }

}