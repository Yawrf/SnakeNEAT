
package snake;

import snake.Board.Direction;

/**
 *
 * @author rewil
 */
public class SnakeObject {
    
    private final SnakeJoint head = new SnakeJoint();
    private Direction facing = Direction.DOWN;
    
    public SnakeJoint getHead() {
        return head;
    }
    public Direction getFacing() {
        return facing;
    }
    public void setFacing(Direction d) {
        switch(facing) {
            case UP: if(d.equals(Direction.DOWN)) return;
                break;
            case DOWN: if(d.equals(Direction.UP)) return;
                break;
            case LEFT: if(d.equals(Direction.RIGHT)) return;
                break;
            case RIGHT: if(d.equals(Direction.LEFT)) return;
                break;
        }
        facing = d;
    }
    
    public int getX() {
        return head.getX();
    }
    public int getY() {
        return head.getY();
    }
    
    public int getTailX() {
        SnakeJoint tail = head;
        while(!tail.isTail()) tail = tail.getNextJoint();
        return tail.getX();
    }
    public int getTailY() {
        SnakeJoint tail = head;
        while(!tail.isTail()) tail = tail.getNextJoint();
        return tail.getY();
    }
    
    /**
     * Adds a SnakeJoint at the current location of the last joint (tail)
     */
    public void addJoint() {
        SnakeJoint tail = head;
        while(!tail.isTail()) tail = tail.getNextJoint();
        tail.setNextJoint(new SnakeJoint(tail.getX(), tail.getY()));
    }
    
    /**
     * Moves the SnakeObject one space in the direction it is facing
     */
    public void move() {
        int x = 0;
        int y = 0;
        switch(facing) {
            case UP: y = -1;
                break;
            case DOWN: y = 1;
                break;
            case LEFT: x = -1;
                break;
            case RIGHT:  x = 1;
        }
        x += head.getX();
        y += head.getY();
        head.moveNextJoint(x, y);
    }
    
    /**
     * Returns number of joints in Snake (including head)
     * @return 
     */
    public int length() {
        int i = 1;
        SnakeJoint temp = head;
        while(!temp.isTail()) {
            temp = temp.getNextJoint();
            ++i;
        }
        return i;
    }
    
}
