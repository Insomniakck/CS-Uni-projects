package gameItems;

/**
 * Counter is a simple class that is used for counting things.
 */
public class Counter {
    private int counter;

    /**
     * Counter constructor.
     *
     * @param counter - the number we are counting
     */
    public Counter(int counter) {
        this.counter = counter;
    }

    /**
     * @param number - add the number to current count
     */
    public void increase(int number) {
        counter += number;
    }


    /**
     * @param number - subtract the number from current count
     */
    public void decrease(int number) {
        counter -= number;
    }

    /**
     * @return - current count
     */
    public int getValue() {
        return counter;
    }
}