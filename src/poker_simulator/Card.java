package poker_simulator;

public class Card {

	String index;
	String suit;
	int value;

	public Card(String index, String suit) {
		this.index = index;
		this.suit = suit;
		this.value = assignValue();
	}

	public String getCard() {
		return index.concat(suit);
	}

	public String getIndex() {
		return index;
	}

	public String getSuit() {
		return this.suit;
	}

	public String getSuitName() {
		String suitName = "";

		switch (suit) {
		case "♥": {
			suitName = "HEARTS";
			break;
		}
		case "♦": {
			suitName = "DIAMONDS";
			break;
		}
		case "♣": {
			suitName = "CLOPS";
			break;
		}
		case "♠": {
			suitName = "SPADES";
			break;
		}
		default: {
			suitName = "UNKNOWN";
			break;
		}
		}

		return suitName;
	}

	public int getValue() {
		return this.value;
	}

	private int assignValue() {
		try {
			return Integer.parseInt(index);
		} catch (NumberFormatException ex) {
			switch (index) {
			case "T": {
				return 10;
			}
			case "J": {
				return 11;
			}
			case "Q": {
				return 12;
			}
			case "K": {
				return 13;
			}
			case "A": {
				return 14;
			}
			default: {
				return 0;
			}
			}
		}
	}
}
