package gameItems.listeners;

import interfaces.HitListener;
import shapes.Block;
import shapes.Ball;
import gameItems.Counter;

/**
 * ScoreTrackingListener holds the player's score, adds 5 points every Block the player hits.
 */
public class ScoreTrackingListener implements HitListener {
    private Counter currentScore;

    /**
     * ScoreTrackingListener constructor.
     *
     * @param scoreCounter - reference to the score Counter from the Game
     */
    public ScoreTrackingListener(Counter scoreCounter) {
        this.currentScore = scoreCounter;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        currentScore.increase(5);
    }
}