package exeter.comp.msc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateTest {
    @Test
    void firstPlayerCanDeclareWinner() {
        GameState gameState = new GameState();

        assertTrue(gameState.declareWinner(1));
        assertTrue(gameState.isGameOver());
        assertEquals(1, gameState.getWinnerId());
    }

    @Test
    void secondPlayerCannotOverwriteExistingWinner() {
        GameState gameState = new GameState();

        gameState.declareWinner(1);

        assertFalse(gameState.declareWinner(2));
        assertEquals(1, gameState.getWinnerId());
    }

    @Test
    void rejectsNonPositiveWinnerId() {
        GameState gameState = new GameState();

        assertThrows(IllegalArgumentException.class, () -> gameState.declareWinner(0));
    }
}

