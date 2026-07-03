# Java Multithreaded Card Game

A Java Maven implementation of a multithreaded card game simulation.

The program creates a configurable number of players and decks, validates an input pack, deals cards in a round-robin structure, and runs one thread per player until a winning hand is found.

## Features

- Configurable number of players
- Input validation for player count and pack files
- Pack size validation using the `8n` card rule
- Non-negative integer card values
- One thread per player
- Ring-based draw and discard flow
- Atomic draw/discard turns using shared synchronization
- Player output files
- Final deck output files
- JUnit test coverage
- Executable Maven JAR build

## Requirements

- Java 11 or later
- Maven
- JUnit 5.10.2

## Run Tests

```bash
mvn test
```

## Build the JAR

```bash
mvn package
```

The built JAR is:

```text
target/cards.jar
```

## Run the Game

```bash
java -jar target/cards.jar
```

The program prompts for:

```text
Please enter the number of players:
Please enter pack file location:
```

A sample run with two players can use:

```text
src/test/resources/packs/valid_eventual_win_2.txt
```

After entering `2` for the number of players, enter that pack path when prompted.

## Input Rules

The number of players must be a positive integer.

For `n` players, the pack file must contain exactly `8n` rows. Each row must contain one non-negative integer card value.

Invalid player counts and invalid pack files are rejected before the game starts.

## Output Files

The game writes output files in the working directory:

```text
player1_output.txt
player2_output.txt
deck1_output.txt
deck2_output.txt
```

For `n` players, there are `n` player files and `n` deck files.

## Main Classes

- `Card` - immutable card value
- `CardDeck` - synchronized deck queue
- `Player` - player logic and thread execution
- `GameState` - shared winner state
- `PackReader` - pack file parsing and validation
- `InvalidPackException` - invalid pack handling
- `CardGame` - command-line entry point and game setup
