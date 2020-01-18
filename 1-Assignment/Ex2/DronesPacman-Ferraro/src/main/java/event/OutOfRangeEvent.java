package event;

import java.util.EventObject;

/**
 *
 * @author Gaspare Ferraro - 520549 - <ferraro@gaspa.re>
 */
public class OutOfRangeEvent extends EventObject {

    private final int x;
    private final int y;

    /**
     *
     * @param source DroneButton object
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public OutOfRangeEvent(Object source, int x, int y) {
        super(source);
        this.x = x;
        this.y = y;
    }

    /**
     * Getter method for x
     *
     * @return X coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Getter method for y
     *
     * @return Y coordinate
     */
    public int getY() {
        return this.y;
    }
}
