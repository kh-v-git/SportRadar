package com.khomenko.demo.repository;

import com.khomenko.demo.domain.Game;

import java.util.List;
import java.util.Optional;


/**
 * An interface for managing Game data in in-memory repository.
 */
public interface GameRepository {

    /**
     * Saves a game to the repository.
     *
     * @param game The game to be saved.
     * @return The saved game.
     */
    Game save(Game game);

    /**
     * Deletes a game from the repository.
     *
     * @param game The game to be deleted.
     */
    void delete(Game game);

    /**
     * Deletes all games from the repository.
     */
    void deleteAll();

    /**
     * Searches for a specific game in the repository.
     *
     * @param game The game to search for.
     * @return An optional containing the found game, or an empty optional if not found.
     */
    Optional<Game> findGame(Game game);

    /**
     * Retrieves a list of all games stored in the repository.
     *
     * @return A list of all games.
     */
    List<Game> findAll();
}
