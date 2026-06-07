package exeter.comp.msc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PackReaderTest {
    @TempDir
    Path tempDirectory;
    @Test
    void rejectsZeroPlayers() {
        Path packPath = Path.of("src/test/resources/packs/invalid_zero_players_0.txt");

        assertThrows(InvalidPackException.class, () -> PackReader.readPack(packPath, 0));
    }

    @Test
    void acceptsOnePlayerPack() throws Exception {
        Path packPath = Path.of("src/test/resources/packs/valid_immediate_win_1.txt");

        List<Card> pack = PackReader.readPack(packPath, 1);

        assertEquals(8, pack.size());
        assertEquals(new Card(1), pack.get(0));
    }

    @Test
    void acceptsValidPackWithExactlyEightNCards() throws Exception {
        Path packPath = Path.of("src/test/resources/packs/valid_immediate_win_2.txt");

        List<Card> pack = PackReader.readPack(packPath, 2);

        assertEquals(16, pack.size());
        assertEquals(new Card(1), pack.get(0));
    }

    @Test
    void rejectsWrongSizePack() {
        Path packPath = Path.of("src/test/resources/packs/invalid_wrong_size_2.txt");

        assertThrows(InvalidPackException.class, () -> PackReader.readPack(packPath, 2));
    }

    @Test
    void rejectsNegativeCardValue() {
        Path packPath = Path.of("src/test/resources/packs/invalid_negative_card_2.txt");

        assertThrows(InvalidPackException.class, () -> PackReader.readPack(packPath, 2));
    }

    @Test
    void rejectsNonIntegerRow() {
        Path packPath = Path.of("src/test/resources/packs/invalid_non_integer_2.txt");

        assertThrows(InvalidPackException.class, () -> PackReader.readPack(packPath, 2));
    }

    @Test
    void rejectsBlankRow() throws IOException {
        Path packPath = writePack("1", "2", "", "4", "1", "2", "1", "2",
                "1", "2", "1", "2", "1", "2", "1", "2");

        assertThrows(InvalidPackException.class, () -> PackReader.readPack(packPath, 2));
    }

    @Test
    void rejectsMultipleValuesOnOneRow() throws IOException {
        Path packPath = writePack("1", "2", "3 4", "4", "1", "2", "1", "2",
                "1", "2", "1", "2", "1", "2", "1", "2");

        assertThrows(InvalidPackException.class, () -> PackReader.readPack(packPath, 2));
    }

    @Test
    void acceptsCardValuesGreaterThanNumberOfPlayers() throws Exception {
        Path packPath = writePack("1", "2", "20", "4", "1", "2", "1", "2",
                "1", "2", "1", "2", "1", "2", "1", "2");

        List<Card> pack = PackReader.readPack(packPath, 2);

        assertEquals(new Card(20), pack.get(2));
    }

    private Path writePack(String... lines) throws IOException {
        Path packPath = tempDirectory.resolve("pack.txt");
        Files.write(packPath, Arrays.asList(lines));
        return packPath;
    }
}


