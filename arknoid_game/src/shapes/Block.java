// 211315262 Sofia Tchernikov

package shapes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import gameItems.Game;
import interfaces.Collidable;
import interfaces.HitListener;
import interfaces.HitNotifier;
import interfaces.Sprite;
import shapes.basics.Velocity;
import shapes.basics.Point;



import biuoop.DrawSurface;

/**
 * Block Class holds information about a collidable rectangle.
 */
public class Block implements Collidable, Sprite, HitNotifier {

    private Rectangle block;
    private Color color;
    private List<HitListener> hitListeners = new ArrayList<>();


    /**
     * Block constructor.
     *
     * @param block - rectangle
     * @param color - color
     */
    public Block(Rectangle block, Color color) {
        Rectangle b = new Rectangle(block.getUpperLeft().getX(), block.getUpperLeft().getY(),
                block.getWidth(), block.getHeight());
        this.block = b;
        this.color = color;
    }

    /**
     * checks if this block and a given ball are of the same color.
     *
     * @param ball - the ball that hit the block
     * @return - true if their colors are the same, false otherwise
     */
    public boolean ballColorMatch(Ball ball) {
        return ball.getColor().getRGB() == this.color.getRGB();
    }

    /**
     * @return - the Color of the Block.
     */
    public Color getColor() {
        return color;
    }

    /**
     * removes the block from the game.
     *
     * @param game - the game we remove the block from
     */
    public void removeFromGame(Game game) {
        game.removeCollidable(this);
        game.removeSprite(this);
    }

    //~~~~~~~~~~~~~~HitNotifier methods~~~~~~~~~
    @Override
    public void addHitListener(HitListener hl) {
        if (hitListeners == null) {
            hitListeners = new ArrayList<>();
        }
        hitListeners.add(hl);
    }

    @Override
    public void removeHitListener(HitListener hl) {
        hitListeners.remove(hl);
    }


    private void notifyHit(Ball hitter) {
        // Make a copy of the hitListeners before iterating over them.
        List<HitListener> listeners = new ArrayList<>(this.hitListeners);
        // Notify all listeners about a hit event:
        for (HitListener hl : listeners) {
            hl.hitEvent(this, hitter);
        }
    }

    //~~~~~~~~~~~~~Sprite methods~~~~~~~~~~~~~~

    @Override
    public void drawOn(DrawSurface d) {
        d.setColor(color);
        d.fillRectangle((int) block.getUpperLeft().getX(), (int) block.getUpperLeft().getY(),
                (int) block.getWidth(), (int) block.getHeight());
        d.setColor(Color.BLACK);
        d.drawRectangle((int) block.getUpperLeft().getX(), (int) block.getUpperLeft().getY(),
                (int) block.getWidth(), (int) block.getHeight());
    }

    @Override
    public void timePassed() {
    }

    @Override
    public void addToGame(Game g) {
        g.addCollidable(this);
        g.addSprite(this);
    }


    //~~~~~~~~~~~~~~Collidable methods~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Rectangle getCollisionRectangle() {
        Rectangle b = new Rectangle(block.getUpperLeft().getX(), block.getUpperLeft().getY(),
                block.getWidth(), block.getHeight());
        return b;
    }

    @Override
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        Velocity updatedVelocity;
        // checks if the collision is with the top/bottom border, or left/right border.
        //changes the velocity accordingly
        if (block.getBorder(0).insideLine(collisionPoint)) {
            updatedVelocity = new Velocity(currentVelocity.getDx(), -Math.abs(currentVelocity.getDy()));
        } else if (block.getBorder(1).insideLine(collisionPoint)) {
            updatedVelocity = new Velocity(currentVelocity.getDx(), Math.abs(currentVelocity.getDy()));
        } else if (block.getBorder(2).insideLine(collisionPoint)) {
            updatedVelocity = new Velocity(-Math.abs(currentVelocity.getDx()), currentVelocity.getDy());
        } else {
            updatedVelocity = new Velocity(Math.abs(currentVelocity.getDx()), currentVelocity.getDy());

        }
        if (!ballColorMatch(hitter)) {
            this.notifyHit(hitter);
        }
        return updatedVelocity;
    }

    @Override
    public Boolean isPaddle() {
        return false;
    }

}
