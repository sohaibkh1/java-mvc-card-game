package exeter.comp.msc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Player implements Runnable {
    private static final int HAND_SIZE = 4;

    private final int playerId;
    private final int preferredValue;
    private final List<Card> hand;
    private final CardDeck leftDeck;
    private final CardDeck rightDeck;
    private final GameState gameState;
    private final Object actionLock;
    private final Path outputDirectory;

    public Player(
            int playerId,
            CardDeck leftDeck,
            CardDeck rightDeck,
            GameState gameState,
            Object actionLock,
            Path outputDirectory
    ) {
        if (playerId <= 0) {
            throw new IllegalArgumentException("Player id must be positive.");
        }
        if (leftDeck == null) {
            throw new IllegalArgumentException("Left deck cannot be null.");
        }
        if (rightDeck == null) {
            throw new IllegalArgumentException("Right deck cannot be null.");
        }
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }
        if (actionLock == null) {
            throw new IllegalArgumentException("Action lock cannot be null.");
        }
        if (outputDirectory == null) {
            throw new IllegalArgumentException("Output directory cannot be null.");
        }

        this.playerId = playerId;
        this.preferredValue = playerId;
        this.hand = new ArrayList<Card>();
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.gameState = gameState;
        this.actionLock = actionLock;
        this.outputDirectory = outputDirectory;
    }

    public int getPlayerId() {
        return playerId;
    }

    public synchronized void addCardToHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        if (hand.size() >= HAND_SIZE) {
            throw new IllegalStateException("Initial hand cannot contain more than four cards.");
        }
        hand.add(card);
    }

    public synchronized List<Card> getHandSnapshot() {
        return new ArrayList<Card>(hand);
    }

    synchronized boolean hasWinningHand() {
        if (hand.size() != HAND_SIZE) {
            return false;
        }

        int firstValue = hand.get(0).getValue();
        for (Card card : hand) {
            if (card.getValue() != firstValue) {
                return false;
            }
        }
        return true;
    }

    synchronized int chooseDiscardIndex() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getValue() != preferredValue) {
                return i;
            }
        }
        return 0;
    }

    private synchronized void addCardDuringTurn(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        hand.add(card);
    }

    private synchronized Card removeCardAt(int index) {
        return hand.remove(index);
    }

    private synchronized int handSize() {
        return hand.size();
    }

    private synchronized String handAsString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            if (i > 0) {
                builder.append(" ");
            }
            builder.append(hand.get(i));
        }
        return builder.toString();
    }

    @Override
    public void run() {
        try {
            Files.createDirectories(outputDirectory);
            Path outputFile = outputDirectory.resolve("player" + playerId + "_output.txt");
            try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
                writeLine(writer, "player " + playerId + " initial hand " + handAsString());

                if (gameState.isGameOver()) {
                    writeEnding(writer);
                    return;
                }

                if (hasWinningHand()) {
                    if (gameState.declareWinner(playerId)) {
                        System.out.println("player " + playerId + " wins");
                    }
                    writeEnding(writer);
                    return;
                }

                playUntilGameEnds(writer);
                writeEnding(writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not write output for player " + playerId + ".", e);
        }
    }

    private void playUntilGameEnds(BufferedWriter writer) throws IOException {
        while (!gameState.isGameOver()) {
            boolean tookTurn = false;

            // Draw and discard are locked together because the coursework says they must be one atomic action.
            synchronized (actionLock) {
                if (!gameState.isGameOver()) {
                    tookTurn = takeTurn(writer);
                }
            }

            if (!tookTurn && !gameState.isGameOver()) {
                Thread.yield();
            }
        }
    }

    private boolean takeTurn(BufferedWriter writer) throws IOException {
        Card drawnCard = leftDeck.drawFromTop();
        if (drawnCard == null) {
            return false;
        }

        addCardDuringTurn(drawnCard);
        writeLine(writer, "player " + playerId + " draws a " + drawnCard + " from deck " + leftDeck.getDeckId());

        Card discardedCard = removeCardAt(chooseDiscardIndex());
        rightDeck.addToBottom(discardedCard);
        writeLine(writer, "player " + playerId + " discards a " + discardedCard + " to deck " + rightDeck.getDeckId());
        writeLine(writer, "player " + playerId + " current hand is " + handAsString());

        if (handSize() != HAND_SIZE) {
            throw new IllegalStateException("Player " + playerId + " does not have four cards after a turn.");
        }

        if (hasWinningHand() && gameState.declareWinner(playerId)) {
            System.out.println("player " + playerId + " wins");
        }
        return true;
    }

    private void writeEnding(BufferedWriter writer) throws IOException {
        int winnerId = gameState.getWinnerId();
        if (winnerId == playerId) {
            writeLine(writer, "player " + playerId + " wins");
            writeLine(writer, "player " + playerId + " exits");
            writeLine(writer, "player " + playerId + " final hand: " + handAsString());
        } else {
            writeLine(writer, "player " + winnerId + " has informed player " + playerId
                    + " that player " + winnerId + " has won");
            writeLine(writer, "player " + playerId + " exits");
            writeLine(writer, "player " + playerId + " hand: " + handAsString());
        }
    }

    private void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }
}
