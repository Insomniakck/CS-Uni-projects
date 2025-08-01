// 211315262 Sofia Tchernikov

package shapes.basics;

/**
 * class point holds x, y values.
 */
public class Point {

    private double x;
    private double y;

    /**
     * constructor.
     *
     * @param x - the x value
     * @param y - the y value
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * the distance between two points.
     *
     * @param other - the other point
     * @return the distance between two points
     */
    public double distance(Point other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }

    /**
     * checks if two doubles are equal by comparing them with a threshold.
     *
     * @param num1 - first double
     * @param num2 - second double
     * @return - true if they are equal false if not
     */
    public static boolean doubleEquals(double num1, double num2) {
        return Math.abs(num1 - num2) < 0.0000001;
    }

    /**
     * checks if two points are equal by comparing them with a threshold.
     *
     * @param other - the second point
     * @return true if the values of both points are the same
     */
    public boolean equals(Point other) {
        return doubleEquals(this.x, other.x) && doubleEquals(this.y, other.y);
    }

    /**
     * x value getter.
     *
     * @return the value of x
     */
    public double getX() {
        return x;
    }

    /**
     * y value getter.
     *
     * @return the value of y
     */
    public double getY() {
        return y;
    }

}
