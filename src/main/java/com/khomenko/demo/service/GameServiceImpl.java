package com.khomenko.demo.service;

import com.khomenko.demo.domain.Game;
import com.khomenko.demo.repository.GameRepository;
import com.khomenko.demo.utils.exception.CustomBusinessException;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


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

        return gameRepository.save(savedGame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Game finishGame(@NonNull Game game) throws CustomBusinessException {
        Game savedGame = gameRepository.findGame(game).orElseThrow(() -> new CustomBusinessException(String.format("Game is missing. Failed to finish game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam())));

        if (savedGame.getStartGameTime() == null)
            throw new CustomBusinessException(String.format("Game is not started. Failed to finish game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam()));

        if (savedGame.getEndGameTime() != null)
            throw new CustomBusinessException(String.format("Game is already finished. Failed to finish game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam()));

        if (game.getEndGameTime().isBefore(savedGame.getStartGameTime()))
            throw new CustomBusinessException(String.format("Game cannot be finished before started time. Failed to finish game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam()));

        if (game.getHomeTeamScore() < 0 || game.getAwayTeamScore() < 0)
            throw new CustomBusinessException(String.format("Game cannot be finished with negative scores. Failed to finish game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam()));

        savedGame.setEndGameTime(LocalDateTime.now());
        savedGame.setHomeTeamScore(game.getHomeTeamScore());
        savedGame.setAwayTeamScore(game.getAwayTeamScore());

        return gameRepository.save(savedGame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Game updateScore(@NonNull Game game) throws CustomBusinessException {
        Game savedGame = gameRepository.findGame(game).orElseThrow(() -> new CustomBusinessException(String.format("Game is missing. Failed to update score for game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam())));

        if (savedGame.getStartGameTime() == null)
            throw new CustomBusinessException(String.format("Game is not started. Failed to update score for game with teams:  %s, %s.", game.getHomeTeam(), game.getAwayTeam()));

        if (savedGame.getEndGameTime() != null)
            throw new CustomBusinessException(String.format("Game is already finished. Failed to update score for game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam()));

        if (game.getHomeTeamScore() < 0 || game.getAwayTeamScore() < 0)
            throw new CustomBusinessException(String.format("Scores to update are negative. Failed to update score for  game with teams: %s, %s.", game.getHomeTeam(), game.getAwayTeam()));

        savedGame.setHomeTeamScore(game.getHomeTeamScore());
        savedGame.setAwayTeamScore(game.getAwayTeamScore());

        return gameRepository.save(savedGame);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Game> getSummaryOfAllGames() throws CustomBusinessException {

        return gameRepository.findAll().stream()
                .filter(game -> game.getStartGameTime() != null)
                .sorted((game1, game2) -> {
                    int scoreDiff = ((game2.getHomeTeamScore() + game2.getAwayTeamScore()) - (game1.getHomeTeamScore() + game1.getAwayTeamScore()));
                    return scoreDiff != 0
                            ? scoreDiff
                            : game2.getStartGameTime().compareTo(game1.getStartGameTime());
                })
                .collect(Collectors.toList());
    }
}
