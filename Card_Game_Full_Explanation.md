# COMM110J Card Game — Full Code Explanation and CA Mapping

This file explains the final Java project step by step.  
It is meant as a revision and backup explanation file to keep in the project folder.

The code package is:

```java
package exeter.comp.msc;
```

The main executable class is:

```java
CardGame
```

The project is a Java Maven project. It uses Java, JUnit 5, threads, synchronized methods, file input/output, and simple object-oriented design.

---

# 1. What the coursework asks for

The coursework asks for a Java multi-threaded card game simulation.

The main rules are:

- The game has `n` players.
- The game has `n` decks.
- `n` is a positive integer.
- The pack has exactly `8n` cards.
- Each card is a non-negative integer.
- Card values greater than `n` are allowed.
- Each player starts with 4 cards.
- The remaining cards go into the decks.
- The players and decks form a ring.
- Each player runs in its own thread.
- Each player draws from the deck on the left.
- Each player discards to the deck on the right.
- A player wins when they have four cards of the same value.
- The first player to declare a win ends the game.
- Each player must have their own output file.
- Each deck must have its own output file at the end.
- Draw and discard must be treated as one atomic action.

The code is built around these rules.

---

# 2. Big picture of the program

The project has seven main production classes:

```text
Card
CardDeck
Player
GameState
PackReader
InvalidPackException
CardGame
```

Simple meaning:

| Class | Simple job |
|---|---|
| `Card` | Stores one card value. |
| `CardDeck` | Stores cards in a deck. |
| `Player` | Represents one player and runs as a thread. |
| `GameState` | Stores whether the game is over and who won. |
| `PackReader` | Reads and validates the pack file. |
| `InvalidPackException` | Custom exception for invalid pack files. |
| `CardGame` | Main class that creates and runs the game. |

The whole program flow is:

```text
1. CardGame asks the user for the number of players.
2. CardGame asks the user for the pack file path.
3. PackReader checks that the pack is valid.
4. CardGame creates n decks.
5. CardGame creates n players.
6. CardGame deals cards to the players.
7. CardGame deals remaining cards to the decks.
8. CardGame starts one thread per player.
9. Players draw and discard until someone wins.
10. GameState stores the first winner.
11. All players exit.
12. CardGame writes the deck output files.
```

---

# 3. Project folder structure

The important files are:

```text
src/main/java/exeter/comp/msc/Card.java
src/main/java/exeter/comp/msc/CardDeck.java
src/main/java/exeter/comp/msc/CardGame.java
src/main/java/exeter/comp/msc/GameState.java
src/main/java/exeter/comp/msc/InvalidPackException.java
src/main/java/exeter/comp/msc/PackReader.java
src/main/java/exeter/comp/msc/Player.java

src/test/java/exeter/comp/msc/CardTest.java
src/test/java/exeter/comp/msc/CardDeckTest.java
src/test/java/exeter/comp/msc/CardGameIntegrationTest.java
src/test/java/exeter/comp/msc/GameStateTest.java
src/test/java/exeter/comp/msc/PackReaderTest.java
src/test/java/exeter/comp/msc/PlayerTest.java

src/test/resources/packs/
pom.xml
README.md
COURSEWORK_REQUIREMENTS.md
REPORT_ALIGNMENT_NOTES.md
```

The `src/main/java` folder contains the actual game code.  
The `src/test/java` folder contains JUnit tests.  
The `src/test/resources/packs` folder contains sample pack files for testing.

---

# 4. How to run the project

From the main project folder, run the tests with:

```bash
mvn test
```

Build the jar with:

```bash
mvn package
```

Run the game with:

```bash
java -jar target/cards.jar
```

The program will ask:

```text
Please enter the number of players:
Please enter pack file location:
```

Example:

```text
Please enter the number of players:
2
Please enter pack file location:
src/test/resources/packs/valid_eventual_win_2.txt
```

The game then runs and creates output files in the working directory.

---

# 5. Output files

For `n` players, the game creates:

```text
n player files
n deck files
```

So the total number of output files is:

```text
2n
```

Example for `n = 2`:

```text
player1_output.txt
player2_output.txt
deck1_output.txt
deck2_output.txt
```

Example for `n = 4`:

```text
player1_output.txt
player2_output.txt
player3_output.txt
player4_output.txt
deck1_output.txt
deck2_output.txt
deck3_output.txt
deck4_output.txt
```

