package exeter.comp.msc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardDeckTest {
    @TempDir
    Path tempDirectory;

    @Test
    void addToBottomAddsCardsCorrectly() {
        CardDeck deck = new CardDeck(1);

        deck.addToBottom(new Card(4));

        assertEquals(Arrays.asList(new Card(4)), deck.snapshot());
    }

    @Test
    void drawFromTopRemovesFirstCard() {
        CardDeck deck = new CardDeck(1);
        deck.addToBottom(new Card(4));
        deck.addToBottom(new Card(5));

        assertEquals(new Card(4), deck.drawFromTop());
        assertEquals(Arrays.asList(new Card(5)), deck.snapshot());
    }

    @Test
    void sizeUpdatesCorrectly() {
        CardDeck deck = new CardDeck(1);

        assertEquals(0, deck.size());
        deck.addToBottom(new Card(1));
        assertEquals(1, deck.size());
        deck.drawFromTop();
        assertEquals(0, deck.size());
        assertTrue(deck.isEmpty());
    }

    @Test
    void snapshotDoesNotExposeInternalList() {
        CardDeck deck = new CardDeck(1);
        deck.addToBottom(new Card(2));

        List<Card> snapshot = deck.snapshot();
        snapshot.clear();

        assertEquals(1, deck.size());
    }

    @Test
    void contentsAsStringUsesSpacesWithNoTrailingSpace() {
        CardDeck deck = new CardDeck(1);
        deck.addToBottom(new Card(1));
        deck.addToBottom(new Card(3));
        deck.addToBottom(new Card(7));

        assertEquals("1 3 7", deck.contentsAsString());
    }

    @Test
    void writeOutputCreatesCorrectFileContent() throws IOException {
        CardDeck deck = new CardDeck(2);
        deck.addToBottom(new Card(1));
        deck.addToBottom(new Card(3));
        deck.addToBottom(new Card(3));
        deck.addToBottom(new Card(7));

        deck.writeOutput(tempDirectory);

        Path outputFile = tempDirectory.resolve("deck2_output.txt");
        assertEquals("deck2 contents: 1 3 3 7", new String(Files.readAllBytes(outputFile)));
    }
}
