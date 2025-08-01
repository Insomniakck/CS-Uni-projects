// 211315262 Sofia Tchernikov

/**
 * class Velocity - has the parameters: dx, dy.
 */
public class Velocity {

    private double dx;
    private double dy;

    /**
     * constructor.
     * @param dx - distance travelled on X axis
     * @param dy - distance travelled on Y axis
     */
    public Velocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Velocity class getter.
     *
     * @return - dx value
     */
    public double getDx() {
        return this.dx;
    }

    /**
     * Velocity class getter.
     *
     * @return - dy value
     */
    public double getDy() {
        return this.dy;
    }


    /**
     * Take a point with position (x,y) and return a new point (x+dx,y+dy).
     *
     * @param p - point-(x,y)
     * @return - new point-(x+dx,y+dy)
     */
    public Point applyToPoint(Point p) {
        return new Point(p.getX() + dx, p.getY() + dy);
    }

    /**
     * sets the velocity of an object according to an angle and speed.
     *
     * @param angle - angle of initial movement of the drawing surface
     * @param speed - the speed of the movement on the drawing surface
     * @return - new velocity
     */
    public static Velocity fromAngleAndSpeed(double angle, double speed) {
        double dx = speed * Math.sin(Math.toRadians(angle));
        double dy = -speed * Math.cos(Math.toRadians(angle));
        return new Velocity(dx, dy);
    }


    /**
     * sets the velocity speed according to the size of the ball.
     * @param r - radius of the ball
     * @return - the speed of movement
     */
    public static int setSpeedBySize(int r) {
        // radius<50 - sets a higher speed the smaller the radius
        if (r < 50) {
            return (int) (16 - 2 * Math.log(r));
        }
        return 5;
    }
}
