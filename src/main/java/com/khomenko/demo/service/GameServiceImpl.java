package com.khomenko.demo.service;

import com.khomenko.demo.domain.Game;
import com.khomenko.demo.utils.exception.CustomBusinessException;

import java.util.List;


/**
 * An implementation of the GameService interface that provides functionality to manage and interact with game-related operations.
 * This class allows starting, finishing, updating scores, and retrieving summaries of games.
 */
public class GameServiceImpl implements GameService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Game startGame(Game game) throws CustomBusinessException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Game finishGame(Game game) throws CustomBusinessException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Game updateScore(Game game) throws CustomBusinessException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Game> getSummaryOfAllGames() throws CustomBusinessException {
        return null;
    }
}
