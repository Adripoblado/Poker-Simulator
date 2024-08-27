package poker_simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

	Player player;
	String[] suits = { "♥", "♦", "♣", "♠" };

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
//		Create a new list to avoid working with the original one and modifying it
		List<Card> sortedBoard = new ArrayList<Card>();
		sortedBoard.addAll(board);

		sortedBoard.sort(new CardComparator());

//		Create a new card which, in case there is a straight, it will take the highest value of the straight
		Card higherCard = null;
		int consecutiveCards = 1;
		for (Card card : sortedBoard) {
//			Skip card if it's the first on the board
			if (sortedBoard.indexOf(card) == sortedBoard.size() - 1) {
				continue;
			}

//			Skip the card if it has the same value as the next card on the board
			if (card.getValue() == sortedBoard.get(sortedBoard.indexOf(card) + 1).getValue()) {
				continue;
			}

//			If the card is a 2 and there is an Ace on the board add one consecutive card
			if (card.getValue() + 12 == sortedBoard.get(sortedBoard.size() - 1).getValue()) {
				consecutiveCards++;
			}

//			If value of the current card is one point less than the next card add one consecutive card, otherwise straight is broken and consecutive card counter is set to 1
			if (card.getValue() == sortedBoard.get(sortedBoard.indexOf(card) + 1).getValue() - 1) {
				consecutiveCards++;
			} else {
				consecutiveCards = 1;
			}
//			If there is a straight, get the value of the card ahead of the current card. 
//			This is because consecutiveCards is initialized with value 1 (as we have to take into account that there is always one consecutive card), 
//			so when we calculate if the next card's value is a single point ahead of current, we need to take the index of the reference card plus 1 (the index of the last card on the straight)
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
		Card higherCard = null;
//		Count how many cards of each suit are on the board, if there is at least 5 of one, there is a flush of that suit (as it is impossible to have two different flushes)
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
		Card returnCard = null;
		List<Card> sortedBoard = new ArrayList<Card>();
		sortedBoard.addAll(board);

//		If there is an Ace on the board, create a new Card with Card.index = 1 to take the Ace into account on both high and low straights
		for (Card card : board) {
			if (card.getIndex().equals("A")) {
				sortedBoard.add(new Card("1", card.getSuit()));
			}
		}

		sortedBoard.sort(new CardComparator());

		Card ender = hasStraight(board);
		Card opener = null;
		if (ender != null) {
			for (Card card : sortedBoard) {
				if (card.getValue() == ender.getValue() - 4) {
					opener = card;
				}
			}
		}

		if (opener != null) {
			int suitedCards = 0;
			for (Card card : sortedBoard) {
				if (card == ender) {
					suitedCards++;
					break;
				}

				if (card.getValue() >= opener.getValue() && card.getSuit().equals(ender.getSuit())) {
					suitedCards++;
				}
			}

			if (suitedCards >= 5) {
				returnCard = ender;
			}
		} else {
			returnCard = null;
		}

		return returnCard;
	}

	public String getMatches(Card card, List<Card> board) {
		boolean pair = false, threes = false;
		String result = "none";

//		Go through all cards on the board (player's hand included), with one reference card, 
//		if we go through the reference card we skip it, then, if any other card on the board has the same card value, 
//		it is a match, and so a pair. If it was already a pair, it becomes a set, and, finally, if it was already a set, it becomes a poker
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
//		Create a list of all matching cards from the board (player hand cards included)
		List<String> matches = new ArrayList<String>();

		for (Card card : board) {
			matches.add(getMatches(card, board));
		}

//		Initialize finalResult with at least "High X" value; and do so with handValue, cardValue and calculate kickerValue previously
		String finalResult = "High " + getHighCard(player.hand).getIndex();
		int handValue = 1, cardValue = getHighCard(player.hand).getValue(), kickerValue = calculateKickerValue(board),
				seccondPairValue = 0;

//		Start from the combinations with the lowest value and then going up until Royal Flush
		if (matches.contains("pairs")) {
			finalResult = "Pair of " + board.get(matches.indexOf("pairs")).getIndex() + "'s";
			handValue = 2;
			cardValue = board.get(matches.indexOf("pairs")).getValue();
			kickerValue = calculateKickerValue(board);
		}

//		Create a list of matches to know if, by measuring it size, has more than one pair so it can contain a two pair
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

//			If there are 6 paired cards, which means that on the board exists 3 different pairs, player must have a pocket pair, this means that the kicker is null
			if (pairedCards.size() == 6) {
				kickerValue = pairedCards.get(0).getValue();
			}
		}

