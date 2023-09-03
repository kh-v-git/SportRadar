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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class GameServiceImplTest {
    private static final String TEAM_A = "Team A";
    private static final String TEAM_B = "Team B";
    private static final String TEAM_C = "Team C";
    private static final String TEAM_D = "Team D";
    private static final LocalDateTime localDateTime = LocalDateTime.of(2023, Month.AUGUST, 28, 14, 33, 48);
    private static final int startScore = 0;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    public void bothTeamsDoNotExistInSameGame() {
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
    public void homeTeamHasActiveGame() {
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
                .startGameTime(localDateTime) // Game started.
                .build();
        List<Game> gameList = List.of(game, gameRunning);

        when(gameRepository.findAll()).thenReturn(gameList);

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.startGame(game));
    }

    @Test
    public void awayTeamHasActiveGame() {
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
                .startGameTime(localDateTime) // Game started.
                .build();
        List<Game> gameList = List.of(game, gameRunning);

        when(gameRepository.findAll()).thenReturn(gameList);

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.startGame(game));
    }

    @Test
    public void gameWithCurrentTeamsAlreadyStarted() {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(localDateTime) // Game has started
                .build();

        when(gameRepository.findGame(game)).thenReturn(Optional.of(game));

        // Act and Assert
        assertThrows(CustomBusinessException.class, () -> gameService.startGame(game));
    }

    @Test
    public void gameWithCurrentTeamsNotStarted() throws CustomBusinessException {
        // Arrange
        Team homeTeam = Team.builder().countryOfOrigin(TEAM_A).build();
        Team awayTeam = Team.builder().countryOfOrigin(TEAM_B).build();

        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startGameTime(null) // Game has not started yet
                .build();

        when(gameRepository.findGame(any())).thenReturn(Optional.of(game));

        // Act
        Game result = gameService.startGame(game);

        // Assert
        assertNotNull(result); // Game should be returned
        assertEquals(startScore, result.getHomeTeamScore());
        assertEquals(startScore, result.getAwayTeamScore());
        assertNotNull(result.getStartGameTime()); // Start time should be set
        verify(gameRepository).save(result); // Game should be saved with updated start time
    }
}
