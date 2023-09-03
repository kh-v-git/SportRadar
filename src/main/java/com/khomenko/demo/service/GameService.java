package com.khomenko.demo.service;

import com.khomenko.demo.domain.Game;
import com.khomenko.demo.utils.exception.CustomBusinessException;
import org.springframework.lang.NonNull;

import java.util.List;


/**
 * A service interface for managing games.
 */
public interface GameService {

    /**
     * Starts a game with an initial score of 0 - 0.
     *
     * @param game The game to start.
     * @return The started game.
     * @throws CustomBusinessException If an error occurs during game start.
     */
    Game startGame(@NonNull Game game) throws CustomBusinessException;

    /**
     * Finishes a game and removes it from the scoreboard.
     *
     * @param game The game to finish.
     * @return The finished game.
     * @throws CustomBusinessException If an error occurs during game finishing.
     */
    Game finishGame(@NonNull Game game) throws CustomBusinessException;

    /**
     * Updates the score of a game with the provided home team and away team scores.
     *
     * @param game The game to update.
     * @return The updated game.
     * @throws CustomBusinessException If an error occurs during score update.
     */
    Game updateScore(@NonNull Game game) throws CustomBusinessException;

    /**
     * Retrieves a summary of all games by total score. Games with the same total score are grouped together.
     *
     * @return A list of games grouped by total score.
     * @throws CustomBusinessException If an error occurs while retrieving the summary.
     */
    List<Game> getSummaryOfAllGames() throws CustomBusinessException;
}
