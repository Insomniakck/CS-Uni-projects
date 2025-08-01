// 211315262 Sofia Tchernikov

package gameItems.collections;


import interfaces.Collidable;
import shapes.basics.Line;
import shapes.basics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * GameEnvironment class holds all the collidable objects for the game.
 */
public class GameEnvironment {

    private List<Collidable> collidableList = new ArrayList<>();


    /**
     * add the given collidable to the environment.
     *
     * @param c - the new collidable
     */
    public void addCollidable(Collidable c) {
        if (collidableList == null) {
            collidableList = new ArrayList<>();
        }
        collidableList.add(c);

    }

    /**
     * removes collidable from collidableList.
     * @param c - the collidable we remove
     */
    public void removeCollidable(Collidable c) {
        collidableList.remove(c);
    }

    /**
     * checks for collisions with the Line trajectory.
     *
     * @param trajectory - the line we check collisions with
     * @return - the collision closest to the start of the line. Or null if there are no collisions
     */
    public CollisionInfo getClosestCollision(Line trajectory) {
        CollisionInfo closestColl = null;
        List<Collidable>  collidables = new ArrayList<>(this.collidableList);
        for (int i = 0; i < collidables.size(); i++) {
            Point point = trajectory.closestIntersectionToStartOfLine(collidables.get(i).getCollisionRectangle());

            //if a collision is detected, compares it to previous collisions (if there are any)
            if (point != null) {
                if (closestColl == null) {
                    closestColl = new CollisionInfo(point, collidables.get(i));
                } else if (trajectory.start().distance(closestColl.collisionPoint())
                        > trajectory.start().distance(point)) {
                    closestColl = new CollisionInfo(point, collidables.get(i));
                }
            }
        }
        return closestColl;
    }

}