The terminal only needs to show the winner:

```text
player 1 wins
```

The full game trace is written to the player output files.

---

# 6. Output wording

The player output file starts with the initial hand:

```text
player 1 initial hand 1 1 2 3
```

During the game, actions are written like this:

```text
player 1 draws a 4 from deck 1
player 1 discards a 3 to deck 2
player 1 current hand is 1 1 2 4
```

If the player wins, the ending is:

```text
player 1 wins
player 1 exits
player 1 final hand: 1 1 1 1
```

If another player wins, the ending is:

```text
player 3 has informed player 1 that player 3 has won
player 1 exits
player 1 hand: 2 2 3 5
```

Deck files look like this:

```text
deck2 contents: 1 3 3 7
```

The wording matters because the coursework gives these examples.

---

# 7. Class-by-class explanation

## 7.1 `Card.java`

### Purpose

`Card` represents one card in the game.

A card only stores one value:

```java
private final int value;
```

Example:

```java
new Card(5)
```

means a card with value 5.

### Constructor

```java
public Card(int value)
```

It rejects negative values:

```java
if (value < 0) {
    throw new IllegalArgumentException("Card value cannot be negative.");
}
```

This matches the coursework rule that card values must be non-negative.

### Methods

```java
public int getValue()
```

Returns the card value.

```java
public String toString()
```

Returns the value as text.

Example:

```java
new Card(12).toString()
```

returns:

```text
12
```

```java
public boolean equals(Object other)
public int hashCode()
```

These are mainly useful for tests. They allow two cards with the same value to be treated as equal.

### Thread-safety

`Card` is thread-safe because it is immutable.

Immutable means:

- the value is set once in the constructor;
- the field is `final`;
- there is no setter method;
- the value cannot change after the card is created.

So it is safe to share `Card` objects between threads.

### CA requirement satisfied

The coursework requires a thread-safe `Card` class.  
This class satisfies that by being immutable.

---

## 7.2 `CardDeck.java`

### Purpose

`CardDeck` represents one deck.

It stores:

```java
private final int deckId;
private final LinkedList<Card> cards;
```

The deck behaves like a queue:

```text
draw from top = remove first card
discard to bottom = add last card
```

### Constructor

```java
public CardDeck(int deckId)
```

It rejects invalid deck IDs:

```java
if (deckId <= 0) {
    throw new IllegalArgumentException("Deck id must be positive.");
}
```

Decks are numbered from 1 to `n`.

### Important methods

```java
public int getDeckId()
```

Returns the deck number.

```java
public synchronized void addToBottom(Card card)
```

Adds a card to the bottom of the deck.

It rejects `null` cards.

```java
public synchronized Card drawFromTop()
```

Draws the top card from the deck.

If the deck is empty, it returns `null`.

```java
public synchronized int size()
```

Returns the number of cards in the deck.

```java
public synchronized boolean isEmpty()
```

Returns true if the deck has no cards.

```java
public synchronized List<Card> snapshot()
```

Returns a copy of the deck.

This is important because the code should not expose the real internal list.  
If it returned the real list, outside code could change the deck without using synchronized methods.

```java
public synchronized String contentsAsString()
```

Turns the deck contents into text with spaces.

Example:

```text
1 3 7
```

No trailing space is added.

```java
public synchronized void writeOutput(Path outputDirectory)
```

Writes the final deck output file.

Example:

```text
deck1_output.txt
```

with content:

```text
deck1 contents: 2 3 4 5
```

### Thread-safety

`CardDeck` methods are synchronized because decks are shared between player threads.

Example with two players:

```text
player 1 discards to deck 2
player 2 draws from deck 2
```

Both players can access deck 2.  
So deck operations must be protected.

### CA requirement satisfied

`CardDeck` helps satisfy:

- `n` decks;
- drawing from a deck;
- discarding to a deck;
- thread-safe shared deck access;
- final deck output files.

---

## 7.3 `GameState.java`

### Purpose

`GameState` stores the shared state of the game.

It stores:

```java
private boolean gameOver;
private int winnerId;
```

Meaning:

```text
gameOver = has someone won?
winnerId = which player won?
```

### Important methods

```java
public synchronized boolean declareWinner(int playerId)
```

