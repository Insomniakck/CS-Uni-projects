// 211315262 Sofia Tchernikov

package gameItems.collections;


import interfaces.Collidable;
import shapes.basics.Point;

/**
 * CollisionInfo class holds the collision point information for the game.
 */
public class CollisionInfo {

    private Point collisionPoint;
    private Collidable collisionObject;


    /**
     * CollisionInfo constructor.
     *
     * @param collisionPoint  - the point of collision with the ball
     * @param collisionObject - the object that collides with the ball
     */
    public CollisionInfo(Point collisionPoint, Collidable collisionObject) {
        Point p = new Point(collisionPoint.getX(), collisionPoint.getY());
        this.collisionPoint = p;
        this.collisionObject = collisionObject;
    }

    /**
     * @return - the point at which the collision occurs.
     */
    public Point collisionPoint() {
        return new Point(collisionPoint.getX(), collisionPoint.getY());
    }

    /**
     * @return - the collidable object involved in the collision.
     */
    public Collidable collisionObject() {
        return collisionObject;
    }
}