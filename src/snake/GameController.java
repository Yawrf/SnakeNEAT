
package snake;

import java.util.Random;

/**
 *
 * @author rewil
 */
public class GameController {
    
    private final int boardSize;
    
    private SnakeObject snake = new SnakeObject();
    private Board board;
    private boolean alive = true;
    private int score = 0;
        private final int foodValue = 100; // When food is eaten, score increments by this
        private final int tickValue = 1; // Score increments by this after every move
        private Integer movesNoFoodCutoff = null; // Most moves a snake can go without getting food before being killed (avoids loops). Defaults to number of squares in board if left null
        private int currentMovesNoFood = 0;
    
    public GameController() {
        this(25);
    }
    public GameController(int boardSize) {
        this.boardSize = boardSize;
        board = new Board(boardSize, snake);
        if(movesNoFoodCutoff == null) movesNoFoodCutoff = boardSize * boardSize;
        putFood();
    }
    
    /**
     * Progresses the board one move
     */
    public void tick() {
        if(alive) {
            board.clearTailSpot();
            snake.move();
            if(board.snakeOnFood()) eatFood();
            else ++currentMovesNoFood;
            score += tickValue;

            if(currentMovesNoFood >= movesNoFoodCutoff) {
                alive = false;
                score -= movesNoFoodCutoff;
            }
            if(board.snakeOnSnake() || board.snakeOnWall()) alive = false;
            board.markHeadSpot();
        }
    }
        private void eatFood() {
            snake.addJoint();
            board.markTailSpot();
            putFood();
            score += foodValue;
        }
        private void putFood() {
            Random rand = new Random();
            if(snake.length() < (boardSize * boardSize)) while(!board.putFood(rand.nextInt(boardSize), rand.nextInt(boardSize))) {}
        }
    
    /**
     * Sets the direction the Snake is facing.
     * Fails if it is opposite to the direction it is currently facing
     * @param d
     */
    public void setSnakeDirection(Board.Direction d) {
        snake.setFacing(d);
    }
    
    public boolean isAlive() {
        return alive;
    }
    public int getScore() {
        return score;
    }
    
    public Board getBoard() {
        return board;
    }
    
}
