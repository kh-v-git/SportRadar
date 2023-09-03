package com.khomenko.demo.service;

import com.khomenko.demo.domain.Game;
import com.khomenko.demo.domain.Team;
import com.khomenko.demo.repository.GameRepository;
import com.khomenko.demo.utils.exception.CustomBusinessException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class GameServiceImplTest {
    private static final String TEAM_A = "Team A";
    private static final String TEAM_B = "Team B";
    private static final String TEAM_C = "Team C";
    private static final String TEAM_D = "Team D";
    private static final LocalDateTime GAME_START_TIME = LocalDateTime.of(2023, Month.AUGUST, 28, 14, 33, 48);
    private static final LocalDateTime GAME_END_TIME = LocalDateTime.of(2023, Month.AUGUST, 28, 15, 33, 48);
    private static final LocalDateTime BEFORE_START_GAME_TIME = LocalDateTime.of(2023, Month.AUGUST, 28, 13, 33, 48);
    private static final int START_SCORE = 0;
    private static final int UPDATE_SCORE = 10;
    private static final int NEGATIVE_HOME_TEAM_SCORE = -1;
    private static final int NEGATIVE_AWAY_TEAM_SCORE = -1;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    //startGame()
    @Test
    public void bothTeamsDoNotExistInSameGameToStartGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.startGame(game));
    }

    @Test
    public void homeTeamHasActiveGameToStartGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Team teamC = Team.builder().countryOfOrigin(TEAM_C).build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(null) // Game isn't started. Game to be started
                .build();
        Game gameRunning = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(teamC)
                .startGameTime(GAME_START_TIME) // Game started.
                .build();
        List<Game> gameList = List.of(game, gameRunning);

        when(gameRepository.findAll()).thenReturn(gameList);

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.startGame(game));
    }

    @Test
    public void awayTeamHasActiveGameToStartGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Team teamD = Team.builder().countryOfOrigin(TEAM_D).build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(null) // Game isn't started. Game to be started
                .build();
        Game gameRunning = Game.builder()
                .homeTeam(teamD)
                .awayTeam(awayTeam)
                .startGameTime(GAME_START_TIME) // Game started.
                .build();
        List<Game> gameList = List.of(game, gameRunning);

        when(gameRepository.findAll()).thenReturn(gameList);

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.startGame(game));
    }

    @Test
    public void gameWithCurrentTeamsAlreadyStartedToStartGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(GAME_START_TIME) // Game has started
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(game));

        // Act and Assert
        assertNotNull(game.getStartGameTime());
        assertThrows(CustomBusinessException.class, () -> gameService.startGame(game));
    }

    @Test
    public void gameWithCurrentTeamsNotStartedToStartGame() throws CustomBusinessException {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(null) // Game has not started yet
                .build();
        when(gameRepository.findGame(any())).thenReturn(Optional.of(game));
        when(gameRepository.save(game)).thenReturn(game);

        // Act
        Game result = gameService.startGame(game);

        // Assert
        assertNotNull(result); // Game should be returned
        assertEquals(START_SCORE, result.getHomeTeamScore());
        assertEquals(START_SCORE, result.getAwayTeamScore());
        assertNotNull(result.getStartGameTime()); // Start time should be set
        verify(gameRepository, times(1)).save(result); // Game should be saved with updated start time
    }

    //finishGame()
    @Test
    public void gameNotExistsToFinishGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(GAME_START_TIME) // Game has started
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.finishGame(game));
    }

    @Test
    public void gameNotStartedToFinishGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(null) // Game not started
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(game));

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.finishGame(game));
    }

    @Test
    public void finishedTimeBeforeStartTimeToFinishGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_A).build();

        Game gameSaved = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .awayTeamScore(START_SCORE)
                .startGameTime(GAME_START_TIME) // Game started
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(GAME_START_TIME) // Game started
                .endGameTime(BEFORE_START_GAME_TIME)
                .build();

        when(gameRepository.findGame(gameSaved)).thenReturn(Optional.of(gameSaved));

        // Act
        try {
            gameService.finishGame(game);
        } catch (CustomBusinessException ignored) {
            // Act and Assert
            assertThrows(CustomBusinessException.class, () -> gameService.finishGame(game));
        }
    }

    @Test
    public void negativeGameScoreToFinishGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game gameSaved = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .awayTeamScore(START_SCORE)
                .startGameTime(GAME_START_TIME) // Game started
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(NEGATIVE_HOME_TEAM_SCORE)
                .awayTeamScore(NEGATIVE_AWAY_TEAM_SCORE)
                .startGameTime(GAME_START_TIME) // Game started
                .build();

        when(gameRepository.findGame(gameSaved)).thenReturn(Optional.of(gameSaved));


        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.finishGame(game));

    }

    @Test
    public void gameAlreadyFinishedToFinishGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(GAME_START_TIME) // Game started
                .endGameTime(GAME_END_TIME) // Game already finished
                .build();


        when(gameRepository.findGame(game)).thenReturn(Optional.of(game));

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.finishGame(game));
    }

    @Test
    public void gameExistsAndStartedToFinishGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game gameSaved = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .awayTeamScore(START_SCORE)
                .startGameTime(GAME_START_TIME) // Game started
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .awayTeamScore(START_SCORE)
                .startGameTime(GAME_START_TIME) // Game started
                .endGameTime(GAME_END_TIME) // Game finished
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(gameSaved));
        when(gameRepository.save(gameSaved)).thenReturn(gameSaved);

        // Act
        Game result = gameService.finishGame(game);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getEndGameTime());
        verify(gameRepository, times(1)).save(result); // Verify endGameTime is updated
    }

    //updateScore()
    @Test
    public void gameNotExistsToUpdateScore() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .homeTeamScore(START_SCORE)
                .startGameTime(GAME_START_TIME)
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.updateScore(game));
    }

    @Test
    public void gameNotStartedToUpdateScore() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .homeTeamScore(START_SCORE)
                .startGameTime(null) // Game not started
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(game));

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.updateScore(game));
    }

    @Test
    public void gameFinishedToUpdateScore() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .homeTeamScore(START_SCORE)
                .startGameTime(GAME_START_TIME) // Game started
                .endGameTime(GAME_END_TIME) // Game already finished
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(game));

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.updateScore(game));
    }

    @Test
    public void negativeGameScoreToUpdateScore() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game gameSaved = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .awayTeamScore(START_SCORE)
                .startGameTime(GAME_START_TIME) // Game started
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(NEGATIVE_HOME_TEAM_SCORE)
                .awayTeamScore(NEGATIVE_AWAY_TEAM_SCORE)
                .startGameTime(GAME_START_TIME) // Game started
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(gameSaved));

        assertThrows(CustomBusinessException.class, () -> gameService.updateScore(game));
    }

    @Test
    public void updateValidScoreToUpdateScore() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Game gameSaved = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(START_SCORE)
                .awayTeamScore(START_SCORE)
                .startGameTime(GAME_START_TIME)
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(UPDATE_SCORE)
                .awayTeamScore(UPDATE_SCORE)
                .startGameTime(GAME_START_TIME)
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(gameSaved));
        when(gameRepository.save(gameSaved)).thenReturn(gameSaved);

        // Act
        Game result = gameService.updateScore(game);

        // Assert
        assertNotNull(result);
        assertEquals(UPDATE_SCORE, result.getHomeTeamScore());
        assertEquals(UPDATE_SCORE, result.getAwayTeamScore());
        verify(gameRepository, times(1)).save(game); // Verify endGameTime is updated
    }

    @Test
    public void noStartedGamesNoGamesInSummary() {
        // Arrange
        Team homeTeamA = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeamB = Team.builder().countryOfOrigin(TEAM_B).build();
        Team homeTeamC = Team.builder().countryOfOrigin(TEAM_C).build();
        Team awayTeamD = Team.builder().countryOfOrigin(TEAM_D).build();

        Game game1 = Game.builder()
                .homeTeam(homeTeamA)
                .awayTeam(awayTeamB)
                .homeTeamScore(START_SCORE)
                .awayTeamScore(START_SCORE)
                .build();

        Game game2 = Game.builder()
                .homeTeam(homeTeamC)
                .awayTeam(awayTeamD)
                .homeTeamScore(START_SCORE)
                .awayTeamScore(START_SCORE)
                .build();

        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));

        // Act and Assert
        assertEquals(Collections.emptyList(), gameService.getSummaryOfAllGames());
    }

    @Test
    public void startedGamesReturnGamesSummary() {
        // Arrange
        Team teamA = Team.builder().countryOfOrigin(TEAM_A).build();
        Team teamB = Team.builder().countryOfOrigin(TEAM_B).build();
        Team teamC = Team.builder().countryOfOrigin(TEAM_C).build();
        Team teamD = Team.builder().countryOfOrigin(TEAM_D).build();

        Game game1 = Game.builder()
                .homeTeam(teamA)
                .awayTeam(teamB)
                .homeTeamScore(0)
                .awayTeamScore(3)
                .startGameTime(GAME_START_TIME)
                .endGameTime(GAME_END_TIME)
                .build();

        Game game2 = Game.builder()
                .homeTeam(teamC)
                .awayTeam(teamD)
                .homeTeamScore(3)
                .awayTeamScore(2)
                .startGameTime(BEFORE_START_GAME_TIME)
                .endGameTime(GAME_START_TIME)
                .build();

        Game game3 = Game.builder()
                .homeTeam(teamA)
                .awayTeam(teamD)
                .homeTeamScore(3)
                .awayTeamScore(2)
                .startGameTime(GAME_END_TIME)
                .build();

        when(gameRepository.findAll()).thenReturn(List.of(game1, game2, game3));

        // Act and Assert
        assertEquals(List.of(game3, game2, game1), gameService.getSummaryOfAllGames());
    }
}
