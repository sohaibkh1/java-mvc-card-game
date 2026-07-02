# COMM110J Card Game

This is a Java Maven project multi-threaded card game 

The program asks for the number of players and a pack file. The number of players must be a positive integer. It checks that the pack has exactly `8n` non-negative integer card values before the game starts. The game then creates `n` players, `n` decks, and one thread for each player.

## Versions

- Java 11 or later
- JUnit 5.10.2
- Maven

## Run Tests

```bash
mvn test
```

## Build The Jar

```bash
mvn package
```

The built jar is:

```text
target/cards.jar
```

The jar contains both the compiled `.class` files and the source `.java` files.

## Run The Game

```bash
java -jar target/cards.jar
```

The program prompts:

```text
Please enter the number of players:
Please enter pack file location:
```

If the number of players is invalid, it prints:

```text
Invalid number of players. Please enter a positive integer.
```

If the pack file is invalid, it prints:

```text
Invalid pack file. Please try again.
```

The game writes output files in the working directory when running normally:

```text
player1_output.txt
player2_output.txt
deck1_output.txt
deck2_output.txt
```

For `n` players, where `n` is a positive integer, there are `n` player files and `n` deck files.

## Test Pack Files

The valid test pack files include 1 to 8 players. The sample immediate-win packs use small card values from 1 to 10. The 0-player file is an invalid example. The test pack files are in:

```text
src/test/resources/packs/
```

A sample run with two players can use:

```text
src/test/resources/packs/valid_eventual_win_2.txt
```

After entering `2` for the number of players, enter that pack path when prompted.





