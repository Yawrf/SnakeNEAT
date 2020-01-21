
package snake;

import snake.BoardSpot.Type;

/**
 *
 * @author rewil
 */
public class Board {
    
    private final BoardSpot[][] board;
    private final SnakeObject snake;
    
    private int foodX = 0;
    private int foodY = 0;
    
    public Board(int size, SnakeObject snake) {
        board = new BoardSpot[size][size];
        for(int x = 0; x < size; ++x) {
            for(int y = 0; y < size; ++y) {
                board[x][y] = new BoardSpot(x,y);
            }
        }
        this.snake = snake;
    }
    public Board(int size) {
        this(size, new SnakeObject());
    }
    
    public void setSpotType(int x, int y, Type t) {
        board[x][y].setType(t);
    }
    public Type getSpotType(int x, int y) {
        return board[x][y].getType();
    }
    public SnakeObject getSnake() {
        return snake;
    }
    
    /**
     * Attempts to mark a spot as having food.
     * @param x
     * @param y
     * @return Returns false if spot is not empty
     */
    public boolean putFood(int x, int y) {
        if(!board[x][y].isEmpty()) return false;
        board[x][y].setType(Type.FOOD);
        foodX = x;
        foodY = y;
        return true;
    }
    public int getFoodX() {
        return foodX;
    }
    public int getFoodY() {
        return foodY;
    }
    public int getSnakeX() {
        return snake.getX();
    }
    public int getSnakeY() {
        return snake.getY();
    }
    
    public boolean snakeOnFood() {
        if(!snakeOnWall()) return getSpotType(snake.getX(), snake.getY()).equals(Type.FOOD);
        return false;
    }
    public boolean snakeOnSnake() {
        if(!snakeOnWall()) return getSpotType(snake.getX(), snake.getY()).equals(Type.SNAKE);
        return false;
    }
    public boolean snakeOnWall() {
        boolean wall = false;
        wall = wall || snake.getX() < 0;
        wall = wall || snake.getY() < 0;
        wall = wall || snake.getX() == board.length;
        wall = wall || snake.getY() == board.length;
        return wall;
    }
    
    /**
     * Meant to be used before calling the Snake's movement
     * Marks the current tail spot of the snake as Empty
     */
    public void clearTailSpot() {
        setSpotType(snake.getTailX(), snake.getTailY(), Type.EMPTY);
    }
    /**
     * Meant to be used when Snake gains another joint to ensure the spot is marked
     * (Tail spot gets cleared by clearTailSpot before the Snake moves
     */
    public void markTailSpot() {
        setSpotType(snake.getTailX(), snake.getTailY(), Type.FOOD);
    }
    /**
     * Meant to be used after calling the Snake's movement, and after checking for food
     * Marks the current position of the Snake's head as not empty
     */
    public void markHeadSpot() {
        if(!snakeOnWall()) setSpotType(snake.getX(), snake.getY(), Type.SNAKE);
    }
    
    /**
     * Returns the distance from the Snake's head to either a joint or the wall traveling UP
     * @return 
     */
    public int castUp() {
        int i = 0;
        int x = getSnakeX();
        int y = getSnakeY();
        --y;
        while(y >= 0 && y < board.length && !getSpotType(x, y).equals(Type.SNAKE)) {
            ++i;
            --y;
        }
        return i;
    }
    /**
     * Returns the distance from the Snake's head to either a joint or the wall traveling RIGHT
     * @return 
     */
    public int castRight() {
        int i = 0;
        int x = getSnakeX();
        int y = getSnakeY();
        ++x;
        while(x >= 0 && x < board.length && !getSpotType(x, y).equals(Type.SNAKE)) {
            ++i;
            ++x;
        }
        return i;
    }
    /**
     * Returns the distance from the Snake's head to either a joint or the wall traveling DOWN
     * @return 
     */
    public int castDown() {
        int i = 0;
        int x = getSnakeX();
        int y = getSnakeY();
        ++y;
        while(y >= 0 && y < board.length && !getSpotType(x, y).equals(Type.SNAKE)) {
            ++i;
            ++y;
        }
        return i;
    }
    /**
     * Returns the distance from the Snake's head to either a joint or the wall traveling LEFT
     * @return 
     */
    public int castLeft() {
        int i = 0;
        int x = getSnakeX();
        int y = getSnakeY();
        --x;
        while(x >= 0 && x < board.length && !getSpotType(x, y).equals(Type.SNAKE)) {
            ++i;
            --x;
        }
        return i;
    }
    
  //----------------------------------------------------------------------------
    
    public static enum Direction {
        UP, // -y
        RIGHT, // +x
        DOWN, // +y
        LEFT; // -x
    }
    
}
