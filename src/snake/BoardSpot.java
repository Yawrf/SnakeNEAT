
package snake;

import static snake.BoardSpot.Type.*;

/**
 *
 * @author rewil
 */
public class BoardSpot {
    
    private final int x;
    private final int y;
    private Type type = EMPTY;
    
    public BoardSpot(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setType(Type t) {
        type = t;
    }
    public Type getType() {
        return type;
    }
    public boolean isEmpty() {
        return type.equals(EMPTY);
    }
    
  //----------------------------------------------------------------------------
    
    public static enum Type {
        SNAKE,
        FOOD,
        EMPTY;
    }
}