This is called when a player thinks they have won.

The logic is:

```text
If no winner exists yet:
    set gameOver to true
    store winnerId
    return true

If a winner already exists:
    do not change anything
    return false
```

This prevents later players from overwriting the first winner.

```java
public synchronized boolean isGameOver()
```

Players call this to know whether they should stop.

```java
public synchronized int getWinnerId()
```

Returns the winning player number.

### Thread-safety

All methods are synchronized.

This matters because many player threads can check or change the game state.

Without synchronization, two players could both try to set themselves as the winner.

### CA requirement satisfied

The coursework says there should only be one player declaring a win for a single run.  
`GameState` helps enforce this by accepting only the first winner.

---

## 7.4 `InvalidPackException.java`

### Purpose

This class is a custom checked exception for invalid pack files.

Code idea:

```java
public class InvalidPackException extends Exception
```

It is used when the pack file is wrong.

Examples:

```text
wrong number of rows
negative value
blank row
non-integer row
multiple values on one row
```

### Inheritance

This class uses inheritance.

```java
InvalidPackException extends Exception
```

This means `InvalidPackException` is a specific type of `Exception`.

This is normal Java exception design.

### CA requirement satisfied

The coursework gives marks for exception handling.  
This class makes pack validation errors clearer and easier to handle.

---

## 7.5 `PackReader.java`

### Purpose

`PackReader` reads and validates the input pack before the game starts.

Main method:

```java
public static List<Card> readPack(Path path, int numberOfPlayers)
        throws IOException, InvalidPackException
```

It returns a list of `Card` objects in the same order as the file.

The first row in the file is treated as the top of the pack.

### Validation rules

`PackReader` checks:

1. Number of players is positive.
2. Pack path is not null.
3. File exists.
4. File is readable.
5. File has exactly `8 * numberOfPlayers` rows.
6. Each row is not blank.
7. Each row contains exactly one value.
8. Each value is an integer.
9. Each value is non-negative.
10. Card values greater than `n` are allowed.

### Why exact size matters

If `n = 1`:

```text
8n = 8 cards
```

If `n = 2`:

```text
8n = 16 cards
```

If `n = 4`:

```text
8n = 32 cards
```

The pack must match exactly.

### Why high card values are allowed

For example, if there are 2 players, this is still valid:

```text
20
```

because the coursework says card values may be greater than `n`.

### CA requirement satisfied

`PackReader` satisfies:

- pack file input;
- exactly `8n` rows;
- one non-negative integer per row;
- reject invalid pack;
- allow card values greater than `n`;
- game does not start until the pack is valid.

---

## 7.6 `Player.java`

### Purpose

`Player` represents one player in the game.

It implements:

```java
public class Player implements Runnable
```

This means each player can run in a Java `Thread`.

### Why `Runnable` is used

The coursework requires multiple player threads.

Using `Runnable` means the code can do:

```java
Thread thread = new Thread(player);
thread.start();
```

This matches the threading material from the module.

### Important fields

```java
private static final int HAND_SIZE = 4;

private final int playerId;
private final int preferredValue;
private final List<Card> hand;
private final CardDeck leftDeck;
private final CardDeck rightDeck;
private final GameState gameState;
private final Object actionLock;
private final Path outputDirectory;
```

Meaning:

| Field | Meaning |
|---|---|
| `HAND_SIZE` | Always 4. |
| `playerId` | Player number. |
| `preferredValue` | Same as player ID. |
| `hand` | Player cards. |
| `leftDeck` | Deck the player draws from. |
| `rightDeck` | Deck the player discards to. |
| `gameState` | Shared winner/game-over state. |
| `actionLock` | Shared lock for atomic draw and discard. |
| `outputDirectory` | Where output files are written. |

### Constructor

The constructor checks:

- player ID is positive;
- left deck is not null;
- right deck is not null;
- game state is not null;
- action lock is not null;
- output directory is not null.

This prevents broken player objects.

### Player preference

Each player prefers their own number:

```text
player 1 prefers 1s
player 2 prefers 2s
player 3 prefers 3s
```

This is stored as:

```java
this.preferredValue = playerId;
```

### Adding initial cards

```java
public synchronized void addCardToHand(Card card)
```

This is used during the initial deal.

It rejects:

- null cards;
- more than 4 cards during initial dealing.

