// 211315262 Sofia Tchernikov
package shapes;


import gameItems.Game;
import gameItems.collections.CollisionInfo;
import gameItems.collections.GameEnvironment;
import interfaces.Sprite;
import shapes.basics.Line;
import shapes.basics.Point;
import shapes.basics.Velocity;
import biuoop.DrawSurface;
import java.awt.Color;
import java.util.Random;

/**
 * class Ball - has the parameters: Center, Radius, Color, and Velocity.
 */
public class Ball implements Sprite {

    private Point center;
    private int radius;
    private java.awt.Color color;
    private GameEnvironment gameEnv;
    //default velocity set to (1,1)
    private Velocity velocity = new Velocity(1, 1);


    /**
     * constructor for Ball.
     *
     * @param x     - x value of center
     * @param y     - y value of center
     * @param r     - the radius
     * @param color the color
     */
    public Ball(int x, int y, int r, java.awt.Color color) {
        this.center = new Point(x, y);
        this.radius = r;
        this.color = color;
    }

    /**
     * sets a reference for a GameEnvironment object.
     *
     * @param g - GameEnvironment object
     */
    public void setBallEnvironment(GameEnvironment g) {
        this.gameEnv = g;
    }

    /**
     * @return x value of center point
     */
    public int getX() {
        return (int) center.getX();
    }

    /**
     * @return y value of center point
     */
    public int getY() {
        return (int) center.getY();
    }

    /**
     * @return radius of ball
     */
    public int getSize() {
        return this.radius;
    }

    /**
     * @return color of the ball
     */
    public java.awt.Color getColor() {
        return color;
    }

    /**
     * @return velocity of the ball
     */
    public Velocity getVelocity() {
        return new Velocity(velocity.getDx(), velocity.getDy());
    }


    //~~~~~~~~~~~~~sprite methods~~~~~~~~~~~~~~~~~~

    @Override
    public void drawOn(DrawSurface d) {
        d.setColor(color);
        d.fillCircle((int) center.getX(), (int) center.getY(), radius);
    }

    @Override
    public void timePassed() {
        moveOneStep();
    }

    @Override
    public void addToGame(Game g) {
        g.addSprite(this);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * removes the Ball from the Game.
     * @param g - Game
     */
    public void removeFromGame(Game g) {
        g.removeSprite(this);
    }

    /**
     * sets the velocity of the ball.
     *
     * @param v - velocity
     */
    public void setVelocity(Velocity v) {
        Velocity update = new Velocity(v.getDx(), v.getDy());
        this.velocity = update;
    }

    /**
     * changes the color of this Ball.
     *
     * @param c - the new color
     */
    public void setColor(Color c) {
        this.color = c;
    }

    /**
     * sets the velocity of the ball.
     *
     * @param dx - distance travelled on X axis
     * @param dy - distance travelled on Y axis
     */
    public void setVelocity(double dx, double dy) {
        this.velocity = new Velocity(dx, dy);
    }

    /**
     * returns a random color for a ball.
     *
     * @return - new color
     */
    public static Color setRandomColor() {
        Random rand = new Random();
        int redValue = rand.nextInt(253 + 1);
        int greenValue = rand.nextInt(253 + 1);
        int blueValue = rand.nextInt(253 + 1);
        return new Color(redValue, greenValue, blueValue);
    }

    /**
     * moves the center of the ball according to the velocity.
     */
    public void moveOneStep() {
        //creates line trajectory - from the current position of the center, to its future position
        Point trStart = new Point(center.getX(), center.getY());
        Point trEnd = new Point(center.getX() + velocity.getDx(), center.getY() + velocity.getDy());
        Line trajectory = new Line(trStart, trEnd);
        //checks if the trajectory collides with any of the collidable objects
        CollisionInfo collision = gameEnv.getClosestCollision(trajectory);
        if (collision == null) {
            //no collision
            this.center = this.getVelocity().applyToPoint(this.center);
        } else {
            //change the position of the center to slightly before the collision point
            //change the velocity of the ball according to which edge it collided with

            //The collidable is a paddle:
            if (collision.collisionObject().isPaddle()) {
                this.setVelocity(collision.collisionObject().hit(this, collision.collisionPoint(), getVelocity()));
                this.center = new Point(collision.collisionPoint().getX() + 0.1 * this.velocity.getDx(),
                        collision.collisionPoint().getY() + 0.1 * this.velocity.getDy());

                // The collidable is a Block:
            } else {
                this.center = new Point(collision.collisionPoint().getX() - 0.1 * this.velocity.getDx(),
                        collision.collisionPoint().getY() - 0.1 * this.velocity.getDy());
                this.setVelocity(collision.collisionObject().hit(this, collision.collisionPoint(), getVelocity()));
            }

        }
    }
}


