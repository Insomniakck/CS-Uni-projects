// 211315262 Sofia Tchernikov

package interfaces;

import shapes.Ball;
import shapes.Rectangle;
import shapes.basics.Point;
import shapes.basics.Velocity;

/**
 * Sprite Interface holds methods for block, Paddle to implement.
 */
public interface Collidable {

    /**
     * @return - the "collision shape" of the object.
     */
    Rectangle getCollisionRectangle();


    /**
     * Notify the object that we collided with it at collisionPoint with a given velocity.
     *
     * @param hitter - the Ball that hits the Collidable
     * @param collisionPoint  - the point of collision with the Collidable object
     * @param currentVelocity - the velocity with which the ball collided with the Collidable object
     * @return - the new velocity expected after the hit (based on the force the object inflicted on us).
     */
    Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity);

    /**
     * @return - true if the Collidable is a Paddle, false otherwise
     */
    Boolean isPaddle();
}
