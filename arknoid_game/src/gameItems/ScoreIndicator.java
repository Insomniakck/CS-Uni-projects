package gameItems;

import interfaces.Sprite;
import biuoop.DrawSurface;

import java.awt.Color;

/**
 * sprite that holds a reference to the game score and prints it on the screen.
 */
public class ScoreIndicator implements Sprite {
    private Counter score;

    /**
     * ScoreIndicator constructor.
     *
     * @param score - reference to the game score
     */
    public ScoreIndicator(Counter score) {
        this.score = score;
    }

    @Override
    public void drawOn(DrawSurface d) {
        d.setColor(Color.BLACK);
        d.drawText(380, 15, "Score: " + score.getValue(), 15);
    }

    @Override
    public void timePassed() {
    }


    @Override
    public void addToGame(Game g) {
        g.addSprite(this);
    }
}