### Hand snapshot

```java
public synchronized List<Card> getHandSnapshot()
```

This returns a copy of the hand.

This is used in tests and avoids exposing the real hand list.

### Winning hand check

```java
synchronized boolean hasWinningHand()
```

A player wins if:

```text
hand has exactly 4 cards
all 4 cards have the same value
```

Important:

The winning cards do not need to match the preferred value.

Example:

```text
player 2 hand: 7 7 7 7
```

Player 2 still wins.

### Discard choice

```java
synchronized int chooseDiscardIndex()
```

The rule is:

```text
Choose the first card that is not the preferred value.
```

Example:

```text
player 1 hand: 1 1 4 1
preferred value: 1
discard index: 2
discard card: 4
```

This is simple and deterministic.

Why this is good:

- It follows the coursework strategy.
- It avoids keeping non-preferred cards forever.
- It is easy to test.
- It avoids randomness, so output is easier to explain.

If every card is preferred, the method returns index 0.  
Usually, if a player has four preferred cards, they have already won.

### `run()` method

This is the main player thread method.

Step by step:

```text
1. Create the output directory if needed.
2. Open playerX_output.txt.
3. Write the initial hand.
4. If the game is already over, write the ending and return.
5. If this player has a winning hand, declare winner and write ending.
6. Otherwise, keep playing until the game ends.
7. When the game ends, write final/informed lines.
```

### Normal play loop

The method:

```java
playUntilGameEnds(writer)
```

runs while the game is not over.

Inside it, the player uses:

```java
synchronized (actionLock) {
    tookTurn = takeTurn(writer);
}
```

This is the most important concurrency part.

### `takeTurn()`

One turn does this:

```text
1. Draw from left deck.
2. If no card is available, return false.
3. Add drawn card to hand.
4. Write draw line.
5. Choose discard card.
6. Remove discard card from hand.
7. Add discard card to right deck.
8. Write discard line.
9. Write current hand line.
10. Check hand size is still 4.
11. Check if the player has won.
12. Declare winner if needed.
```

### Atomic action

The coursework says draw and discard must be atomic.

That means draw and discard should be treated as one action.

The code uses:

```java
synchronized (actionLock)
```

around the complete turn.

This prevents a player being left halfway through a turn.

Without this, a player could temporarily have 5 cards after drawing and before discarding.  
The lock makes the action safer and easier to explain.

### Player output ending

If this player wins:

```text
player 1 wins
player 1 exits
player 1 final hand: 1 1 1 1
```

If another player wins:

```text
player 3 has informed player 1 that player 3 has won
player 1 exits
player 1 hand: 2 2 3 5
```

### CA requirement satisfied

`Player` satisfies:

- one player object per player;
- one thread per player;
- preferred card strategy;
- draw from left deck;
- discard to right deck;
- check winner;
- write player output file;
- stop when another player wins;
- thread-safe hand access.

---

## 7.7 `CardGame.java`

### Purpose

`CardGame` is the main controller.

It is the class with:

```java
public static void main(String[] args)
```

This makes the program executable.

### Main method flow

The main method does:

```text
1. Create Scanner for terminal input.
2. Read number of players.
3. Keep asking until the number is valid.
4. Read pack path.
5. Keep asking until the pack is valid.
6. Run the game.
7. Handle output errors or interruption.
```

### Player count validation

```java
static boolean isValidPlayerCount(String input)
```

This returns true only if the input is a positive integer.

Valid:

```text
1
2
3
```

Invalid:

```text
0
-1
abc
blank input
```

This matches the coursework wording: `n` is a positive integer.

### Reading player number

```java
static int readNumberOfPlayers(Scanner scanner)
```

This asks:

```text
Please enter the number of players:
```

If invalid, it prints:

```text
Invalid number of players. Please enter a positive integer.
```

### Reading pack path

```java
static Path readPackPath(Scanner scanner)
```

This asks:

```text
Please enter pack file location:
```

### Creating decks

```java
static List<CardDeck> createDecks(int numberOfPlayers)
```

If `n = 3`, it creates:

```text
deck 1
deck 2
deck 3
```

### Creating players

```java
static List<Player> createPlayers(...)
```

This creates the ring topology.

For `n = 4`:

