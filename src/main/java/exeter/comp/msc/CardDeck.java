package exeter.comp.msc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardDeck {
    private final int deckId;
    private final LinkedList<Card> cards;

    public CardDeck(int deckId) {
        if (deckId <= 0) {
            throw new IllegalArgumentException("Deck id must be positive.");
        }
        this.deckId = deckId;
        this.cards = new LinkedList<Card>();
    }

    public int getDeckId() {
        return deckId;
    }

    public synchronized void addToBottom(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        cards.addLast(card);
    }

    public synchronized Card drawFromTop() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.removeFirst();
    }

    public synchronized int size() {
        return cards.size();
    }

    public synchronized boolean isEmpty() {
        return cards.isEmpty();
    }

    public synchronized List<Card> snapshot() {
        // Return a copy so outside code cannot change the deck directly.
        return new ArrayList<Card>(cards);
    }

    public synchronized String contentsAsString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) {
                builder.append(" ");
            }
            builder.append(cards.get(i));
        }
        return builder.toString();
    }

    public synchronized void writeOutput(Path outputDirectory) throws IOException {
        if (outputDirectory == null) {
            throw new IllegalArgumentException("Output directory cannot be null.");
        }
        Files.createDirectories(outputDirectory);
        Path outputFile = outputDirectory.resolve("deck" + deckId + "_output.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            writer.write("deck" + deckId + " contents: " + contentsAsString());
        }
    }
}
