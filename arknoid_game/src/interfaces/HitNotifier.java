package interfaces;

/**
 * Class HitNotifier holds objects that need to send notifications when they are being hit.
 */
public interface HitNotifier {

    /**
     * Add hl as a listener for hit events.
     *
     * @param hl - the HitListener
     */
    void addHitListener(HitListener hl);

    /**
     * Remove hl from the list of listeners to hit events.
     *
     * @param hl - the HitListener
     */
    void removeHitListener(HitListener hl);

}
