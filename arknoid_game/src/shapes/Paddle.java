// 211315262 Sofia Tchernikov
package shapes;

import gameItems.Game;
import interfaces.Collidable;
import interfaces.Sprite;
import shapes.basics.Point;
import shapes.basics.Velocity;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import java.awt.Color;

/**
 * Paddle Class - the moving platform the user can control.
 */
public class Paddle implements Sprite, Collidable, KeyboardSensor {

    private Rectangle paddle;
    private Color color;
    private biuoop.KeyboardSensor keyboard;


    /**
     * constructor for Paddle.
     *
     * @param x        - X value for the rectangle top left point
     * @param y        - Y value for the rectangle top left point
     * @param width    - the width of the rectangle
     * @param height   - the height of the rectangle
     * @param color    - the color of the rectangle
     * @param keyboard - the keyboard sensor, used to move the paddle left and right
     */
    public Paddle(double x, double y, double width, double height, Color color, biuoop.KeyboardSensor keyboard) {
        this.paddle = new Rectangle(x, y, width, height);
        this.color = color;
        this.keyboard = keyboard;
    }

    /**
     * @return - this.rectangle
     */
    public Rectangle getRectangle() {
        return new Rectangle(paddle.getUpperLeft().getX(), paddle.getUpperLeft().getY(),
                paddle.getWidth(), paddle.getHeight());
    }


    //~~~~~~~~~ KeyboardSensor method~~~~~~~~~~~~~~~~~
    @Override
    public boolean isPressed(String s) {
        return true;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    /**
     * moves the paddle x spaces on the X-axis.
     *
     * @param x - the number of pixels the paddle moves
     */
    public void cyclePaddle(double x) {
        paddle = new Rectangle(paddle.getUpperLeft().getX() + x, paddle.getUpperLeft().getY(), paddle.getWidth(),
                paddle.getHeight());
    }


    /**
     * moves the paddle to the left if the user is pressing the left key.
     */
    public void moveLeft() {
        if (keyboard.isPressed(KeyboardSensor.LEFT_KEY)) {
            cyclePaddle(-5);
        }
    }

    /**
     * moves the paddle to the right if the user is pressing the right key.
     */
    public void moveRight() {
        if (keyboard.isPressed(KeyboardSensor.RIGHT_KEY)) {
            cyclePaddle(5);
        }
    }

    /**
     * moves the given paddle so it does step out of bounds [-800,1600].
     */
    public void paddleCycle() {
        if (getRectangle().getUpperLeft().getX() > 2 * 800 - getRectangle().getWidth()) {
            cyclePaddle(-2 * 800);
        }
        if (getRectangle().getUpperLeft().getX() < -800) {
            cyclePaddle(2 * 800);
        }
    }

    //~~~~~~~~~ Sprite methods~~~~~~~~~~~~~~~~

    @Override
    public void timePassed() {
        moveRight();
        moveLeft();
        paddleCycle();
    }

    @Override
    public void drawOn(DrawSurface d) {
        d.setColor(color);
        d.fillRectangle((int) paddle.getUpperLeft().getX(), (int) paddle.getUpperLeft().getY(),
                (int) paddle.getWidth(), (int) paddle.getHeight());
        d.setColor(Color.BLACK);
        d.drawRectangle((int) paddle.getUpperLeft().getX(), (int) paddle.getUpperLeft().getY(),
                (int) paddle.getWidth(), (int) paddle.getHeight());
    }

    @Override
    public void addToGame(Game g) {
        g.addCollidable(this);
        g.addSprite(this);
    }

    //~~~~~~~~~ Collidable methods~~~~~~~~~~~~~~~~

    @Override
    public Rectangle getCollisionRectangle() {
        return paddle;
    }

    @Override
    public Boolean isPaddle() {
        return true;
    }

    @Override
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        Velocity updatedVelocity;
        if (paddle.getBorder(0).insideLine(collisionPoint)) {
            double section = paddle.getWidth() / 5;
            //check which section the ball collided with
            if (collisionPoint.getX() < paddle.getUpperLeft().getX() + 1 * section) {
                updatedVelocity = Velocity.fromAngleAndSpeed(300, Game.GAME_BALL_SPEED);
            } else if (collisionPoint.getX() < paddle.getUpperLeft().getX() + 2 * section) {
                updatedVelocity = Velocity.fromAngleAndSpeed(330, Game.GAME_BALL_SPEED);
            } else if (collisionPoint.getX() < paddle.getUpperLeft().getX() + 3 * section) {
                updatedVelocity = new Velocity(currentVelocity.getDx(), -Math.abs(currentVelocity.getDy()));
            } else if (collisionPoint.getX() < paddle.getUpperLeft().getX() + 4 * section) {
                updatedVelocity = Velocity.fromAngleAndSpeed(30, Game.GAME_BALL_SPEED);
            } else {
                updatedVelocity = Velocity.fromAngleAndSpeed(60, Game.GAME_BALL_SPEED);
            }
        } else if (paddle.getBorder(2).insideLine(collisionPoint)) {
            updatedVelocity = new Velocity(-Math.abs(currentVelocity.getDx()), currentVelocity.getDy());
        } else if (paddle.getBorder(3).insideLine(collisionPoint)) {
            updatedVelocity = new Velocity(Math.abs(currentVelocity.getDx()), currentVelocity.getDy());
        } else {
            updatedVelocity = new Velocity(currentVelocity.getDx(), -Math.abs(currentVelocity.getDy()));
        }
        return updatedVelocity;
    }
}