package event;

import java.util.EventListener;

/**
 *
 * @author Gaspare Ferraro - 520549 - <ferraro@gaspa.re>
 */
public interface OutOfRangeListener extends EventListener {

    /**
     *
     * @param evt OutOfRangeEvent event
     */
    public void OutOfRange(OutOfRangeEvent evt);

}