```text
player 1 draws from deck 1, discards to deck 2
player 2 draws from deck 2, discards to deck 3
player 3 draws from deck 3, discards to deck 4
player 4 draws from deck 4, discards to deck 1
```

The key line is:

```java
CardDeck rightDeck = decks.get((i + 1) % numberOfPlayers);
```

The modulo `%` makes the last player wrap around to deck 1.

For `n = 1`:

```text
player 1 draws from deck 1 and discards to deck 1
```

This still works because deck 1 is both left and right.

### Dealing cards

```java
static void dealCards(List<Card> pack, List<Player> players, List<CardDeck> decks)
```

This deals cards in two stages.

#### Stage 1: deal player hands

It deals 4 rounds.

For `n = 2`, rows go:

```text
row 1 -> player 1
row 2 -> player 2
row 3 -> player 1
row 4 -> player 2
row 5 -> player 1
row 6 -> player 2
row 7 -> player 1
row 8 -> player 2
```

So each player gets exactly 4 cards.

#### Stage 2: fill decks

Remaining cards go to decks round-robin.

For `n = 2`:

```text
row 9  -> deck 1
row 10 -> deck 2
row 11 -> deck 1
row 12 -> deck 2
row 13 -> deck 1
row 14 -> deck 2
row 15 -> deck 1
row 16 -> deck 2
```

So each deck gets 4 cards.

### Running the game

```java
static GameState runGame(int numberOfPlayers, List<Card> pack, Path outputDirectory)
```

This method:

```text
1. Creates decks.
2. Creates GameState.
3. Creates actionLock.
4. Creates players.
5. Deals cards.
6. Checks immediate winner.
7. Starts player threads.
8. Joins player threads.
9. Writes deck outputs.
10. Returns GameState.
```

### Starting threads

For each player:

```java
Thread thread = new Thread(player, "player-" + player.getPlayerId());
thread.start();
```

This means each player runs in its own thread.

### Joining threads

```java
thread.join();
```

This makes `CardGame` wait until all player threads finish.

Only after all players exit does the game write final deck files.

### Immediate winner implementation note

The current code checks for an immediate winner in `CardGame` after dealing and before starting the player threads.

This makes the winner fixed before normal play begins.

Then the player threads still run so that every player creates their required output file.

If asked why:

```text
The immediate win is checked immediately after the initial deal because no draw/discard is needed. The player threads are still started afterwards so every player writes its required output file.
```

This is a design choice. The external behaviour is correct:

- the winner is printed;
- all player files are created;
- all deck files are created;
- other players are informed that the game has ended.

If a marker is very strict about the wording "player thread declares immediate win", a possible future improvement is to move the immediate-win declaration fully into `Player.run()`. But the current implementation still satisfies the visible game behaviour and output requirements.

### Writing deck outputs

```java
static void writeDeckOutputs(List<CardDeck> decks, Path outputDirectory)
```

Calls:

```java
deck.writeOutput(outputDirectory)
```

for each deck.

---

# 8. Detailed game example: n = 2 eventual win

Pack file:

```text
1
2
1
2
1
3
9
4
1
5
6
7
8
9
10
11
```

There are 16 rows because:

```text
8n = 8 * 2 = 16
```

## Dealing to players

Player hands are dealt first:

```text
row 1 -> player 1: 1
row 2 -> player 2: 2
row 3 -> player 1: 1
row 4 -> player 2: 2
row 5 -> player 1: 1
row 6 -> player 2: 3
row 7 -> player 1: 9
row 8 -> player 2: 4
```

So:

```text
player 1 initial hand: 1 1 1 9
player 2 initial hand: 2 2 3 4
```

## Dealing to decks

Remaining rows:

```text
row 9  -> deck 1: 1
row 10 -> deck 2: 5
row 11 -> deck 1: 6
row 12 -> deck 2: 7
row 13 -> deck 1: 8
row 14 -> deck 2: 9
row 15 -> deck 1: 10
row 16 -> deck 2: 11
```

So:

```text
deck 1: 1 6 8 10
deck 2: 5 7 9 11
```

## Player 1 turn

Player 1 draws from deck 1:

```text
draws 1
```

Player 1 hand becomes:

```text
1 1 1 9 1
```

Player 1 prefers 1s, so they discard the first non-preferred card:

```text
discard 9
```

Player 1 hand becomes:

```text
1 1 1 1
```

