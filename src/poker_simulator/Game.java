package poker_simulator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Thread {

	List<Card> deck;
	List<Player> lobby;
	List<Card> board;
	int hands;

	public Game(int hands) {
		this.hands = hands;
	}

	@Override
	public void run() {
//		double royalFlushes = 0;
//		for (int i = 1; i <= hands; i++) {
			String winner = "";
		int i = 1;
		while (!winner.contains("Straight flush")) {

			lobby = new ArrayList<Player>();
			board = new ArrayList<Card>();
			populateDeck();
			enrollPlayers();

			System.out.println("\tHAND N" + i);
			System.out.println("PRE-FLOP");
			System.out.println("-----------------------------------");
			System.out.println("\t" + printBoard(board));
			for (Player player : lobby) {
				System.out
						.println(player.getId() + ": " + player.printHand() + " > " + player.calculateHandValue(board));
			}

			runBoard(true);
			System.out.println("\nFLOP");
			System.out.println("-----------------------------------");
			System.out.println("\t" + printBoard(board));
			for (Player player : lobby) {
				System.out
						.println(player.getId() + ": " + player.printHand() + " > " + player.calculateHandValue(board));
			}

			runBoard(false);
			System.out.println("\nTURN");
			System.out.println("-----------------------------------");
			System.out.println("\t" + printBoard(board));
			for (Player player : lobby) {
				System.out
						.println(player.getId() + ": " + player.printHand() + " > " + player.calculateHandValue(board));
			}

			runBoard(false);
			System.out.println("\nRIVER");
			System.out.println("-----------------------------------");
			System.out.println("\t" + printBoard(board));
			for (Player player : lobby) {
				System.out
						.println(player.getId() + ": " + player.printHand() + " > " + player.calculateHandValue(board));
			}

			Player win = calculateWinner();
			winner = win.getId() + "> " + win.calculateHandValue(board);
			System.out.println("\n\tWinner: " + winner);

//			if (winner.contains("Royal flush")) {
//				royalFlushes++;
//			}
			i++;
		}

//		System.out.println("Total royal flushes on " + hands + " hands: " + royalFlushes);
//		DecimalFormat df = new DecimalFormat("0.0000000000");
//		System.out.println("Royal flush %: " + df.format(royalFlushes / hands));
	}

	private String printBoard(List<Card> board) {
		String print = "";
		if (board.size() == 0) {
			print = " - - - - -";
		} else if (board.size() == 3) {
			print = " " + board.get(0).getCard() + " " + board.get(1).getCard() + " " + board.get(2).getCard() + " - -";
		} else if (board.size() == 4) {
			print = " " + board.get(0).getCard() + " " + board.get(1).getCard() + " " + board.get(2).getCard() + " "
					+ board.get(3).getCard() + " -";
		} else if (board.size() == 5) {
			print = " " + board.get(0).getCard() + " " + board.get(1).getCard() + " " + board.get(2).getCard() + " "
					+ board.get(3).getCard() + " " + board.get(4).getCard();
		}
		return print;
	}

	private void runBoard(boolean flop) {
		Random random = new Random();

		if (flop) {
			for (int i = 0; i < 3; i++) {
				Card card = deck.get(random.nextInt(deck.size()));
				board.add(card);
				deck.remove(card);
			}
		} else {
			deck.remove(random.nextInt(deck.size()));
			Card card = deck.get(random.nextInt(deck.size()));
			board.add(card);
			deck.remove(card);
		}
	}

	private void populateDeck() {
		deck = new ArrayList<Card>();
		String[] cards = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };
		String[] suit = { "♥", "♦", "♣", "♠" };

		for (String card : cards) {
			for (String s : suit) {
				deck.add(new Card(card, s));
			}
		}
	}

	private synchronized List<Card> getHand() {
		List<Card> hand = new ArrayList<Card>();
		Random random = new Random();

		Card c1 = deck.get(random.nextInt(deck.size()));
		deck.remove(c1);
		hand.add(c1);
		Card c2 = deck.get(random.nextInt(deck.size()));
		deck.remove(c2);
		hand.add(c2);

		return hand;
	}

	private void enrollPlayers() {
		Player player1 = new Player("Player 1", getHand());
		lobby.add(player1);
		Player player2 = new Player("Player 2", getHand());
		lobby.add(player2);
		Player player3 = new Player("Player 3", getHand());
		lobby.add(player3);
		Player player4 = new Player("Player 4", getHand());
		lobby.add(player4);
		Player player5 = new Player("Player 5", getHand());
		lobby.add(player5);
		Player player6 = new Player("Player 6", getHand());
		lobby.add(player6);
		Player player7 = new Player("Player 7", getHand());
		lobby.add(player7);
	}

	private synchronized Player calculateWinner() {
		List<Player> split = new ArrayList<Player>();
		Player winner = null;

		for (Player player : lobby) {
			if (player.equals(lobby.get(0))) {
				winner = player;
				continue;
			}

//			If current player's hand has more value than previous player (by array order, e.g. Player2 > Player1), temporary winner is current player (until it reaches end of array) 
			if (player.getHandValue() > winner.getHandValue()) {
				winner = player;
			} else {
//				If current player's hand is the same as the hand from the previous player (e.g. both have two pairs), then we read the card value to know which combination is stronger
				if (player.getHandValue() == winner.getHandValue()) {
					if (player.getCardValue() > winner.getCardValue()) {
						winner = player;
					}
//					If both combinations have the same value (e.g. both have two pairs of A's X's) then we check second pair
					else {
						if (player.getCardValue() == winner.getCardValue()) {
							if (player.getHandValue() == 3) {
								if (player.getSeccondPairValue() > winner.getSeccondPairValue()) {
									winner = player;
								} else {
//									In case both players have same second pair (if they have two pair), we check for kicker to determine who's the winner
									if (player.getSeccondPairValue() == winner.getSeccondPairValue()) {
										if (player.getKickerValue() > winner.getKickerValue()) {
											winner = player;
										}
//										If all possible values matches, then a new Player is created merging equal value players
										else {
											if (player.getKickerValue() == winner.getKickerValue()) {
												if (!split.contains(winner)) {
													split.add(winner);
												}
												split.add(player);
												winner = player;
											}
										}
									}
								}
							} else {
								if (player.getHandValue() > 4) {
									winner = new Player("Split, " + player.getId() + " and " + winner.getId(),
											player.getHand());
									winner.setHandValue(player.getHandValue());
									winner.setCardValue(player.getCardValue());
									winner.setKickerValue(player.getKickerValue());
									winner.setSeccondPairValue(player.getSeccondPairValue());
								} else {
									if (player.getKickerValue() > winner.getKickerValue()) {
										winner = player;
									} else {
										if (player.getKickerValue() == winner.getKickerValue()) {
											winner = new Player("Split, " + player.getId() + " and " + winner.getId(),
													player.getHand());
											winner.setHandValue(player.getHandValue());
											winner.setCardValue(player.getCardValue());
											winner.setKickerValue(player.getKickerValue());
											winner.setSeccondPairValue(player.getSeccondPairValue());
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (split.size() > 1) {
			StringBuilder id = new StringBuilder();
			id.append("Split between: ");
			for (Player player : split) {
				id.append(player.getId());
				if (!player.equals(split.get(split.size() - 1))) {
					id.append(" and ");
				}
			}
			winner = new Player(id.toString(), split.get(0).getHand());
		}

		return winner;
	}
}
