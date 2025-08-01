// 211315262 Sofia Tchernikov

import biuoop.GUI;
import biuoop.DrawSurface;

import java.util.Random;

/**
 * draws multiple bouncing balls.
 */
public class MultipleBouncingBallsAnimation {

    /**
     * draws multiple bouncing ball.
     *
     * @param r - the radius' of the balls
     */
    public static void multipleBalls(int[] r) {
        GUI gui = new GUI("Multiple Bouncing Balls", 200, 200);
        biuoop.Sleeper sleeper = new biuoop.Sleeper();
        Random rand = new Random();
        Ball[] balls = new Ball[r.length];

        //creates 10 balls with the radius' from args[], and random starting points
        for (int i = 0; i < r.length; i++) {
            int x = rand.nextInt(r[i], 200);
            int y = rand.nextInt(r[i], 200);
            balls[i] = new Ball(x, y, r[i], Ball.setRandomColor());
            Velocity v = Velocity.fromAngleAndSpeed(rand.nextInt(361), Velocity.setSpeedBySize(r[i]));
            balls[i].setVelocity(v);
        }
        while (true) {
            DrawSurface d = gui.getDrawSurface();
            for (Ball ball : balls) {
                ball.moveOneStep();
                ball.drawOn(d);
            }
            gui.show(d);
            sleeper.sleepFor(50);  // wait for 50 milliseconds.
        }
    }

    /**
     * requires integers.
     *
     * @param args - the integers input will be used as the balls radius
     */
    public static void main(String[] args) {
        int[] r = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            //the largest radius possible outside the grey box
            if (Integer.parseInt(args[i]) > 100) {
                System.out.println("radius " + args[i] + " too large, please choose a radius smaller than 100");
                return;
            }
            //radius too small
            if (Integer.parseInt(args[i]) < 1) {
                System.out.println("radius cannot be 0 or a negative number.");
                return;
            }
            r[i] = Integer.parseInt(args[i]);
        }
        multipleBalls(r);
    }
}
