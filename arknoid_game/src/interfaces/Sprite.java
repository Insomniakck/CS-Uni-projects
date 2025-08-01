// 211315262 Sofia Tchernikov
package interfaces;

import gameItems.Game;
import biuoop.DrawSurface;

/**
 * Sprite Interface holds methods for Ball, Block, Paddle to implement.
 */
public interface Sprite {

    /**
     * draw the sprite to the screen.
     *
     * @param d - the given surface
     */
    void drawOn(DrawSurface d);


    /**
     * notify the sprite that time has passed.
     */
    void timePassed();


    /**
     * add the sprite to the appropriate List in Game Class.
     *
     * @param g - the given Game
     */
    void addToGame(Game g);

}