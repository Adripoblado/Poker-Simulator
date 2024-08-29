package poker_simulator;

public class App {

	public static void main(String[] args) {
		Game game = new Game(1000000, 7);
		game.run();
	}
}
