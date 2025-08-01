// 211315262 Sofia Tchernikov

package gameItems.collections;

import java.util.ArrayList;
import java.util.List;

import interfaces.Sprite;
import biuoop.DrawSurface;

/**
 * SpriteCollection Class holds a reference to all the sprite objects.
 */
public class SpriteCollection {

    private List<Sprite> spriteList = new ArrayList<>();


    /**
     * add the given sprite to the Sprite Collection.
     *
     * @param s - the new sprite
     */
    public void addSprite(Sprite s) {
        if (spriteList == null) {
            spriteList = new ArrayList<>();
        }
        spriteList.add(s);
    }

    /**
     * removes collidable from spriteList.
     * @param s - the sprite we remove
     */
    public void removeSprite(Sprite s) {
        spriteList.remove(s);
    }

    /**
     * call timePassed() on all sprites.
     */
    public void notifyAllTimePassed() {
        List<Sprite> sprites = new ArrayList<>(this.spriteList);
        for (Sprite sprite : sprites) {
            sprite.timePassed();
        }
    }


    /**
     * call drawOn(d) on all sprites.
     *
     * @param d - the surface
     */
    public void drawAllOn(DrawSurface d) {
        for (Sprite sprite : spriteList) {
            sprite.drawOn(d);
        }
    }
}