package com.khomenko.demo.repository;

import com.khomenko.demo.domain.Game;

import java.util.*;


/**
 * An implementation of the GameRepository interface that manages game data in a HashSet.
 */
public class GameRepositoryImpl implements GameRepository {

    /**
     * A HashSet to store game objects in-memory.
     */
    Set<Game> gameSet = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Game save(Game game) {
        gameSet.add(game);
        return game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Game game) {
        gameSet.remove(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        gameSet.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Game> findGame(Game game) {
        return gameSet.stream().filter(gameTemp -> gameTemp.equals(game)).findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Game> findAll() {
        return new ArrayList<>(gameSet);
    }
}
