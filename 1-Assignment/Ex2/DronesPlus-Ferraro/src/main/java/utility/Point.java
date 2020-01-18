package utility;

/**
 * Class that define a 2D integer-coordinates point
 * @author Gaspare Ferraro - 520549 - <ferraro@gaspa.re>
 */
public class Point {
    
    private final int x;
    private final int y;
    
    /**
     * Default constructor, create a point in (0,0)
     */
    public Point()
    {
        this(0, 0);
    }
    
    /**
     * Constructor, create a point in (x, y)
     * @param i
     * @param i1
     */
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Get x-coordinate
     * @return x-coordinate
     */
    public int getX()
    {
        return this.x;
    }
    
    /**
     * Get y-coordinate
     * @return y-coordinate
     */
    public int getY()
    {
        return this.y;
    }
}
