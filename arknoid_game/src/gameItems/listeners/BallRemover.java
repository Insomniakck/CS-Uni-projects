
package gameItems.listeners;


import gameItems.Counter;
import gameItems.Game;
import interfaces.HitListener;
import shapes.Ball;
import shapes.Block;

/**
 * BallRemover removes balls from the game, and keeps count of the number of balls that remain.
 */
public class BallRemover implements HitListener {
    private Game game;
    private Counter remainingBalls;

    /**
     * BallRemover constructor.
     *
     * @param game           - the game we remove a Ball from
     * @param remainingBalls - the number of balls in the game
     */
    public BallRemover(Game game, Counter remainingBalls) {
        this.game = game;
        this.remainingBalls = remainingBalls;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        remainingBalls.decrease(1);
        hitter.removeFromGame(game);
    }
}