Player 1 wins.

Output includes:

```text
player 1 draws a 1 from deck 1
player 1 discards a 9 to deck 2
player 1 current hand is 1 1 1 1
player 1 wins
```

---

# 9. One-player case

The code now accepts `n = 1`, because the coursework says `n` is a positive integer.

A valid one-player immediate-win pack is:

```text
1
1
1
1
2
3
4
5
```

There are 8 rows because:

```text
8n = 8 * 1 = 8
```

Player 1 receives:

```text
1 1 1 1
```

Deck 1 receives:

```text
2 3 4 5
```

Player 1 wins immediately.

The output files are:

```text
player1_output.txt
deck1_output.txt
```

Player output:

```text
player 1 initial hand 1 1 1 1
player 1 wins
player 1 exits
player 1 final hand: 1 1 1 1
```

Deck output:

```text
deck1 contents: 2 3 4 5
```

For one player, the ring topology means:

```text
player 1 draws from deck 1
player 1 discards to deck 1
```

This is acceptable because there is only one deck.

---

# 10. Threading and concurrency explanation

## Why threads are required

The coursework asks for a multi-threaded simulation.  
So the players should not run as a simple fixed loop controlled by `CardGame`.

Wrong idea:

```java
player1.takeTurn();
player2.takeTurn();
player3.takeTurn();
```

That would be sequential.

Correct idea:

```java
Thread thread = new Thread(player);
thread.start();
```

Each player runs independently.

## What shared data exists

The shared data is:

```text
decks
game state
possibly player hand snapshots
```

## Main risks

### Race condition

A race condition happens when two threads access or change shared data at the same time.

Example:

```text
player 1 discards to deck 2
player 2 draws from deck 2
```

Both touch deck 2.

### Winner race

Two players might both reach a winning hand around the same time.

`GameState` prevents the second one from overwriting the first.

### Broken hand size

During a turn, a player draws first and discards second.

In the middle, they temporarily have 5 cards.

The coursework says draw and discard must be atomic, so this needs protection.

## Thread-safety choices

| Shared part | Protection |
|---|---|
| `Card` | Immutable object. |
| `CardDeck` | Synchronized methods. |
| `GameState` | Synchronized methods. |
| `Player` hand | Synchronized hand methods. |
| Draw + discard | Shared `actionLock`. |

## Why `actionLock` is used

The code uses:

```java
synchronized (actionLock) {
    tookTurn = takeTurn(writer);
}
```

This keeps draw and discard together.

The action inside the lock is:

```text
draw
add to hand
write draw
choose discard
remove discard
add to right deck
write discard
write current hand
check win
```

This matches the atomic action requirement.

## Why `join()` is used

After starting all threads, `CardGame` calls:

```java
thread.join();
```

This waits for the player threads to finish.

Then deck output files are written.

This matters because the deck contents should be final.  
If deck files were written before players ended, the deck state could still change.

---

# 11. Object-oriented design explanation

The code follows object-oriented design because each class has a clear responsibility.

| OOP idea | Where it appears |
|---|---|
| Class/object | `Card`, `Player`, `CardDeck`, etc. |
| Encapsulation | Private fields and public methods. |
| Composition | `Player` has decks, hand, game state, and action lock. |
| Inheritance | `InvalidPackException extends Exception`. |
| Interface/polymorphism | `Player implements Runnable`. |
| Method overriding | `Card` overrides `toString`, `equals`, and `hashCode`. |

## Inheritance

The main inheritance example is:

```java
public class InvalidPackException extends Exception
```

This means `InvalidPackException` is a specific type of `Exception`.

## Interface

The main interface example is:

```java
public class Player implements Runnable
```

This means `Player` promises to provide a `run()` method.

Java threads can then run the player.

## Why no abstract class is used

The module covers abstract classes, but the code does not need one.

Adding an abstract class just to show the topic would make the design less natural.

The design is better because it is simple and direct.

---

# 12. Testing explanation

The project uses JUnit 5.

The tests are split into small unit tests and bigger integration tests.

## 12.1 `CardTest`

Checks:

- card stores value correctly;
- negative card values are rejected;
- `toString()` returns the number as text;
- `equals()` and `hashCode()` use the card value.

Example test idea:

```text
new Card(7).getValue() returns 7
new Card(-1) throws IllegalArgumentException
```

