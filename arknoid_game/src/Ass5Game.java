// 211315262 Sofia Tchernikov

import gameItems.Game;

/**
 * initiate and run the Game.
 */
public class Ass5Game {
    /**
     * initiate and run the Game.
     *
     * @param args - no args required
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.initialize();
        game.run();
    }
}
