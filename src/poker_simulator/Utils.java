package poker_simulator;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	Player player;

	public Utils(Player player) {
		this.player = player;
	}

	public String getIndexByValue(int value) {
		switch (value) {
		case 1: {
			return "A";
		}
		case 10: {
			return "T";
		}
		case 11: {
			return "J";
		}
		case 12: {
			return "Q";
		}
		case 13: {
			return "K";
		}
		case 14: {
			return "A";
		}
		default: {
			return null;
		}
		}
	}

	public Card hasStraight(List<Card> board) {
		List<Card> sortedBoard = new ArrayList<Card>();
		sortedBoard.addAll(board);

		sortedBoard.sort(new CardComparator());

		Card higherCard = null;
		int consecutiveCards = 1;
		for (Card card : sortedBoard) {
			if (sortedBoard.indexOf(card) == sortedBoard.size() - 1) {
				continue;
			}

			if (card.getValue() == sortedBoard.get(sortedBoard.indexOf(card) + 1).getValue()) {
				continue;
			}

			if (card.getValue() + 12 == sortedBoard.get(sortedBoard.size() - 1).getValue()) {
				consecutiveCards++;
			}

			if (card.getValue() == sortedBoard.get(sortedBoard.indexOf(card) + 1).getValue() - 1) {
				consecutiveCards++;
			} else {
				consecutiveCards = 1;
			}
			if (consecutiveCards > 4) {
				higherCard = sortedBoard.get(sortedBoard.indexOf(card) + 1);
			}
		}

		return higherCard;
	}

	public Card getHighCard(List<Card> hand) {
		if (hand.get(0).getValue() > hand.get(1).getValue()) {
			return hand.get(0);
		} else {
			return hand.get(1);
		}
	}

	public Card hasFlush(List<Card> board) {
		List<Card> sortedBoard = new ArrayList<Card>();
		sortedBoard.addAll(board);

		sortedBoard.sort(new CardComparator());
		String[] suits = { "♥", "♦", "♣", "♠" };
		Card higherCard = null;
		for (String suit : suits) {
			int matches = 0;
			for (Card card : sortedBoard) {
				if (card.getSuit().equals(suit)) {
					matches++;
					if (matches > 4) {
						higherCard = card;
					}
				}
			}
		}
		return higherCard;
	}

	public Card hasStraightFlush(List<Card> board) {
		List<Card> sortedBoard = new ArrayList<Card>();
		sortedBoard.addAll(board);

		sortedBoard.sort(new CardComparator());
		Card higherCard = null;
		String[] suits = { "♥", "♦", "♣", "♠" };
		for (String suit : suits) {
			int matches = 0;
			for (Card card : sortedBoard) {
				if (card.getSuit().equals(suit)) {
					if (sortedBoard.indexOf(card) == sortedBoard.size() - 1) {
						continue;
					}

					if (card.getValue() == sortedBoard.get(sortedBoard.indexOf(card) + 1).getValue()) {
						continue;
					}

					if (card.getValue() + 12 == sortedBoard.get(sortedBoard.size() - 1).getValue()) {
						matches++;
					}

					if (card.getValue() == sortedBoard.get(sortedBoard.indexOf(card) + 1).getValue() - 1) {
						matches++;
					} else {
						matches = 1;
					}

					if (matches > 4) {
						higherCard = card;
					}
				}
			}
		}

		return higherCard;
	}

	public String getMatches(Card card, List<Card> board) {
		boolean pair = false, threes = false;
		String result = "none";

		for (Card c : board) {
			if (c.equals(card)) {
				continue;
			}
			if (card.getIndex().equals(c.getIndex())) {
				if (!pair) {
					pair = true;
					result = "pairs";
				} else {
					if (!threes) {
						threes = true;
						result = "threes";
					} else {
						result = "poker";
					}
				}
			}
		}
		return result;
	}

	public String getFinalCombination(List<Card> board) {
		List<String> matches = new ArrayList<String>();

		for (Card card : board) {
			matches.add(getMatches(card, board));
		}

		String finalResult = "High " + getHighCard(player.hand).getIndex();
		int handValue = 1, cardValue = getHighCard(player.hand).getValue(), kickerValue = calculateKickerValue(board),
				seccondPairValue = 0;

		if (matches.contains("pairs")) {
			finalResult = "Pair of " + board.get(matches.indexOf("pairs")).getIndex() + "'s";
			handValue = 2;
			cardValue = board.get(matches.indexOf("pairs")).getValue();
			kickerValue = calculateKickerValue(board);
		}

		List<Card> pairedCards = new ArrayList<Card>();
		for (int i = 0; i < matches.size(); i++) {
			if (matches.get(i).equals("pairs")) {
				pairedCards.add(board.get(i));
			}
		}
		pairedCards.sort(new CardComparator());

		if (pairedCards.size() > 2) {
			finalResult = "Two pair of " + pairedCards.get(pairedCards.size() - 3).getIndex() + "'s and "
					+ pairedCards.get(pairedCards.size() - 1).getIndex() + "'s";
			handValue = 3;
			cardValue = pairedCards.get(pairedCards.size() - 1).getValue();
			seccondPairValue = pairedCards.get(pairedCards.size() - 3).getValue();

			if (pairedCards.size() == 6) {
				kickerValue = pairedCards.get(0).getValue();
			}
		}

		if (matches.contains("threes")) {
			finalResult = "Set of " + board.get(matches.indexOf("threes")).getIndex() + "'s";
			handValue = 4;
			cardValue = board.get(matches.indexOf("threes")).getValue();
		}

		Card straightCard = hasStraight(board);
		Card firstCard = null;
		if (straightCard != null) {
			for (Card c : board) {
				if (c.getValue() == straightCard.getValue() - 4) {
					firstCard = c;
					break;
				}
			}

			if (straightCard.getIndex().equals("5")) {
				finalResult = "Straight, " + "A to " + straightCard.getIndex();
			} else {
				finalResult = "Straight, " + firstCard.getIndex() + " to " + straightCard.getIndex();
			}
			cardValue = straightCard.getValue();
			handValue = 5;
			kickerValue = 0;
		}

		Card flushCard = hasFlush(board);
		if (flushCard != null) {
			handValue = 6;
			kickerValue = 0;

			List<Card> higherSuited = new ArrayList<Card>();
			for (Card card : player.getHand()) {
				if (card.getSuit().equals(flushCard.getSuit())) {
					higherSuited.add(card);
				}
			}

			if (higherSuited.size() == 2) {
				cardValue = getHighCard(higherSuited).getValue();
				finalResult = "Flush of " + flushCard.getSuitName().toLowerCase() + ", "
						+ getHighCard(higherSuited).getCard();
			} else if (higherSuited.size() == 1) {
				cardValue = higherSuited.get(0).getValue();
				finalResult = "Flush of " + flushCard.getSuitName().toLowerCase() + ", "
						+ higherSuited.get(0).getCard();
			} else if (higherSuited.size() == 0) {
				finalResult = "Flush of " + flushCard.getSuitName().toLowerCase() + ", " + flushCard.getCard();
			}
		}

		if (matches.contains("threes") && matches.contains("pairs")) {
			finalResult = "Full house, " + board.get(matches.indexOf("threes")).getIndex() + "'s over "
					+ board.get(matches.indexOf("pairs")).getIndex() + "'s";
			handValue = 7;
			kickerValue = 0;
		}

		if (matches.contains("poker")) {
			finalResult = "Poker of " + board.get(matches.indexOf("poker")).getIndex() + "'s";
			handValue = 8;
			cardValue = board.get(matches.indexOf("poker")).getValue();
			kickerValue = 0;
		}

		if ((straightCard != null && flushCard != null)) {
			Card straightFlush = hasStraightFlush(board);
			if (straightFlush != null) {
				for (Card c : board) {
					if (c.getValue() == straightFlush.getValue() - 4) {
						firstCard = c;
						break;
					}
				}

				finalResult = "Straight flush of " + straightFlush.getSuitName().toLowerCase() + ", "
						+ firstCard.getIndex() + " to " + straightFlush.getIndex();
				handValue = 9;
				cardValue = straightFlush.getValue();
				kickerValue = 0;

				if (straightFlush.getIndex().equals("A")) {
					finalResult = "Royal flush, all " + straightFlush.getSuitName().toLowerCase();
					handValue = 10;
				}
			}
		}

		player.setHandValue(handValue);
		player.setCardValue(cardValue);
		player.setKickerValue(kickerValue);
		player.setSeccondPairValue(seccondPairValue);

		return finalResult;
	}

	private synchronized int calculateKickerValue(List<Card> b) {
		List<Card> board = new ArrayList<Card>();
		board.addAll(b);

		int kickerValue = 0;

		if (player.hand.get(0).getIndex().equals(player.hand.get(1).getIndex())) {
			return 0;
		} else {
			for (Card card : player.hand) {
				board.remove(card);
			}

			Card discardedCard = null;
			Card discardedCard2 = null;
			for (Card handCard : player.hand) {
				for (Card card : board) {
					if (card.getIndex().equals(handCard.getIndex()) && discardedCard == null) {
						discardedCard = handCard;
						break;
					} else {
						if (card.getIndex().equals(handCard.getIndex()) && discardedCard != null) {
							discardedCard2 = handCard;
							break;
						}
					}
				}
			}

			if (discardedCard2 != null) {
				return 0;
			}

			if (discardedCard != null) {
				for (Card card : player.hand) {
					if (!card.equals(discardedCard)) {
						kickerValue = card.getValue();
					}
				}
			} else {
				for (Card card : player.hand) {
					if (player.getHandValue() > 1) {
						kickerValue = getHighCard(player.hand).getValue();
					} else {
						if (!card.equals(getHighCard(player.hand))) {
							kickerValue = card.getValue();
						}
					}
				}
			}
		}
		return kickerValue;
	}

	public synchronized List<Card> calculateBestHandPossible(List<Card> board) {
		return null;
	}

	private synchronized String getFlushSuit(List<Card> board) {
		String suit = null;
		String[] suits = { "♥", "♦", "♣", "♠" };

		for (String s : suits) {
			int matches = 0;
			for (Card card : board) {
				if (card.getSuit().equals(s)) {
					matches++;
				}
			}
			if (matches >= 3) {
				suit = s;
			}
		}
		return suit;
	}
	// 6 8 T Q A
	// 6 8 10 12 14

	// 6 7 8 12 14
	// 6 7 9 12 14
	public synchronized List<String> getHandForStraight(List<Card> board) {
		List<String> neededHand = new ArrayList<String>();
		List<Card> sortedBoard = new ArrayList<Card>();
		sortedBoard.addAll(board);
		sortedBoard.sort(new CardComparator());
		
		List<Integer> boardValues = new ArrayList<Integer>();
		for (Card card : sortedBoard) {
			boardValues.add(card.getValue());
		}
		
		List<List<Card>> straightCardsList = new ArrayList<List<Card>>();
		for (int compareValue : boardValues) {
			List<Card> straightCards = new ArrayList<Card>();
			for (int value : boardValues) {
				if (compareValue == value) {
					continue;
				}

				if (compareValue + 4 >= value) {
					straightCards.add(sortedBoard.get(boardValues.indexOf(value)));
				} else {
					straightCards = new ArrayList<Card>();
					break;
				}
			}
			if(straightCards.size() > 2) {
				straightCards = new ArrayList<Card>();
			} else {
				straightCardsList.add(straightCards);
			}
		}

		List<Integer> straightValues = new ArrayList<Integer>();
		for (List<Card> cardList : straightCardsList) {
			for(Card card : cardList) {
				straightValues.add(card.getValue());
			}
		}
		
		if(straightCardsList.size() > 0) {
			for(List<Card> straightCards : straightCardsList) {
				for (int i = straightCards.get(0).getValue(); i < straightCards.get(straightCards.size() - 1).getValue(); i++) {
					if(!straightValues.contains(i)) {
//						System.err.print("Missing " + i + " ");
						neededHand.add(String.valueOf(i));
					}
				}
				neededHand.add("|");
			}
		}
			
		return neededHand;
	}

	private synchronized int factorialOf(int n) {
		int result = 1;

		for (int factor = 2; factor <= n; factor++) {
			result *= factor;
		}

		return result;
	}
}