package poker_simulator;

import java.util.List;

public class Player {

	List<Card> hand;
	Utils utils;
	int handValue, cardValue, seccondPairValue, kickerValue;
	String id;

	public Player(String id, List<Card> hand) {
		this.id = id;
		this.handValue = 0;
		this.cardValue = 0;
		this.seccondPairValue = 0;
		this.kickerValue = 0;
		this.hand = hand;
		this.utils = new Utils(this);
	}

	public String getId() {
		return id;
	}

	public List<Card> getHand() {
		return hand;
	}

	public synchronized String printHand() {
		return hand.get(0).getCard() + " " + hand.get(1).getCard();
	}
	
	public int getHandValue() {
		return this.handValue;
	}
	
	public void setHandValue(int rate) {
		this.handValue = rate;
	}
	
	public int getCardValue() {
		return this.cardValue;
	}
	
	public void setCardValue(int rate) {
		this.cardValue = rate;
	}
	
	public int getSeccondPairValue() {
		return this.seccondPairValue;
	}
	
	public void setSeccondPairValue(int rate) {
		this.seccondPairValue = rate;
	}
	
	public int getKickerValue() {
		return this.kickerValue;
	}
	
	public void setKickerValue(int rate) {
		this.kickerValue = rate;
	}
	
	public synchronized String calculateHandValue(List<Card> board, boolean test) {
		if(!test) {
			board.add(hand.get(0));
			board.add(hand.get(1));
		}

		String cardComb = utils.getFinalCombination(board);

		if(!test) {
			board.remove(hand.get(0));
			board.remove(hand.get(1));
		}
		
//		if(board.size() == 5) {
//			List<List<Integer>> handsForStraight = utils.getHandsForStraight(board);
//			for (List<Card> hand : utils.getNeededHandsForPossibleStraights(handsForStraight)) {
//				for (Card card : hand) {
//					System.out.print(card.getCard() + " ");
//				}
//				System.out.println();
//			}
//		}
		
		return cardComb;
	}
}