//		Simply check if there is a set on the match list
		if (matches.contains("threes")) {
			finalResult = "Set of " + board.get(matches.indexOf("threes")).getIndex() + "'s";
			handValue = 4;
			cardValue = board.get(matches.indexOf("threes")).getValue();
		}

//		Get highest card of a straight, if it is possible, then check if there is an Ace involved and if it is an opener or an ender to add an extra non-existent card into account
		Card straightCard = hasStraight(board);
		Card firstCard = null;
		if (straightCard != null) {
//			Get the opener to put it into the result String
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
//			From straight up, kicker has absolute no value on any hand
			kickerValue = 0;
		}

//		Check if there is a flush and get the highest card of that flush
		Card flushCard = hasFlush(board);
		if (flushCard != null) {
			handValue = 6;
			kickerValue = 0;

//			Get the highest suited with the flush card on the player's hand to know the card value in case anyone else has a flush
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

//		If on matches list exists a set and a pair, there is a full-house
		if (matches.contains("threes") && matches.contains("pairs")) {
			finalResult = "Full house, " + board.get(matches.indexOf("threes")).getIndex() + "'s over "
					+ board.get(matches.indexOf("pairs")).getIndex() + "'s";
			handValue = 7;
			kickerValue = 0;
		}

//		Simply check if there is a poker on the board
		if (matches.contains("poker")) {
			finalResult = "Poker of " + board.get(matches.indexOf("poker")).getIndex() + "'s";
			handValue = 8;
			cardValue = board.get(matches.indexOf("poker")).getValue();
			kickerValue = 0;
		}

//		If there is a straight and a flush on the table, we then need to check if there is a straight flush.
//		For this the straight must be all suited
		if ((straightCard != null && flushCard != null)) {
			Card straightFlush = hasStraightFlush(board);
			if (straightFlush != null) {
				for (Card c : board) {
					if (straightFlush.getValue() == 5) {
						if (c.getValue() == 14) {
							firstCard = c;
							break;
						} else {
							continue;
						}
					}

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

//		If the player has a pocket pair, then there is no kicker, so value = 0
		if (player.hand.get(0).getIndex().equals(player.hand.get(1).getIndex())) {
			return 0;
		} else {
//			Remove hand from board
			for (Card card : player.hand) {
				board.remove(card);
			}

//			Create two null cards, in case there is a match within some hand card and a card on the board, those cards are assigned a value
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

//			If both cards have matched with any of the board, then there is no kicker possible (this means that player have, at least, a two pair)
			if (discardedCard2 != null) {
				return 0;
			}

//			If only one card from the player's hand matched any of the cards on the board, then the other card from the hand is the kicker
			if (discardedCard != null) {
				for (Card card : player.hand) {
					if (!card.equals(discardedCard)) {
						kickerValue = card.getValue();
					}
				}
			}
//			If none of the cards matched any of the cards on the board, kicker card will be the one with the least value 
//			(this has a purpose, if two or more players has the same high card, then kicker comes into action to untie the board)
			else {
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
//		TODO
		return null;
	}

	private synchronized String getFlushSuit(List<Card> board) {
		String suit = null;

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

	public synchronized List<List<Integer>> getHandsForStraight(List<Card> board) {
//		Create a new list of cards to avoid sorting the original and changing board order
		List<Card> sortedBoard = new ArrayList<Card>();
		sortedBoard.addAll(board);
		sortedBoard.sort(new CardComparator());

//		Translate cards into int values (obtained from Card.getValue();) to make it easier to manage cards order
		List<Integer> boardValues = new ArrayList<Integer>();
		for (Card card : sortedBoard) {
//			If current card is an Ace then we add an extra value to our list to take into account low straights (Ace to five)
			if (card.getValue() == 14) {
				boardValues.add(1);
			}
			boardValues.add(card.getValue());
		}

//		Create a new Card with Card.index = 1 if there is any Ace in the board to match boardValues size and take low straights into account
		if (boardValues.contains(1) && boardValues.contains(14)) {
			sortedBoard.add(new Card("1", "#"));
			sortedBoard.sort(new CardComparator());
		}
		Collections.sort(boardValues);

//		A list of hands (included in another list of card values List<Integer>)
		List<List<Integer>> combinationList = new ArrayList<List<Integer>>();
		for (Card referenceCard : sortedBoard) {
			if (referenceCard.equals(sortedBoard.get(sortedBoard.size() - 2))) {
				break;
			}
			List<Integer> possibleHand = new ArrayList<Integer>();
			possibleHand.add(referenceCard.getValue());

			for (int i = sortedBoard.indexOf(referenceCard); i < sortedBoard.size(); i++) {
				int comparedCard = boardValues.get(i);
//			for (int comparedCard : boardValues) {
				if (referenceCard.getValue() == comparedCard) {
					continue;
				}

//				Check if board card value is among +-4 the reference card value (this means that current card can form a straight with the card we took as a reference on the previous for() loop)
				if (comparedCard <= referenceCard.getValue() + 4 && comparedCard >= referenceCard.getValue() - 4) {
//					If the card exceeds the value of the reference card so they cannot form a straight loop is broken, saving all the cards that are valid to form a straight
					if (possibleHand.size() < 3 && possibleHand.get(0) < comparedCard - 4) {
						break;
					}

//					If there is more than one hand with the same value (e.g. two Jacks) it is not added to the list and we skip into the next card on the board
					if (!possibleHand.contains(comparedCard)) {
						possibleHand.add(comparedCard);
						continue;
					}

//					If there is at least 3 cards on the candidates list (which means that the possibility of a straight within a hand exists) then we check if all those cards are compatible between themselves
					if (possibleHand.size() > 2) {
//						Create a card value list with those cards which may be removed from the final list due to incompatibilities
						List<Integer> removeList = new ArrayList<Integer>();
						for (int card : possibleHand) {
							if (card == comparedCard) {
								continue;
							}
//							Check if the card can form a straight with the highest card over the candidates
							if (card < possibleHand.get(possibleHand.size() - 1) - 4) {
								removeList.add(card);
							}
						}
						possibleHand.removeAll(removeList);
					}
				}
			}

//			Once all combinations are examinated and incompatibilities are removed, check if the combination remains as big as it is needed to be able to form a straight with (max) two more cards
			if (possibleHand.size() >= 3) {
				Collections.sort(possibleHand);
				combinationList.add(possibleHand);
			}
		}

//		Create a Set<List<Integer>> to remove duplicate combinations
		Set<List<Integer>> set = new HashSet<>(combinationList);
		combinationList.clear();
		combinationList.addAll(set);

		return combinationList;
	}

	public synchronized List<List<Card>> getNeededHandsForPossibleStraights(List<List<Integer>> possibleStraightList) {
//		A List<> where needed hands for a straight (List<Card>) are stored
		List<List<Card>> neededHands = new ArrayList<List<Card>>();

//		Check if there are possible combinations, otherwise, nothing happens
		if (possibleStraightList != null && possibleStraightList.size() > 0) {
//			Go through every possible straight combination
			for (List<Integer> hand : possibleStraightList) {
//				For every combination, go through all its cards
				for (int cardValue : hand) {
					List<Card> board = new ArrayList<Card>();

//					Get as a maximum reference value the value of the current card -4
					int maxValue = hand.get(hand.size() - 1) + 4;
					if (maxValue > 14) {
						maxValue = 14;
					}

//					Get as minimum reference value the value of the current card + 4
					int minValue = cardValue - 4;
					if (minValue < 1) {
						minValue = 1;
					}

//					Variable to count the number of carts needed to form a straight
					int handCardCount = 0;

//					Start the loop between minimum and maximum values
					for (int i = minValue; i <= maxValue; i++) {
//						If the card is already on the table, then a card with no suit is created and added to the board
						if (hand.contains(i)) {
							board.add(new Card(String.valueOf(i), ""));
						}
//						If the card is not currently on the table, a new card is created with a # as suit to distinguish it from existent cards
						else {
							board.add(new Card(String.valueOf(i), "#"));
							handCardCount++;
						}
//						When the board reaches 5 cards break the loop to check for straights
						if (board.size() == 5) {
							break;
						}
					}

//					If board contains a value straight and there are no more than 2 non-existant hands on the supossed board, add the board into the list (if is not already in it)
					if (hasStraight(board) != null && handCardCount <= 2) {
						if (!neededHands.contains(board)) {
							neededHands.add(board);
						}
					}
				}
			}
		}

//		To clear duplicates and remove cards that are already on the table, create a new list of hands, go through all previous 
//		hands which has valid straights and get only those cards which are marked with a #, then add them into a new hand and add that hand into our list
		List<List<Card>> finalHands = new ArrayList<List<Card>>();
		for (List<Card> h : neededHands) {
			List<Card> hand = new ArrayList<Card>();
			for (Card card : h) {
				if (card.getSuit().equals("#")) {
					for (String suit : suits) {
						hand.add(new Card(card.getIndex(), suit));
					}
				}
			}
			finalHands.add(hand);
		}

		Set<List<Card>> set = new HashSet<>(finalHands);
		finalHands.clear();
		finalHands.addAll(set);

		return finalHands;
	}

	private synchronized long factorialOf(int n) {
		int result = 1;

		for (int factor = 2; factor <= n; factor++) {
			result *= factor;
		}

		return result;
	}
}