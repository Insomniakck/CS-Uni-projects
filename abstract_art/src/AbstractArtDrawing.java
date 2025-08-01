// 211315262 Sofia Tchernikov


import biuoop.GUI;
import biuoop.DrawSurface;
import java.util.Random;
import java.awt.Color;

/**
 * Abstract Art Drawing.
 */
public class AbstractArtDrawing {

    /**
     * generates a random line.
     *
     * @return - new line
     */
    public Line generateRandomLine() {
        Random rand = new Random();
        int x1, x2, y1, y2;
        //edge case: checks if the random line is a point
        do {
            x1 = rand.nextInt(1, 400);
            y1 = rand.nextInt(1, 300);
            x2 = rand.nextInt(1, 400);
            y2 = rand.nextInt(1, 300);
        } while (x1 == x2 && y1 == y2);
        return new Line(x1, y1, x2, y2);
    }

    /**
     * adds line to surface.
     *
     * @param l - Line
     * @param d - Surface
     */
    private void drawLine(Line l, DrawSurface d) {
        d.drawLine((int) l.start().getX(), (int) l.start().getY(), (int) l.end().getX(), (int) l.end().getY());
    }

    /**
     * adds point to surface.
     *
     * @param p - Point
     * @param d - Surface
     */
    private void drawPoint(Point p, DrawSurface d) {
        d.fillCircle((int) p.getX(), (int) p.getY(), 3);
    }

    /**
     * draws 10 random lines BLACK, their middle point BLUE, intersection points RED.
     * and colors over the edges that form a triangle GREEN
     */
    public void drawLines() {
        GUI gui = new GUI("abstractArtDrawing", 400, 300);
        DrawSurface d = gui.getDrawSurface();
        Line[] lines = new Line[10];

        //generates 10 random lines
        for (int i = 0; i < 10; i++) {
            lines[i] = generateRandomLine();
            d.setColor(Color.BLACK);
            drawLine(lines[i], d);

        }

        for (int i = 0; i < 10; i++) {
            for (int j = i + 1; j < 10; j++) {
                for (int k = j + 1; k < 10; k++) {
                    //checks if 3 lines form a triangle
                    if (lines[k].intersectionWith(lines[i]) != null && lines[k].intersectionWith(lines[j]) != null
                        && lines[i].intersectionWith(lines[j]) != null && lines[i].isIntersecting(lines[j], lines[k])
                        && lines[j].isIntersecting(lines[i], lines[k])) {

                        Line l1 = new Line(lines[i].intersectionWith(lines[j]), lines[i].intersectionWith(lines[k]));
                        Line l2 = new Line(lines[j].intersectionWith(lines[i]), lines[j].intersectionWith(lines[k]));
                        Line l3 = new Line(lines[k].intersectionWith(lines[i]), lines[k].intersectionWith(lines[j]));
                        d.setColor(Color.GREEN);
                        drawLine(l1, d);
                        drawLine(l2, d);
                        drawLine(l3, d);
                    }
                }

            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = i + 1; j < 10; j++) {
                //checks if 2 lines intersect
                if (lines[i].isIntersecting(lines[j]) && lines[i].intersectionWith(lines[j]) != null) {
                    d.setColor(Color.RED);
                    drawPoint(lines[i].intersectionWith(lines[j]), d);
                }
            }
            d.setColor(Color.BLUE);
            drawPoint(lines[i].middle(), d);
        }
        gui.show(d);

    }

    /**
     * calls for drawLines method.
     *
     * @param args - no input needed
     */
    public static void main(String[] args) {
        AbstractArtDrawing example = new AbstractArtDrawing();
        example.drawLines();
    }
}
