// 211315262 Sofia Tchernikov

import biuoop.DrawSurface;

import java.awt.Color;
import java.util.Random;

/**
 * class Ball - has the parameters: Center, Radius, Color, and Velocity.
 */
public class Ball {

    private Point center;
    private int radius;
    private java.awt.Color color;
    private Line bounds = new Line(new Point(0, 0), new Point(200, 200));
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
     * Ball class getter.
     *
     * @return x value of center point
     */
    public int getX() {
        return (int) center.getX();
    }

    /**
     * Ball class getter.
     *
     * @return y value of center point
     */
    public int getY() {
        return (int) center.getY();
    }

    /**
     * Ball class getter.
     *
     * @return radius of ball
     */
    public int getSize() {
        return this.radius;
    }

    /**
     * Ball class getter.
     *
     * @return color of the ball
     */
    public java.awt.Color getColor() {
        return color;
    }

    /**
     * Ball class getter.
     *
     * @return velocity of the ball
     */
    public Velocity getVelocity() {
        return this.velocity;
    }

    /**
     * draw the ball on the given DrawSurface.
     *
     * @param surface where the ball will be drawn
     */
    public void drawOn(DrawSurface surface) {
        surface.setColor(color);
        surface.fillCircle((int) center.getX(), (int) center.getY(), radius);
    }


    /**
     * sets the velocity of the ball.
     *
     * @param v - velocity
     */
    public void setVelocity(Velocity v) {
        this.velocity = v;
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
     * the default bounds of the ball are (0,200) - (0,200). setBounds changes the bounds of the ball.
     *
     * @param x1 - X-axis start
     * @param x2 - X-axis end
     * @param y1 - Y-axis start
     * @param y2 - Y-axis end
     */
    public void setBounds(int x1, int x2, int y1, int y2) {
        this.bounds = new Line(new Point(x1, y1), new Point(x2, y2));
    }

    /**
     * moves the center of the ball according to the velocity.
     * makes sure the ball stays inside the drawing surface
     */
    public void moveOneStep() {
        //checks if the x value is within the correct range
        if (center.getX() - radius + this.velocity.getDx() < bounds.start().getX()) {
            this.setVelocity(Math.abs(this.velocity.getDx()), this.velocity.getDy());
        } else if (center.getX() + radius + this.velocity.getDx() > bounds.end().getX()) {
            this.setVelocity(-Math.abs(this.velocity.getDx()), this.velocity.getDy());
        }
        //checks if the y value is within the correct range
        if (this.center.getY() - radius + this.velocity.getDy() < bounds.start().getY()) {
            this.setVelocity(this.velocity.getDx(), Math.abs(this.velocity.getDy()));
        } else if (this.center.getY() + radius + this.velocity.getDy() > bounds.end().getY()) {
            this.setVelocity(this.velocity.getDx(), -Math.abs(this.velocity.getDy()));
        }
        this.center = this.getVelocity().applyToPoint(this.center);
    }

    /**
     * makes sure the ball doesn't intersect with a square frame.
     *
     * @param left  - the left border of the square
     * @param right - the right border of the square
     * @param up    - the top border of the square
     * @param down  - the bottom border of the square
     */
    public void keepOffSquare(int left, int right, int up, int down) {
        //the future center point
        Point fCenter = new Point(center.getX() + velocity.getDx(), center.getY() + velocity.getDy());
        // checks if it hit an edge
        // if the edge ball is inside the square
        if ((fCenter.getX() + radius >= left && fCenter.getX() - radius <= right && fCenter.getY() <= down
                && fCenter.getY() >= up) || (fCenter.getX() >= left && fCenter.getX() <= right
                && fCenter.getY() - radius <= down && fCenter.getY() + radius >= up)) {
            //checks which edge is going to be crossed.
            if (fCenter.getY() - radius <= down && center.getY() - radius >= down) {
                this.setVelocity(this.velocity.getDx(), Math.abs(this.velocity.getDy()));
            }
            if (fCenter.getY() + radius >= up && center.getY() + radius <= up) {
                this.setVelocity(this.velocity.getDx(), -Math.abs(this.velocity.getDy()));
            }
            if (fCenter.getX() + radius >= left && center.getX() + radius <= left) {
                this.setVelocity(-Math.abs(this.velocity.getDx()), this.velocity.getDy());
            }
            if (fCenter.getX() - radius <= right && center.getX() - radius >= right) {
                this.setVelocity(Math.abs(this.velocity.getDx()), this.velocity.getDy());
            }

            //if the ball doesn't hit an edge, checks if it any of the 4 corners
        } else if (fCenter.distance(new Point(right, up)) <= radius && fCenter.getX() > right && fCenter.getY() < up) {
            this.setVelocity(Math.abs(this.velocity.getDx()), -Math.abs(this.velocity.getDy()));
        } else if (fCenter.distance(new Point(right, down)) <= radius && fCenter.getX() > right
                && fCenter.getY() > down) {
            this.setVelocity(Math.abs(this.velocity.getDx()), Math.abs(this.velocity.getDy()));
        } else if (fCenter.distance(new Point(left, down)) <= radius && fCenter.getX() < left
                && fCenter.getY() > down) {
            this.setVelocity(-Math.abs(this.velocity.getDx()), Math.abs(this.velocity.getDy()));
        } else if (fCenter.distance(new Point(left, up)) <= radius && fCenter.getX() < left && fCenter.getY() < up) {
            this.setVelocity(-Math.abs(this.velocity.getDx()), -Math.abs(this.velocity.getDy()));
        }
    }
}