## 12.2 `CardDeckTest`

Checks:

- `addToBottom` adds cards;
- `drawFromTop` removes the first card;
- deck size changes correctly;
- `snapshot` does not expose the real list;
- `contentsAsString` has correct spacing;
- `writeOutput` creates the correct deck file.

Example:

```text
deck2 contents: 1 3 3 7
```

## 12.3 `GameStateTest`

Checks:

- first player can declare winner;
- game becomes over;
- winner ID is stored;
- second player cannot overwrite the first winner;
- invalid player ID is rejected.

This proves the one-winner logic.

## 12.4 `PackReaderTest`

Checks:

- zero players rejected;
- one-player pack accepted;
- valid `8n` pack accepted;
- wrong-size pack rejected;
- negative card rejected;
- non-integer row rejected;
- blank row rejected;
- multiple values on one row rejected;
- card values greater than number of players accepted.

This is important for exception handling marks.

## 12.5 `PlayerTest`

Checks:

- winning hand is detected;
- winning value does not need to match preferred value;
- discard choice picks a non-preferred card;
- hand returns to four cards after draw/discard;
- immediate-win output format is correct.

## 12.6 `CardGameIntegrationTest`

Checks the whole system more directly:

- player count validation;
- deck creation;
- player creation;
- round-robin dealing;
- last player discards to deck 1;
- one-player game output;
- `2n` output files;
- immediate win;
- eventual win;
- non-winner output lines.

Integration tests are useful because they check that the classes work together.

---

# 13. How the code satisfies each CA requirement

| CA requirement | Code support |
|---|---|
| Java application | Project is written in Java. |
| Executable `CardGame` | `CardGame` has `main(String[] args)`. |
| Thread-safe `Card` | `Card` is immutable. |
| Thread-safe `Player` | Player hand methods are synchronized; shared state is protected. |
| Additional classes allowed | Uses `CardDeck`, `GameState`, `PackReader`, `InvalidPackException`. |
| `n` players | `createPlayers(n, ...)` creates `n` players. |
| `n` decks | `createDecks(n)` creates `n` decks. |
| `n` positive | `isValidPlayerCount` requires value > 0. |
| `8n` pack | `PackReader` checks exact pack size. |
| Non-negative card values | `Card` and `PackReader` reject negative values. |
| Values greater than `n` allowed | `PackReader` does not restrict high values. |
| 4 cards per player | `dealCards` gives four rounds to players. |
| Round-robin dealing | `dealCards` loops through players/decks in order. |
| Ring topology | `rightDeck = decks.get((i + 1) % numberOfPlayers)`. |
| Draw from left deck | `Player` uses `leftDeck.drawFromTop()`. |
| Discard to right deck | `Player` uses `rightDeck.addToBottom(...)`. |
| Preferred strategy | `preferredValue = playerId`. |
| Discard non-preferred | `chooseDiscardIndex()` selects a non-preferred card first. |
| Multi-threaded | `CardGame` creates one `Thread` per `Player`. |
| Not sequential | Players run independently through `Thread.start()`. |
| Atomic draw/discard | Shared `actionLock` surrounds `takeTurn()`. |
| Winner detection | `hasWinningHand()` checks four equal cards. |
| One winner | `GameState.declareWinner()` only accepts first winner. |
| Terminal winner message | Code prints `player X wins`. |
| Player files | `Player.run()` writes `playerX_output.txt`. |
| Deck files | `CardDeck.writeOutput()` writes `deckX_output.txt`. |
| Invalid pack handling | `PackReader` throws `InvalidPackException`. |
| Testing | JUnit test classes are included. |
| README | README explains tests, jar build, and running the game. |
| Jar source and bytecode | Maven build creates `target/cards.jar` with `.class` and `.java` files. |

---

# 14. Report sections this code supports

The report can be built from the code like this:

## Requirements analysis

Mention:

- valid player number;
- valid pack file;
- `8n` cards;
- one output file per player;
- one output file per deck;
- multi-threading;
- atomic draw/discard;
- winner detection.

## Use cases

Main use cases:

- Start game.
- Enter number of players.
- Provide pack file.
- Validate pack.
- Initialise game.
- Run game simulation.
- Declare winner.
- Write output files.
- Run tests.

## Design

Use the seven classes:

