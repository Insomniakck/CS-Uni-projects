// 211315262 Sofia Tchernikov


import biuoop.GUI;
import biuoop.DrawSurface;

import java.awt.Color;
import java.util.Random;

/**
 * draws multiple bouncing balls and 2 rectangles.
 */
public class MultipleFramesBouncingBallsAnimation {

    /**
     * creates a new Ball with a random center point.
     *
     * @param r      - radius given by the user
     * @param xLeft  - left border of the frame
     * @param xRight - right border of the frame
     * @param yUp    - top border of the frame
     * @param yDown  - bottom border of the frame
     * @return - new Ball
     */
    public static Ball randomBall(int r, int xLeft, int xRight, int yUp, int yDown) {
        Random rand = new Random();
        int x = rand.nextInt(xLeft + r, xRight - r);
        int y = rand.nextInt(yUp + r, yDown - r);
        Velocity v = Velocity.fromAngleAndSpeed(rand.nextInt(361), Velocity.setSpeedBySize(r));
        Ball ball = new Ball(x, y, r, Ball.setRandomColor());
        ball.setVelocity(v);
        return ball;
    }

    /**
     * draws multiple bouncing balls and 2 rectangles.
     *
     * @param r - list of radius' for balls
     */
    private static void multiFrames(int[] r) {
        GUI gui = new GUI("Multiple Bouncing Balls", 800, 600);
        biuoop.Sleeper sleeper = new biuoop.Sleeper();
        Ball[] balls = new Ball[r.length];
        for (int i = 0; i < r.length; i++) {
            //the first half of the balls are inside the grey box
            if (i < r.length / 2) {
                balls[i] = randomBall(r[i], 50, 450, 50, 450);
                balls[i].setBounds(50, 500, 50, 500);
                //the second half are outside
            } else {
                int t;
                do {
                    t = 0;
                    balls[i] = randomBall(r[i], 0, 800, 0, 600);
                    if (balls[i].getX() + r[i] >= 50 && balls[i].getX() - r[i] <= 500
                            && balls[i].getY() + r[i] >= 50 && balls[i].getY() - r[i] <= 500) {
                        t = 1;
                    }
                    if (balls[i].getX() + r[i] >= 450 && balls[i].getX() - r[i] <= 600
                            && balls[i].getY() + r[i] >= 450 && balls[i].getY() - r[i] <= 600) {
                        t = 1;
                    }
                } while (t != 0);
                balls[i].setBounds(1, 800, 1, 600);
            }


        }

        while (true) {
            DrawSurface d = gui.getDrawSurface();
            d.setColor(Color.GRAY);
            d.fillRectangle(50, 50, 450, 450);

            for (int i = 0; i < balls.length; i++) {
                if (i >= balls.length / 2) {
                    balls[i].keepOffSquare(50, 500, 50, 500);
                }
                balls[i].keepOffSquare(450, 600, 450, 600);
                balls[i].moveOneStep();
                balls[i].drawOn(d);
            }
            d.setColor(Color.YELLOW);
            d.fillRectangle(450, 450, 150, 150);

            gui.show(d);
            sleeper.sleepFor(50);  // wait for 50 milliseconds.
        }
    }

    /**
     * requires integers and calls for multiFrames method.
     *
     * @param args - the integers input will be used as the balls radius
     */
    public static void main(String[] args) {
        int[] r = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            //the largest radius possible inside the grey box
            if (i < args.length / 2 && Integer.parseInt(args[i]) > 200) {
                System.out.println("radius " + args[i] + " too large, please choose a radius smaller than 200");
                return;
            }
            //the largest radius possible outside the grey box
            if (i >= args.length / 2 && Integer.parseInt(args[i]) > 150) {
                System.out.println("radius " + args[i] + " too large, please choose a radius smaller than 150");
                return;
            }
            //radius too small
            if (Integer.parseInt(args[i]) < 1) {
                System.out.println("radius cannot be 0 or a negative number.");
                return;
            }
            r[i] = Integer.parseInt(args[i]);
        }
        multiFrames(r);
    }

}
