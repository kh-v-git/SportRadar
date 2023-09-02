package com.khomenko.demo.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * Game entity is representation of real game from Football World Cup Score Board
 * <p>
 * Contains basic value validation
 *
 * @param homeTeam       home team object
 * @param awayTeam       away team object
 * @param homeTeamScore  home team score
 * @param awayTeamScore  away team score
 * @param startGameTime  LocalDateTime record about start of the game
 * @param endGameTime    LocalDateTime record about end of the game
 */

@Data
@Builder
public class Game {
    private Team homeTeam;
    private Team awayTeam;
    private int homeTeamScore;
    private int awayTeamScore;
    private LocalDateTime startGameTime;
    private LocalDateTime endGameTime;
}