```text
Card
CardDeck
Player
GameState
PackReader
InvalidPackException
CardGame
```

Explain why each exists.

## Class diagram

Show:

```text
CardGame creates Players and CardDecks
Player has leftDeck and rightDeck
Player has GameState
Player has Cards in hand
CardDeck has Cards
PackReader creates Cards from file
InvalidPackException extends Exception
Player implements Runnable
```

## Sequence diagram

A good sequence is:

```text
User -> CardGame: enter n
User -> CardGame: enter pack path
CardGame -> PackReader: readPack(path, n)
PackReader -> CardGame: List<Card>
CardGame -> CardDeck: create decks
CardGame -> Player: create players
CardGame -> Player/CardDeck: dealCards
CardGame -> Thread: start player threads
Player -> CardDeck: drawFromTop
Player -> CardDeck: addToBottom
Player -> GameState: declareWinner
CardGame -> Thread: join
CardGame -> CardDeck: writeOutput
```

## Testing strategy

Explain:

- unit tests for small classes;
- integration tests for whole game;
- invalid input tests;
- output file tests;
- immediate and eventual win tests.

## Exception handling

Mention:

- invalid player number;
- missing pack file;
- unreadable pack file;
- wrong pack size;
- blank row;
- non-integer row;
- multiple values on one row;
- negative card value.

## Thread-safety

Mention:

- immutable `Card`;
- synchronized `CardDeck`;
- synchronized `GameState`;
- synchronized player hand methods;
- shared `actionLock`.

---

# 15. How to explain the code in a discussion

Use this answer if asked for the whole design:

```text
I split the game into small classes. Card stores one immutable card value. CardDeck stores deck cards and uses synchronized methods because decks are shared by player threads. Player implements Runnable, so every player can run in its own thread. GameState stores whether the game is over and keeps the first winner. PackReader validates the input pack before the game starts. CardGame is the main class: it reads input, creates players and decks, deals cards, starts threads, waits for them using join, and writes the final deck files.
```

Use this answer if asked about threading:

```text
Each Player is run in a separate Thread. The shared data is the decks and the game state. I used synchronized methods for CardDeck and GameState to avoid race conditions. I also used one shared actionLock around draw and discard because the coursework says these two actions must be atomic. This keeps the player hand size correct after every complete turn.
```

Use this answer if asked about the discard strategy:

```text
Each player prefers the card value equal to their player number. After drawing, the player discards the first card in their hand that is not the preferred value. This is simple and deterministic, so it is easy to test, and it satisfies the requirement that non-preferred cards should not be kept indefinitely.
```

Use this answer if asked about testing:

```text
I used JUnit 5. The unit tests check the small classes such as Card, CardDeck, PackReader, GameState, and Player. The integration tests check the full game flow, including round-robin dealing, output files, immediate win, eventual win, and one-player support.
```

---

# 16. Important small warning

Do not submit the whole development zip if it contains extra folders like:

```text
.tools
target
downloaded JDK files
downloaded Maven files
```

For the final submission, prepare the exact files requested by the coursework:

```text
cards.jar
cardsTest.zip
report
```

The jar should include both `.class` and `.java` files.  
The test zip should include production code, tests, test resources, `pom.xml`, and README.

---

# 17. Quick revision checklist

Before explaining the project, make sure you can answer these:

1. What does `Card` store?
2. Why is `Card` thread-safe?
3. Why does `CardDeck` use synchronized methods?
4. What does `GameState` store?
5. How does `GameState` prevent two winners?
6. What does `PackReader` check?
7. Why can card values be greater than `n`?
8. Why does `Player` implement `Runnable`?
9. What is `actionLock` for?
10. Why must draw and discard be atomic?
11. How does round-robin dealing work?
12. Why does `CardGame` use `join()`?
13. What output files are created?
14. How many output files are created for `n` players?
15. What does each test class check?

---

# 18. Very short final summary

The project is a Java multi-threaded card game.

```text
CardGame controls the setup.
PackReader validates the pack.
Card stores one card value.
CardDeck stores deck cards safely.
Player runs as a thread and plays the game.
GameState stores the first winner.
JUnit tests check the important requirements.
```

The key design decision is:

```text
Use synchronized methods for shared objects and one shared actionLock so draw + discard is atomic.
```

That is the centre of the coursework.
