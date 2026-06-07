package exeter.comp.msc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardTest {
    @Test
    void storesValueCorrectly() {
        Card card = new Card(7);

        assertEquals(7, card.getValue());
    }

    @Test
    void rejectsNegativeCardValue() {
        assertThrows(IllegalArgumentException.class, () -> new Card(-1));
    }

    @Test
    void toStringReturnsValueAsString() {
        assertEquals("12", new Card(12).toString());
    }

    @Test
    void equalsAndHashCodeUseCardValue() {
        Card first = new Card(3);
        Card second = new Card(3);
        Card different = new Card(4);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
    }
}

