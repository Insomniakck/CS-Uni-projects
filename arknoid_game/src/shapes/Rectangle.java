// 211315262 Sofia Tchernikov

package shapes;

import shapes.basics.Point;
import shapes.basics.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * rectangle class.
 */
public class Rectangle {

    private Point upperLeft;
    private double width;
    private double height;
    private Line[] borders = new Line[4];

    /**
     * rectangle constructor.
     *
     * @param x      - upper left X-value of the rectangle
     * @param y      - upper left Y-value of the rectangle
     * @param width  - width
     * @param height - height
     */
    public Rectangle(double x, double y, double width, double height) {
        this.upperLeft = new Point(x, y);
        this.width = width;
        this.height = height;

        double rectX1 = upperLeft.getX();
        double rectY1 = upperLeft.getY();
        double rectX2 = rectX1 + width;
        double rectY2 = rectY1 + height;
        //sets the borders in this order: 0 - top, 1 - bottom, 2 - left, 3 - right
        this.borders[0] = new Line(upperLeft, new Point(rectX2, rectY1));
        this.borders[1] = new Line(new Point(rectX1, rectY2), new Point(rectX2, rectY2));
        this.borders[2] = new Line(upperLeft, new Point(rectX1, rectY2));
        this.borders[3] = new Line(new Point(rectX2, rectY1), new Point(rectX2, rectY2));
    }


    /**
     * Return a (possibly empty) List of intersection points with the specified line.
     *
     * @param line - the colliding line
     * @return - a List of intersections.
     */
    public java.util.List<Point> intersectionPoints(Line line) {
        List<Point> pointList = new ArrayList<>();
        for (Line l : borders) {

            //if the lines intersect add the intersection point to the list
            if (line.isIntersecting(l)) {
                Point intersection = line.intersectionWith(l);
                if (intersection != null) {
                    pointList.add(intersection);

                    //if intersection == null, then the lines are parallel and intersecting
                } else {
                    if (l.insideLine(line.start())) {
                        pointList.add(line.start());
                    } else if (l.insideLine(line.end())) {
                        pointList.add(line.end());
                    }
                }
            }
        }
        return pointList;
    }

    /**
     * @return - the width of the rectangle
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return - the height of the rectangle
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return - the upper left corner (Point) of the rectangle.
     */
    public Point getUpperLeft() {
        return new Point(upperLeft.getX(), upperLeft.getY());
    }

    /**
     * @param i - the index of the wanted border
     * @return - the border (Line) in the index i
     */
    public Line getBorder(int i) {
        if (i >= 0 && i < 5) {
            return new Line(borders[i].start(), borders[i].end());
        } else {
            return null;
        }

    }


}
