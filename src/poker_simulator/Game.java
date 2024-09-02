package poker_simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Thread {

	List<Card> deck;
	List<Player> lobby;
	List<Card> board;
	int hands, playerAmount;

	public Game(int hands, int playerAmount) {
		this.hands = hands;
		this.playerAmount = playerAmount;
	}

	@Override
	public void run() {
		double possibleHands = 0, actualHands = 0;
		for (int i = 1; i <= hands; i++) {
			String winner = "";
//		int i = 1;
//		while (!winner.contains("Straight flush")) {

			lobby = new ArrayList<Player>();
			board = new ArrayList<Card>();
			populateDeck();
			enrollPlayers(playerAmount);

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
				String handValue = player.calculateHandValue(board);
				System.out.println(player.getId() + ": " + player.printHand() + " > " + handValue);
			}

			Player win = calculateWinner();
			winner = win.getId() + "> " + win.calculateHandValue(board);
			System.out.println("\n\tWinner: " + winner);
		}
	}

	private String printBoard(List<Card> board) {
		StringBuilder print = new StringBuilder();
		if (board.size() == 0) {
			print.append(" - - - - - ");
		} else {
			print.append(" ");
			for (Card card : board) {
				print.append(card.getCard() + " ");
			}
		}
		return print.toString();
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

	private void enrollPlayers(int playerAmount) {
		for (int i = 1; i <= playerAmount; i++) {
			Player player = new Player("Player " + i, getHand());
			lobby.add(player);
		}
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
							}
//							If hand value is not two pair, then check to untie on another way
							else {
//								Check if hand value is higher than a set, because kicker is only taken into account until that value
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

//		If there is more than one player on the split players' list, then it is definitely a split
		if (split.size() > 1) {
//			Create a String with those players to print whose are splitting the pot
			StringBuilder id = new StringBuilder();
			id.append("Split between: ");
			for (Player player : split) {
				id.append(player.getId());
				if (!player.equals(split.get(split.size() - 1))) {
					id.append(" and ");
				}
			}
//			Create a new Player with this new info about the split
			winner = new Player(id.toString(), split.get(0).getHand());
		}

		return winner;
	}
}
