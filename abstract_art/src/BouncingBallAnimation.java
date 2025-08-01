// 211315262 Sofia Tchernikov

import biuoop.GUI;
import biuoop.DrawSurface;

/**
 * draws a single bouncing ball.
 */
public class BouncingBallAnimation {

    /**
     * draws a single bouncing ball.
     *
     * @param start - the starting point of the ball
     * @param dx    - movement on the X-Axis per frame
     * @param dy    - movement on the Y-Axis per frame
     */
    private static void drawAnimation(Point start, double dx, double dy) {
        GUI gui = new GUI("Bouncing Ball Animation", 200, 200);
        biuoop.Sleeper sleeper = new biuoop.Sleeper();
        Ball ball = new Ball((int) start.getX(), (int) start.getY(), 30, java.awt.Color.BLACK);
        ball.setVelocity(dx, dy);

        while (true) {
            ball.moveOneStep();
            DrawSurface d = gui.getDrawSurface();
            ball.drawOn(d);
            gui.show(d);
            sleeper.sleepFor(50);  // wait for 50 milliseconds.
        }
    }

    /**
     * requires 4 integers.
     *
     * @param args - a string of 4 integers- x,y,dx,dy - the parameters of a point, and for a velocity
     */
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Please input 4 arguments");
            return;
        }
        int x1 = Integer.parseInt(args[0]);
        int y1 = Integer.parseInt(args[1]);
        int dx = Integer.parseInt(args[2]);
        int dy = Integer.parseInt(args[3]);

        if (x1 > 200 || x1 < 0 || y1 > 200 || y1 < 0) {
            System.out.println("start point is out of bounds");
            return;
        }
        drawAnimation(new Point(x1, y1), dx, dy);
    }
}
