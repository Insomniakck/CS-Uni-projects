
package gameItems.listeners;


import gameItems.Counter;
import gameItems.Game;
import interfaces.HitListener;
import shapes.Ball;
import shapes.Block;

/**
 * BlockRemover removes blocks from the game, as well as keeping count of the number of blocks that remain.
 */
public class BlockRemover implements HitListener {
    private Game game;
    private Counter remainingBlocks;

    /**
     * BlockRemover constructor.
     *
     * @param game            - the Game we remove Blocks from
     * @param remainingBlocks - the number of Blocks in the Game
     */
    public BlockRemover(Game game, Counter remainingBlocks) {
        this.game = game;
        this.remainingBlocks = remainingBlocks;
    }


    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        hitter.setColor(beingHit.getColor());
        remainingBlocks.decrease(1);
        beingHit.removeFromGame(game);
        beingHit.removeHitListener(this);
    }
}
