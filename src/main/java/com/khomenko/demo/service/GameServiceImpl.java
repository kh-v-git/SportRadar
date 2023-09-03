package com.khomenko.demo.service;

import com.khomenko.demo.domain.Game;
import com.khomenko.demo.repository.GameRepository;
import com.khomenko.demo.utils.exception.CustomBusinessException;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * An implementation of the GameService interface that provides functionality to manage and interact with game-related operations.
 * This class allows starting, finishing, updating scores, and retrieving summaries of games.
 */

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Game startGame(@NonNull Game game) throws CustomBusinessException {
        Game savedGame = gameRepository.findGame(game).orElseThrow(() -> new CustomBusinessException(String.format("Game is missing. Failed to start game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam())));

        if (savedGame.getStartGameTime() != null)
            throw new CustomBusinessException(String.format("Game is started. Failed to start game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam()));

        List<Game> gameList = gameRepository.findAll();

        gameList.remove(game);
        boolean homeTeamHasActiveGame = gameList.stream()
                .filter(gameFilter -> (gameFilter.getHomeTeam().equals(game.getHomeTeam()) || gameFilter.getAwayTeam().equals(game.getHomeTeam())))
                .anyMatch(gameMatcher -> gameMatcher.getStartGameTime() != null);
        if (homeTeamHasActiveGame)
            throw new CustomBusinessException(String.format("Failed to start game. Home team: %s has active match", game.getHomeTeam()));

        boolean awayTeamHasActiveGame = gameList.stream()
                .filter(gameFilter -> (gameFilter.getAwayTeam().equals(game.getAwayTeam()) || gameFilter.getHomeTeam().equals(game.getAwayTeam())))
                .anyMatch(gameMatcher -> gameMatcher.getStartGameTime() != null);
        if (awayTeamHasActiveGame)
            throw new CustomBusinessException(String.format("Failed to start game. Away team: %s has active match", game.getAwayTeam()));

        savedGame.setStartGameTime(LocalDateTime.now());
        savedGame.setHomeTeamScore(0);
        savedGame.setAwayTeamScore(0);
        savedGame.setVisibleOnBoard(true);

        return gameRepository.save(savedGame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Game finishGame(@NonNull Game game) throws CustomBusinessException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Game updateScore(@NonNull Game game) throws CustomBusinessException {
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
