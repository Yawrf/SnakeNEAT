
package snake;

/**
 *
 * @author rewil
 */
public class SnakeJoint {
    
    private SnakeJoint next = null;
    private int x = 0;
    private int y = 0;
    
    public SnakeJoint() {
        this(0, 0);
    }
    public SnakeJoint(int x, int y) {
        setCoords(x, y);
    }
    
    public void setNextJoint(SnakeJoint n) {
        next = n;
    }
    public SnakeJoint getNextJoint() {
        return next;
    }
    
    public boolean isTail() {
        return next == null;
    }
    
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    
    public void setCoords(int x, int y) {
        setX(x);
        setY(y);
    }
    
    public void moveNextJoint(int inX, int inY) {
        if(!isTail()) next.moveNextJoint(x, y);
        x = inX;
        y = inY;
    }
    
}
