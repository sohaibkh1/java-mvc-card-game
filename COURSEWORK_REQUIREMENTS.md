# Coursework Requirements Used

This project follows the COMM110J card game requirements supplied for Codex.

The source rules used while building the project were:

- Use package `exeter.comp.msc`.
- Provide the required classes: `Card`, `CardDeck`, `Player`, `GameState`, `PackReader`, `InvalidPackException`, and `CardGame`.
- Simulate `n` players and `n` decks, where `n` is a positive integer.
- Read `n` from the command line and only accept a positive integer.
- Read a pack file containing exactly `8n` rows.
- Treat each row as one non-negative integer card value.
- Allow card values greater than `n`.
- Deal four cards to every player first, then deal the remaining cards to the decks.
- Use the required round-robin dealing order.
- Use the ring topology where player `i` draws from deck `i` and discards to the next deck.
- Run one thread per player.
- Use synchronized shared state and a shared action lock for atomic draw and discard.
- End the game when the first player declares a winning hand.
- Write one output file per player and one output file per deck.
- Keep the player and deck output wording exactly as specified.
- Use Maven and JUnit 5.
- Build an executable `target/cards.jar` with `exeter.comp.msc.CardGame` as the main class.



