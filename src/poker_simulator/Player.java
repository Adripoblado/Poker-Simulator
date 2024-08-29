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

	public synchronized String calculateHandValue(List<Card> board) {
		board.addAll(this.hand);
		String cardComb = utils.getFinalCombination(board);
		board.removeAll(this.hand);
		return cardComb;
	}
}