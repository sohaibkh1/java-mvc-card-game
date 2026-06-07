package exeter.comp.msc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {
    @TempDir
    Path tempDirectory;

    @Test
    void detectsWinningHand() {
        Player player = newPlayer(1);
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));

        assertTrue(player.hasWinningHand());
    }

    @Test
    void winningHandDoesNotNeedToMatchPreferredValue() {
        Player player = newPlayer(1);
        player.addCardToHand(new Card(7));
        player.addCardToHand(new Card(7));
        player.addCardToHand(new Card(7));
        player.addCardToHand(new Card(7));

        assertTrue(player.hasWinningHand());
    }

    @Test
    void chooseDiscardIndexChoosesNonPreferredCard() {
        Player player = newPlayer(1);
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(4));
        player.addCardToHand(new Card(1));

        assertEquals(2, player.chooseDiscardIndex());
    }

    @Test
    void handRemainsFourCardsAfterControlledDrawAndDiscard() throws Exception {
        List<Card> pack = new ArrayList<Card>();
        int[] values = {1, 2, 1, 2, 1, 3, 9, 4, 1, 5, 6, 7, 8, 9, 10, 11};
        for (int value : values) {
            pack.add(new Card(value));
        }

        CardGame.runGame(2, pack, tempDirectory);

        List<String> lines = Files.readAllLines(tempDirectory.resolve("player1_output.txt"));
        assertTrue(lines.contains("player 1 current hand is 1 1 1 1"));
    }

    @Test
    void outputFormatIsCorrectForImmediateWin() throws Exception {
        Player player = newPlayer(1);
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));
        player.addCardToHand(new Card(1));

        player.run();

        List<String> lines = Files.readAllLines(tempDirectory.resolve("player1_output.txt"));
        assertEquals("player 1 initial hand 1 1 1 1", lines.get(0));
        assertEquals("player 1 wins", lines.get(1));
        assertEquals("player 1 exits", lines.get(2));
        assertEquals("player 1 final hand: 1 1 1 1", lines.get(3));
    }

    private Player newPlayer(int playerId) {
        return new Player(
                playerId,
                new CardDeck(playerId),
                new CardDeck(playerId + 1),
                new GameState(),
                new Object(),
                tempDirectory
        );
    }
}
