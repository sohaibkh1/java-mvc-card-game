# Report Alignment Notes

This file is a short guide showing how the code matches the coursework requirements.

## Requirements Implemented

- Positive number of players. The program accepts 1 or more players and rejects 0, negative, blank, and non-integer input.
- Valid pack input before the game starts.
- Exactly `8n` cards in the pack.
- Card values are non-negative integers.
- Card values greater than `n` are allowed.
- `n` players and `n` decks.
- Round-robin dealing to players first, then decks.
- One thread per player.
- Ring topology for drawing and discarding.
- Preferred denomination strategy for discarding.
- Atomic draw and discard using one shared lock.
- Winner detection for any four equal cards.
- Player output files.
- Deck output files.
- JUnit tests.

## Class Design

- `Card`: immutable card value.
- `CardDeck`: synchronized deck queue.
- `Player`: `Runnable`, hand, game actions, and output file.
- `GameState`: synchronized shared winner flag.
- `PackReader`: validation and conversion from file rows to cards.
- `InvalidPackException`: clear invalid pack errors.
- `CardGame`: input, setup, dealing, starting threads, joining threads, and deck output.

## Thread-Safety

- `Card` is immutable, so it is safe to share.
- `CardDeck` methods are synchronized because decks are shared by players.
- `GameState` methods are synchronized so only the first winner is stored.
- `Player` hand methods are synchronized so the hand is not exposed directly.
- A shared `actionLock` keeps draw and discard together as one action.

## Testing

- Unit tests check the individual classes.
- Integration tests check dealing, full game runs, and output files.
- Invalid pack tests cover wrong size, negative values, non-integers, blank rows, and multiple values on one row.
- Immediate win and eventual win packs are tested.

## Exception Handling

- Invalid player numbers, including 0, negative numbers, blank input, and non-integer input, are rejected before the game starts.
- Missing or unreadable pack files are rejected.
- Wrong pack size is rejected.
- Negative values are rejected.
- Non-integer rows are rejected.
- Blank rows are rejected.
- Multiple values on one row are rejected.
- IO errors are shown as invalid pack messages in the command-line program.




