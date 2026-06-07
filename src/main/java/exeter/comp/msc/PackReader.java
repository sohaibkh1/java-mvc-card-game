package exeter.comp.msc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackReader {
    private PackReader() {
    }

    public static List<Card> readPack(Path path, int numberOfPlayers)
            throws IOException, InvalidPackException {
        if (numberOfPlayers <= 0) {
            throw new InvalidPackException("Number of players must be positive.");
        }
        if (path == null) {
            throw new InvalidPackException("Pack file path cannot be null.");
        }
        if (!Files.exists(path)) {
            throw new InvalidPackException("Pack file does not exist.");
        }
        if (!Files.isReadable(path)) {
            throw new InvalidPackException("Pack file is not readable.");
        }

        List<String> lines = Files.readAllLines(path);
        int expectedSize = 8 * numberOfPlayers;
        if (lines.size() != expectedSize) {
            throw new InvalidPackException("Pack must contain exactly " + expectedSize + " cards.");
        }

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            cards.add(readCardFromLine(lines.get(i), i + 1));
        }
        return cards;
    }

    private static Card readCardFromLine(String line, int lineNumber) throws InvalidPackException {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidPackException("Line " + lineNumber + " is blank.");
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length != 1) {
            throw new InvalidPackException("Line " + lineNumber + " must contain one integer.");
        }

        try {
            int value = Integer.parseInt(parts[0]);
            if (value < 0) {
                throw new InvalidPackException("Line " + lineNumber + " contains a negative card value.");
            }
            return new Card(value);
        } catch (NumberFormatException e) {
            throw new InvalidPackException("Line " + lineNumber + " is not an integer.");
        }
    }
}



