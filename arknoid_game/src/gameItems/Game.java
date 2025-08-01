// 211315262 Sofia Tchernikov
package gameItems;

import gameItems.collections.SpriteCollection;
import gameItems.collections.GameEnvironment;
import gameItems.listeners.BallRemover;
import gameItems.listeners.BlockRemover;
import gameItems.listeners.ScoreTrackingListener;
import interfaces.Collidable;
import interfaces.HitListener;
import interfaces.Sprite;


import shapes.Ball;
import shapes.Block;
import shapes.Paddle;
import shapes.Rectangle;
import shapes.basics.Velocity;
import biuoop.GUI;
import biuoop.DrawSurface;

import java.awt.Color;

/**
 * Class Game initiates and runs a game.
 */
public class Game {

    private SpriteCollection sprites;
    private GameEnvironment environment;
    private GUI gui;
    private biuoop.Sleeper sleeper;
    private Counter blockCounter;
    private Counter ballCounter;
    private Counter score = new Counter(0);


    /**
     * @param c - Collidable c is added to environment List
     */
    public void addCollidable(Collidable c) {
        environment.addCollidable(c);
    }

    /**
     * @param s - Sprite s is added to sprites List
     */
    public void addSprite(Sprite s) {
        sprites.addSprite(s);
    }

    /**
     * Removes a Collidable from the environment.
     *
     * @param c - the Collidable we remove
     */
    public void removeCollidable(Collidable c) {
        environment.removeCollidable(c);
    }

    /**
     * Removes a Sprite from sprites.
     *
     * @param s - the Sprite we remove
     */
    public void removeSprite(Sprite s) {
        sprites.removeSprite(s);
    }

    public static final double GAME_BALL_SPEED = 3;


    /**
     * Initialize a new game: create the Blocks and Ball (and Paddle) and add them to the game.
     */
    public void initialize() {

        sprites = new SpriteCollection();
        environment = new GameEnvironment();
        gui = new GUI("Arkanoid", 800, 600);
        sleeper = new biuoop.Sleeper();
        blockCounter = new Counter(57);
        ballCounter = new Counter(3);

        //adds the HitListeners to the game
        HitListener blockRemoverL = new BlockRemover(this, blockCounter);
        HitListener ballRemoverL = new BallRemover(this, ballCounter);
        HitListener scoreTrackerL = new ScoreTrackingListener(score);


        //adds the first ball to the game
        Ball s1 = new Ball(400, 300, 5, Color.WHITE);
        s1.setBallEnvironment(environment);
        s1.setVelocity(Velocity.fromAngleAndSpeed(60, GAME_BALL_SPEED));
        s1.addToGame(this);

        //adds the second ball to the game
        Ball s2 = new Ball(400, 300, 5, Color.WHITE);
        s2.setBallEnvironment(environment);
        s2.setVelocity(Velocity.fromAngleAndSpeed(180, GAME_BALL_SPEED));
        s2.addToGame(this);

        //adds the third ball to the game
        Ball s3 = new Ball(400, 300, 5, Color.WHITE);
        s3.setBallEnvironment(environment);
        s3.setVelocity(Velocity.fromAngleAndSpeed(300, GAME_BALL_SPEED));
        s3.addToGame(this);


        //adds the borders of the screen to the game
        new Block(new Rectangle(0, 0, 800, 20), Color.LIGHT_GRAY).addToGame(this);
        new Block(new Rectangle(0, 20, 20, 580), Color.LIGHT_GRAY).addToGame(this);
        new Block(new Rectangle(780, 20, 20, 580), Color.LIGHT_GRAY).addToGame(this);


        Block deathBlock = new Block(new Rectangle(0, 610, 800, 10), Color.BLACK);
        deathBlock.addToGame(this);
        deathBlock.addHitListener(ballRemoverL);


        // adds the blocks in a pyramid shape to the game
        Color[] colors = new Color[]{new Color(105, 47, 105),
                new Color(126, 247, 249),
                new Color(188, 87, 131),
                new Color(75, 185, 217),
                new Color(236, 131, 175),
                new Color(6, 110, 165)};

        for (int i = 0; i < colors.length; i++) {
            for (int j = i; j < 12; j++) {
                Block b = new Block(new Rectangle(180 + j * 50, 80 + 20 * (i + 1),
                        50, 20), colors[i]);
                b.addToGame(this);
                b.addHitListener(blockRemoverL);
                b.addHitListener(scoreTrackerL);
            }
        }

        //adds the main and secondary paddles to the game
        biuoop.KeyboardSensor keyboard = gui.getKeyboardSensor();

        Paddle paddle1 = new Paddle(360, 565, 80, 15, new Color(250, 196, 132), keyboard);
        paddle1.addToGame(this);
        Paddle paddle2 = new Paddle(360 + 800, 565, 80, 15, new Color(250, 196, 132), keyboard);
        paddle2.addToGame(this);

        //adds the ScoreIndicator Sprite
        new ScoreIndicator(score).addToGame(this);

    }


    /**
     * Run the game -- start the animation loop.
     */
    public void run() {

        int framesPerSecond = 60;
        int millisecondsPerFrame = 1000 / framesPerSecond;
        while (true) {
            // timing
            long startTime = System.currentTimeMillis();

            DrawSurface d = gui.getDrawSurface();
            d.setColor(new Color(41, 35, 84));
            d.fillRectangle(0, 0, 800, 600);
            this.sprites.drawAllOn(d);
            gui.show(d);

            this.sprites.notifyAllTimePassed();

            // timing
            long usedTime = System.currentTimeMillis() - startTime;
            long milliSecondLeftToSleep = millisecondsPerFrame - usedTime;
            if (milliSecondLeftToSleep > 0) {
                sleeper.sleepFor(milliSecondLeftToSleep);
            }

            if ((blockCounter.getValue() == 0) || (ballCounter.getValue() == 0)) {
                score.increase(100);
                gui.close();
                return;
            }

        }
    }
}