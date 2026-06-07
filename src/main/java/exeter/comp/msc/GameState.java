package exeter.comp.msc;

public class GameState {
    private boolean gameOver;
    private int winnerId;

    public synchronized boolean declareWinner(int playerId) {
        if (playerId <= 0) {
            throw new IllegalArgumentException("Player id must be positive.");
        }
        if (gameOver) {
            return false;
        }

        // The first player to set the winner keeps it. Later calls cannot overwrite it.
        gameOver = true;
        winnerId = playerId;
        return true;
    }

    public synchronized boolean isGameOver() {
        return gameOver;
    }

    public synchronized int getWinnerId() {
        return winnerId;
    }
}

