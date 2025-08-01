// 211315262 Sofia Tchernikov

package shapes.basics;

import shapes.Rectangle;

import java.util.List;

/**
 * Line class contains 2 points and can check if there is an intersection between 2 lines, start, middle
 * and end of the line.
 */
public class Line {
    private Point start;
    private Point end;

    /**
     * constructor for Line class.
     *
     * @param start is the first point that is input
     * @param end   is the second point that is input
     */
    public Line(Point start, Point end) {
        this.start = new Point(start.getX(), start.getY());
        this.end = new Point(end.getX(), end.getY());
    }

    /**
     * constructor for Line class.
     *
     * @param x1 is the x value of the first point
     * @param y1 is the y value of the first point
     * @param x2 is the x value of the second point
     * @param y2 is the y value of the second point
     */
    public Line(double x1, double y1, double x2, double y2) {
        start = new Point(x1, y1);
        end = new Point(x2, y2);
    }

    /**
     * @return the length of the line
     */
    public double length() {
        return start.distance(this.end);
    }

    /**
     * @return the middle point of the line
     */
    public Point middle() {
        return new Point((start.getX() + end.getX()) / 2, (start.getY() + end.getY()) / 2);
    }

    /**
     * @return the start point of the line
     */
    public Point start() {

        return new Point(start.getX(), start.getY());
    }

    /**
     * @return the end point of the line
     */
    public Point end() {

        return new Point(end.getX(), end.getY());
    }

    /**
     * calculates the slope of this Line.
     *
     * @return the slope of the Line, or infinity if the line is vertical
     */
    public double getSlope() {
        if (Point.doubleEquals(start.getX(), end.getX())) {
            return Double.POSITIVE_INFINITY;
        }
        return (start.getY() - end.getY()) / (start.getX() - end.getX());
    }


    /**
     * checks if the point input is inside the line.
     *
     * @param point the point we want to check
     * @return true or false, whether the point is inside to line or not
     */
    public boolean insideLine(Point point) {
        return Point.doubleEquals(start.distance(point) + end.distance(point), length());
    }


    /**
     * true if the lines intersect, false otherwise.
     *
     * @param other - the second line we will be comparing
     * @return true if the lines intersect, false otherwise.
     */
    public boolean isIntersecting(Line other) {
        double myS = getSlope();
        double otherS = other.getSlope();

        //edge case - both lines are parallel (including vertical lines)
        if (Point.doubleEquals(myS, otherS)) {

            //if both lines have the same equation, and occupy the same space, returns true
            return Point.doubleEquals(new Line(end, other.end).getSlope(), myS)
                    && (insideLine(other.end) || insideLine(other.start));

            //edge case - only one of the lines is vertical
        } else if ((myS == Double.POSITIVE_INFINITY) ^ (otherS == Double.POSITIVE_INFINITY)) {
            //checks if the vertical line is between the start and end points of the other line
            //if so, checks if the intersection is inside the vertical line.
            if (other.end.getX() <= end.getX() == end.getX() < other.start.getX()) {
                return insideLine(new Point(end.getX(),
                        otherS * (end.getX() - other.end.getX()) + other.end.getY()));
            } else if (end.getX() <= other.end.getX() == other.end.getX() < start.getX()) {
                return other.insideLine(new Point(other.end.getX(),
                        myS * (other.end.getX() - end.getX()) + end.getY()));
            } else {
                return false;
            }

            //the lines aren't parallel or vertical. calculates the point of intersection, and returns
            //whether it is inside both lines.
        } else {
            double x = (end.getY() - other.end.getY() + otherS * other.end.getX() - myS * end.getX()) / (otherS - myS);
            double y = myS * (x - end.getX()) + end.getY();
            Point p = new Point(x, y);
            return insideLine(p) && other.insideLine(p);
        }
    }

    /**
     * true if the lines intersect, false otherwise.
     *
     * @param other1 - the first line we will be comparing
     * @param other2 - the second line we will be comparing
     * @return true if the lines intersect, false otherwise.
     */
    public boolean isIntersecting(Line other1, Line other2) {
        return isIntersecting(other1) && isIntersecting(other2);
    }

    /**
     * Returns the intersection point if the lines intersect.
     *
     * @param other - the second line we will be comparing
     * @return - the intersection point, if it exists.
     */
    public Point intersectionWith(Line other) {
        if (!isIntersecting(other)) {
            return null;
        }
        double myS = getSlope();
        double otherS = other.getSlope();

        //edge case - the lines are parallel, returns null if there is more than one intersection point.
        if (Point.doubleEquals(myS, otherS)) {
            if (other.end.equals(start) || other.end.equals(end)) {
                return other.end;
            } else if (other.start.equals(start) || other.start.equals(end)) {
                return other.start;
            } else {
                return null;
            }

            //edge case - one of the lines is vertical
        } else if (myS == Double.POSITIVE_INFINITY) {
            return new Point(end.getX(), otherS * (end.getX() - other.end.getX()) + other.end.getY());
        } else if (otherS == Double.POSITIVE_INFINITY) {
            return new Point(other.end.getX(), myS * (other.end.getX() - end.getX()) + end.getY());

            //the lines aren't parallel or vertical.
        } else {
            double x = (end.getY() - other.end.getY() + otherS * other.end.getX() - myS * end.getX()) / (otherS - myS);
            double y = myS * (x - end.getX()) + end.getY();
            return new Point(x, y);
        }
    }

    /**
     * @param other the second line we will be comparing
     * @return true if the lines are equal, false otherwise.
     */
    public boolean equals(Line other) {
        return (start.equals(other.start) && end.equals(other.end))
                || (start.equals(other.end) && end.equals(other.start));
    }


    /**
     * If this line does not intersect with the rectangle, return null. Otherwise, return the closest intersection
     * point to the start of the line.
     *
     * @param rect - the rectangle we check for intersection points with
     * @return - the intersection point closest to the start point of the line
     */
    public Point closestIntersectionToStartOfLine(Rectangle rect) {

        List<Point> intersections = rect.intersectionPoints(this);
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        Point closestPoint = intersections.get(0);
        for (int i = 1; i < intersections.size(); i++) {
            if (this.start.distance(closestPoint) > this.start.distance(intersections.get(i))) {
                closestPoint = intersections.get(i);
            }
        }
        return closestPoint;
    }

}
