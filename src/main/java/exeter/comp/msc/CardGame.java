package exeter.comp.msc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CardGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfPlayers = readNumberOfPlayers(scanner);
        List<Card> pack = null;

        while (pack == null) {
            Path packPath = readPackPath(scanner);
            try {
                pack = PackReader.readPack(packPath, numberOfPlayers);
            } catch (IOException | InvalidPackException e) {
                System.out.println("Invalid pack file. Please try again.");
                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                    System.out.println(e.getMessage());
                }
            }
        }

        try {
            runGame(numberOfPlayers, pack, Paths.get("."));
        } catch (IOException e) {
            System.out.println("Could not write output files: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("The game was interrupted.");
        }
    }

    static boolean isValidPlayerCount(String input) {
        if (input == null) {
            return false;
        }
        try {
            return Integer.parseInt(input.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static int readNumberOfPlayers(Scanner scanner) {
        while (true) {
            System.out.println("Please enter the number of players:");
            String input = scanner.nextLine();
            if (isValidPlayerCount(input)) {
                return Integer.parseInt(input.trim());
            }
            System.out.println("Invalid number of players. Please enter a positive integer.");
        }
    }

    static Path readPackPath(Scanner scanner) {
        System.out.println("Please enter pack file location:");
        return Paths.get(scanner.nextLine().trim());
    }

    static List<CardDeck> createDecks(int numberOfPlayers) {
        if (numberOfPlayers <= 0) {
            throw new IllegalArgumentException("Number of players must be positive.");
        }

        List<CardDeck> decks = new ArrayList<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            decks.add(new CardDeck(i));
        }
        return decks;
    }

    static List<Player> createPlayers(
            int numberOfPlayers,
            List<CardDeck> decks,
            GameState gameState,
            Object actionLock,
            Path outputDirectory
    ) {
        if (numberOfPlayers <= 0) {
            throw new IllegalArgumentException("Number of players must be positive.");
        }
        if (decks.size() != numberOfPlayers) {
            throw new IllegalArgumentException("There must be one deck for each player.");
        }

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            CardDeck leftDeck = decks.get(i);
            CardDeck rightDeck = decks.get((i + 1) % numberOfPlayers);
            players.add(new Player(i + 1, leftDeck, rightDeck, gameState, actionLock, outputDirectory));
        }
        return players;
    }

    static void dealCards(
            List<Card> pack,
            List<Player> players,
            List<CardDeck> decks
    ) {
        int index = 0;

        for (int round = 0; round < 4; round++) {
            for (Player player : players) {
                player.addCardToHand(pack.get(index));
                index++;
            }
        }

        int deckIndex = 0;
        while (index < pack.size()) {
            decks.get(deckIndex).addToBottom(pack.get(index));
            index++;
            deckIndex = (deckIndex + 1) % decks.size();
        }
    }

    static GameState runGame(
            int numberOfPlayers,
            List<Card> pack,
            Path outputDirectory
    ) throws IOException, InterruptedException {
        List<CardDeck> decks = createDecks(numberOfPlayers);
        GameState gameState = new GameState();
        Object actionLock = new Object();
        List<Player> players = createPlayers(numberOfPlayers, decks, gameState, actionLock, outputDirectory);

        dealCards(pack, players, decks);
        declareImmediateWinner(players, gameState);

        List<Thread> threads = new ArrayList<>();
        for (Player player : players) {
            Thread thread = new Thread(player, "player-" + player.getPlayerId());
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        writeDeckOutputs(decks, outputDirectory);
        return gameState;
    }

    static void writeDeckOutputs(
            List<CardDeck> decks,
            Path outputDirectory
    ) throws IOException {
        for (CardDeck deck : decks) {
            deck.writeOutput(outputDirectory);
        }
    }

    private static void declareImmediateWinner(List<Player> players, GameState gameState) {
        for (Player player : players) {
            if (player.hasWinningHand() && gameState.declareWinner(player.getPlayerId())) {
                System.out.println("player " + player.getPlayerId() + " wins");
                return;
            }
        }
    }
}



