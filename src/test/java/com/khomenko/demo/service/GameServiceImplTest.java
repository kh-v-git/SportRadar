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
    private static final LocalDateTime gameStartTime = LocalDateTime.of(2023, Month.AUGUST, 28, 14, 33, 48);
    private static final LocalDateTime gameEndTime = LocalDateTime.of(2023, Month.AUGUST, 28, 15, 33, 48);
    private static final LocalDateTime beforeStartGameEndTime = LocalDateTime.of(2023, Month.AUGUST, 28, 13, 33, 48);
    private static final int startScore = 0;
    private static final int updateScore = 10;
    private static final int negativeHomeTeamScore = -1;
    private static final int negativeAwayTeamScore = -1;

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
                .startGameTime(gameStartTime) // Game started.
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
                .startGameTime(gameStartTime) // Game started.
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
                .startGameTime(gameStartTime) // Game has started
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
        assertEquals(startScore, result.getHomeTeamScore());
        assertEquals(startScore, result.getAwayTeamScore());
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
                .startGameTime(gameStartTime) // Game has started
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
                .homeTeamScore(startScore)
                .awayTeamScore(startScore)
                .startGameTime(gameStartTime) // Game started
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(gameStartTime) // Game started
                .endGameTime(beforeStartGameEndTime)
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
                .homeTeamScore(startScore)
                .awayTeamScore(startScore)
                .startGameTime(gameStartTime) // Game started
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(negativeHomeTeamScore)
                .awayTeamScore(negativeAwayTeamScore)
                .startGameTime(gameStartTime) // Game started
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
    public void gameAlreadyFinishedToFinishGame() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(gameStartTime) // Game started
                .endGameTime(gameEndTime) // Game already finished
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
                .homeTeamScore(startScore)
                .awayTeamScore(startScore)
                .startGameTime(gameStartTime) // Game started
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(startScore)
                .awayTeamScore(startScore)
                .startGameTime(gameStartTime) // Game started
                .endGameTime(gameEndTime) // Game finished
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
                .homeTeamScore(startScore)
                .homeTeamScore(startScore)
                .startGameTime(gameStartTime)
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
                .homeTeamScore(startScore)
                .homeTeamScore(startScore)
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
                .homeTeamScore(startScore)
                .homeTeamScore(startScore)
                .startGameTime(gameStartTime) // Game started
                .endGameTime(gameEndTime) // Game already finished
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
                .homeTeamScore(startScore)
                .awayTeamScore(startScore)
                .startGameTime(gameStartTime) // Game started
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(negativeHomeTeamScore)
                .awayTeamScore(negativeAwayTeamScore)
                .startGameTime(gameStartTime) // Game started
                .build();

        when(gameRepository.findGame(gameSaved)).thenReturn(Optional.of(gameSaved));

        // Act
        try {
            gameService.updateScore(game);
        } catch (CustomBusinessException ignored) {
            // Act and Assert
            assertThrows(CustomBusinessException.class, () -> gameService.updateScore(game));
        }
    }

    @Test
    public void updateValidScoreToUpdateScore() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Game gameSaved = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(startScore)
                .homeTeamScore(startScore)
                .startGameTime(gameStartTime)
                .build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(updateScore)
                .homeTeamScore(updateScore)
                .startGameTime(gameStartTime)
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(gameSaved));
        when(gameRepository.save(game)).thenReturn(game);

        // Act
        Game result = gameService.updateScore(game);

        // Assert
        assertNotNull(result);
        assertEquals(updateScore, result.getHomeTeamScore());
        assertEquals(updateScore, result.getAwayTeamScore());
        verify(gameRepository, times(1)).save(game); // Verify endGameTime is updated
    }
}
