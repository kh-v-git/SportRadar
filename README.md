## SportRadar demo application

### About Service

GameServiceImpl executes all business logic:

- startGame();

  _Assumptions_:
    - All Teams exist in system or are given. Each team has unique country of origin. No CRUD functionality for Team.
    - All Games exist in the system or are given. Each game is unique in terms of team composition. No CRUD
      functionality for Game.
    - Two teams do not play against each other more than once.
    - Before start game in not visible on the board


- finishGame();

  _Assumptions_:
    - Game exists in the repository, unique by playing teams
    - Game was started
    - Game was not finished
    - Home team and away team scores >= 0.
    - Game hase boolean marker visibility

General assumptions:
default value for undefined Objects is null.

### Application Requirements

For building and running application you will need:

- [MS JDK 17](https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-17)
- [Maven](https://maven.apache.org/guides/index.html)

### About application

- Java 17 SDK (LTS)
- Spring Boot 3.2.0 (SNAPSHOT)