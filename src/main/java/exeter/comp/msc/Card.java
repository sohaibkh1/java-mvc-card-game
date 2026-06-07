package exeter.comp.msc;

public final class Card {
    private final int value;

    public Card(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Card value cannot be negative.");
        }
        // The value never changes, so Card is safe to share between threads.
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Card)) {
            return false;
        }
        Card card = (Card) other;
        return value == card.value;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(value).hashCode();
    }
}
