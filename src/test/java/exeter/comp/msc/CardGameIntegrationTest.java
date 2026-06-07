package exeter.comp.msc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardGameIntegrationTest {
    @TempDir
    Path tempDirectory;

    @Test
    void acceptsOnePlayerAndRejectsInvalidPlayerCounts() {
        assertTrue(CardGame.isValidPlayerCount("1"));
        assertTrue(CardGame.isValidPlayerCount("2"));
        assertFalse(CardGame.isValidPlayerCount("0"));
        assertFalse(CardGame.isValidPlayerCount("-1"));
        assertFalse(CardGame.isValidPlayerCount("abc"));
        assertFalse(CardGame.isValidPlayerCount(""));
    }

    @Test
    void createsCorrectNumberOfDecks() {
        List<CardDeck> oneDeck = CardGame.createDecks(1);
        List<CardDeck> threeDecks = CardGame.createDecks(3);

        assertEquals(1, oneDeck.size());
        assertEquals(1, oneDeck.get(0).getDeckId());
        assertEquals(3, threeDecks.size());
        assertEquals(1, threeDecks.get(0).getDeckId());
        assertEquals(3, threeDecks.get(2).getDeckId());
    }

    @Test
    void createsCorrectNumberOfPlayers() {
        List<CardDeck> oneDeck = CardGame.createDecks(1);
        List<CardDeck> threeDecks = CardGame.createDecks(3);

        List<Player> onePlayer = CardGame.createPlayers(1, oneDeck, new GameState(), new Object(), tempDirectory);
        List<Player> threePlayers = CardGame.createPlayers(3, threeDecks, new GameState(), new Object(), tempDirectory);

        assertEquals(1, onePlayer.size());
        assertEquals(1, onePlayer.get(0).getPlayerId());
        assertEquals(3, threePlayers.size());
        assertEquals(1, threePlayers.get(0).getPlayerId());
        assertEquals(3, threePlayers.get(2).getPlayerId());
    }

    @Test
    void roundRobinDealingGivesCorrectHandsAndDeckContents() {
        List<Card> pack = numberedPack(16);
        List<CardDeck> decks = CardGame.createDecks(2);
        List<Player> players = CardGame.createPlayers(2, decks, new GameState(), new Object(), tempDirectory);

        CardGame.dealCards(pack, players, decks);

        assertEquals(cards(1, 3, 5, 7), players.get(0).getHandSnapshot());
        assertEquals(cards(2, 4, 6, 8), players.get(1).getHandSnapshot());
        assertEquals(cards(9, 11, 13, 15), decks.get(0).snapshot());
        assertEquals(cards(10, 12, 14, 16), decks.get(1).snapshot());
    }

    @Test
    void playerNDiscardsToDeckOne() throws Exception {
        List<Card> pack = cards(1, 2, 3, 2, 4, 2, 5, 9, 8, 2, 6, 7, 8, 9, 10, 11);

        CardGame.runGame(2, pack, tempDirectory);

        List<String> playerTwoOutput = Files.readAllLines(tempDirectory.resolve("player2_output.txt"));
        assertTrue(playerTwoOutput.contains("player 2 discards a 9 to deck 1"));
    }


    @Test
    void runGameWithOnePlayerCreatesPlayerAndDeckOutputFiles() throws Exception {
        List<Card> pack = PackReader.readPack(Path.of("src/test/resources/packs/valid_immediate_win_1.txt"), 1);

        GameState gameState = CardGame.runGame(1, pack, tempDirectory);

        List<String> fileNames = fileNamesInTempDirectory();
        assertEquals(Arrays.asList("deck1_output.txt", "player1_output.txt"), fileNames);
        assertEquals(1, gameState.getWinnerId());
        List<String> playerOneOutput = Files.readAllLines(tempDirectory.resolve("player1_output.txt"));
        assertTrue(playerOneOutput.contains("player 1 wins"));
        assertTrue(playerOneOutput.contains("player 1 final hand: 1 1 1 1"));
    }
    @Test
    void runGameCreatesPlayerAndDeckOutputFiles() throws Exception {
        List<Card> pack = PackReader.readPack(Path.of("src/test/resources/packs/valid_immediate_win_2.txt"), 2);

        CardGame.runGame(2, pack, tempDirectory);

        List<String> fileNames = fileNamesInTempDirectory();
        assertEquals(Arrays.asList("deck1_output.txt", "deck2_output.txt",
                "player1_output.txt", "player2_output.txt"), fileNames);
    }

    @Test
    void immediateWinWorks() throws Exception {
        List<Card> pack = PackReader.readPack(Path.of("src/test/resources/packs/valid_immediate_win_2.txt"), 2);

        GameState gameState = CardGame.runGame(2, pack, tempDirectory);

        assertEquals(1, gameState.getWinnerId());
        List<String> playerOneOutput = Files.readAllLines(tempDirectory.resolve("player1_output.txt"));
        assertTrue(playerOneOutput.contains("player 1 wins"));
        assertTrue(playerOneOutput.contains("player 1 final hand: 1 1 1 1"));
    }

    @Test
    void eventualWinWorks() throws Exception {
        List<Card> pack = PackReader.readPack(Path.of("src/test/resources/packs/valid_eventual_win_2.txt"), 2);

        GameState gameState = CardGame.runGame(2, pack, tempDirectory);

        assertEquals(1, gameState.getWinnerId());
        List<String> playerOneOutput = Files.readAllLines(tempDirectory.resolve("player1_output.txt"));
        assertTrue(playerOneOutput.contains("player 1 draws a 1 from deck 1"));
        assertTrue(playerOneOutput.contains("player 1 discards a 9 to deck 2"));
        assertTrue(playerOneOutput.contains("player 1 current hand is 1 1 1 1"));
    }

    @Test
    void outputContainsRequiredLinesForNonWinner() throws Exception {
        List<Card> pack = PackReader.readPack(Path.of("src/test/resources/packs/valid_immediate_win_2.txt"), 2);

        CardGame.runGame(2, pack, tempDirectory);

        List<String> playerTwoOutput = Files.readAllLines(tempDirectory.resolve("player2_output.txt"));
        assertEquals("player 2 initial hand 2 2 2 2", playerTwoOutput.get(0));
        assertTrue(playerTwoOutput.contains("player 1 has informed player 2 that player 1 has won"));
        assertTrue(playerTwoOutput.contains("player 2 exits"));
        assertTrue(playerTwoOutput.contains("player 2 hand: 2 2 2 2"));
    }

    private List<Card> numberedPack(int size) {
        List<Card> pack = new ArrayList<Card>();
        for (int i = 1; i <= size; i++) {
            pack.add(new Card(i));
        }
        return pack;
    }

    private List<Card> cards(int... values) {
        List<Card> cards = new ArrayList<Card>();
        for (int value : values) {
            cards.add(new Card(value));
        }
        return cards;
    }

    private List<String> fileNamesInTempDirectory() {
        List<String> fileNames = new ArrayList<String>();
        File[] files = tempDirectory.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
        Collections.sort(fileNames);
        return fileNames;
    }
}




