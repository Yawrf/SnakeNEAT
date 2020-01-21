/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakeneat;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import static javafx.animation.Animation.INDEFINITE;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import neat.GeneConnection;
import neat.GeneNode;
import neat.Genome;
import neat.Species;
import neat.SpeciesManipulator;
import snake.Board;
import snake.Board.Direction;
import snake.GameController;
import snake.SnakeJoint;
import visualizer.GenomeVisualizerInterface;

/**
 *
 * @author rewil
 */
public class FXMLDocumentController implements Initializable, GenomeVisualizerInterface {
    // Input and Output Descriptions
    //<editor-fold>
    private final String[] inputs = {
        "X Position",
        "Y Position",
        "Food X Position",
        "Food Y Position",
        "Distance UP to Obstacle",
        "Distance RIGHT to Obstacle",
        "Distance DOWN to Obstacle",
        "Distance LEFT to Obstacle",
        "Direction Facing", // Enum rank
        "Constant"
    };
    private final String[] outputs = {
        "UP",
        "RIGHT",
        "DOWN",
        "LEFT"
    };
    //</editor-fold>
    
    private final int countGenomes = 500;
    private final double percentTrim = 0.9;
    private final int countMutations = 5; // Mutations performed between each generation. Higher numbers will result in greater entropy
    
    private final SpeciesManipulator sm = new SpeciesManipulator(inputs.length, outputs.length, countGenomes, percentTrim);
    private HashMap<Genome, GameController> games = new HashMap<>();
    
    private final int boardSize = 25;
    private final int constantValue = 1;
    
    private boolean activeGames = true;
    
  //-FXML-----------------------------------------------------------------------
    
    @FXML Label lblScore;
    @FXML Label lblHighScore;
    @FXML Canvas canvas;
    @FXML Canvas canvasGenome;
    @FXML TextField fieldGenCount;
    private GraphicsContext gcBoard;
    private GraphicsContext gcGenome;
    private final int buffer = 5; // Buffer space between squares on board
    private final long pauseTime = 5; // Time between ticks when playing through game
    private int runs = 1;
    
    private final Color boardColor = Color.BLACK;
    private final Color snakeColor = Color.WHITE;
    private final Color foodColor = Color.GREEN;
    
    private double sizeX;
    private double sizeY;
    
    @FXML
    public void playGenerations() {
        int genCount = 1;
        try {genCount = Integer.parseInt(fieldGenCount.getText());} catch (Exception e) {}
        runs = genCount;
        playGame();
    }
    
    @FXML
    public void playGame() {
        prepareGeneration();
        PauseTransition pt = new PauseTransition(Duration.millis(pauseTime));
        pt.setCycleCount(1);
        pt.setOnFinished(value -> {
            Genome best = getBestLivingGenome();
            drawGame(best);
            drawGenome(best);
            lblScore.setText("" + games.get(best).getScore());
            try{
                int score = Integer.parseInt(lblScore.getText());
                int highScore = Integer.parseInt(lblHighScore.getText());
                lblHighScore.setText("" + Math.max(score, highScore));
            } catch (Exception e) {e.printStackTrace();}
            tick();
            if(activeGames) pt.play();
            else if (runs > 1) {
                --runs;
                prepareGeneration();
                fieldGenCount.setText("" + runs);
                pt.play();
            } else {
                runs = 1;
            }
        });
        pt.play();
    }
    
    @FXML
    public void prepareGeneration() {
        beginNewGeneration();
        System.out.println("Species in Generation: " + sm.getCount());
    }
    
    /**
     * Calls drawBoard, then fills in squares where the Snake and Food are
     * Uses snakeColor to determine color of Snake squares
     * Uses foodColor to determine color of Food square
     * @param g 
     */
    private void drawGame(Genome g) {
        drawBoard();
        if(g == null) return;
        // Snake
        gcBoard.setFill(snakeColor);
        SnakeJoint sj = games.get(g).getBoard().getSnake().getHead();
        while(sj != null) {
            gcBoard.fillRect((sj.getX()*sizeX) + (buffer/2), (sj.getY()*sizeY) + (buffer/2), sizeX - buffer, sizeY - buffer);
            sj = sj.getNextJoint();
        }
        
        // Food
        Board b = games.get(g).getBoard();
        gcBoard.setFill(foodColor);
        gcBoard.fillRect((b.getFoodX()*sizeX) + (buffer/2), (b.getFoodY()*sizeY) + (buffer/2), sizeX - buffer, sizeY - buffer);
        
    }
    /**
     * Draws a blank board of squares with empty buffer space between them
     * Uses boardColor to determine color of squares
     */
    private void drawBoard() {
        gcBoard.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gcBoard.setFill(boardColor);
        
        for(double x = 0; x < boardSize * sizeX; x += sizeX) {
            for(double y = 0; y < boardSize * sizeY; y += sizeY) {
                gcBoard.fillRect(x + (buffer/2), y + (buffer/2), sizeX - buffer, sizeY - buffer);
            }
        }
    }
    
    //-Draw-Genome--------------------------------------------------------------
    
        private double xMod;
        private double yMod;
        private final double nodeSize = 15;
        
    private void drawGenome(Genome g) {
        if(g == null) return;
        
        gcGenome.clearRect(0, 0, canvasGenome.getWidth(), canvasGenome.getHeight());
        g.calculate();
            double size = Math.max(g.getInputs().length, g.getOutputs().length);
        yMod = (canvasGenome.getHeight() / 100d) * (10/size);
        g.visualize(this);
        
    }
    
    @Override
    public void drawNode(GeneNode g) {
        
        double nodeX = g.getX() * xMod;
        double nodeY = g.getY() * yMod;
        gcGenome.setGlobalAlpha(0.5);
        gcGenome.setFill(g.getColor());
        gcGenome.fillOval(nodeX - nodeSize/2, nodeY - nodeSize/2, nodeSize, nodeSize);
        gcGenome.setFill(Color.BLACK);
        gcGenome.setGlobalAlpha(1.0);
        gcGenome.fillText("" + g.getValue(), nodeX - nodeSize*1.5, nodeY);
        
    }
    @Override
    public void drawConnection(GeneConnection c) {
        
        double x1 = c.getIn().getX() * xMod;
        double y1 = c.getIn().getY() * yMod;
        double x2 = c.getOut().getX() * xMod;
        double y2 = c.getOut().getY() * yMod;
        
        gcGenome.setLineWidth(c.getWeightOnScale() * 2);
//        System.out.println(c.getWeightOnScale());
        gcGenome.strokeLine(x1, y1, x2, y2);
        gcGenome.setLineWidth(1d);
    }
    
  //-Function-------------------------------------------------------------------
    
    /**
     * Creates a GameController instance for each Genome in the SpecesManipulator and pairs them in the games HashMap
     */
    private void createPairs() {
        games = new HashMap<>();
        for(Species s : sm.getSpecies()) {
            for(Genome g : s.getMembers()) {
                GameController gc = new GameController(boardSize);
                games.put(g, gc);
            }
        }
        activeGames = true;
    }
    
    /**
     * Sets the direction each Snake is going by calling getOutput for each Genome in games
     * Skips dead snakes
     */
    private void setDirections() {
        for(Genome g : games.keySet()) {
            if(games.get(g).isAlive()) {
                g.calculate(); // Calling this here instead of calling sm.caculate() in tick() to avoid calling calculate on dead snakes
                games.get(g).setSnakeDirection(getOutput(g));
            }
        }
    }
    
    /**
     * Calls the tick function for every Genome in games
     * Skips dead snakes
     */
    private void move() {
        for(Genome g : games.keySet()) {
            if(games.get(g).isAlive()) games.get(g).tick();
        }
    }
    
    /**
     * Runs through all Genomes in games to check if any games are still running
     * Sets activeGames to false if none are found
     */
    private void checkActiveGames() {
        activeGames = getBestLivingGenome() != null;
    }
    
    /**
     * Calls the process method of sm, then create pairs, readying a new generation of games
     */
    public void beginNewGeneration() {
        sm.process(countMutations);
        createPairs();
    }
    
    /**
     * Progresses all Games one move
     */
    public void tick() {
        if(activeGames) {
            setInputs();
            setDirections();
            move();
            checkActiveGames();
        } else {
            setScores();
        }
    }
    
    /**
     * Returns the highest-scoring genome
     * @return 
     */
    private Genome getBestGenome() {
        Genome best = null;
        double score = Integer.MIN_VALUE;
        
        for(Genome g : games.keySet()) {
            if(g.getScore() > score) {
                best = g;
                score = g.getScore();
            }
        }
        
        return best;
    }
    /**
     * Returns the highest-scoring genome that's still alive
     * @return 
     */
    private Genome getBestLivingGenome() {
        Genome best = null;
        double score = Integer.MIN_VALUE;
        
        for(Genome g : games.keySet()) {
            GameController gc = games.get(g);
            if(gc.isAlive() && gc.getScore() > score) {
                best = g;
                score = gc.getScore();
            }
        }
        
        return best;
    }
    
  //-Brain----------------------------------------------------------------------
    
    /**
     * Calculates and sets the inputs for every genome stored in games
     * Skips dead snakes
     */
    private void setInputs() {
        for(Genome g : games.keySet()) {
            if(games.get(g).isAlive()) {
                Board b = games.get(g).getBoard();
                int[] inputs = {
                    b.getSnakeX(), // X Position
                    b.getSnakeY(), // Y Position
                    b.getFoodX(), // Food X Position
                    b.getFoodY(), // Food Y Position
                    b.castUp(), // Distance UP to Obstacle
                    b.castRight(), // Distance RIGHT to Obstacle
                    b.castDown(), // Distance DOWN to Obstacle
                    b.castLeft(), // Distance LEFT to Obstacle
                    b.getSnake().getFacing().ordinal(), // Direction Facing
                    constantValue // Constant
                };
                g.setInputs(inputs);
            }
        }
    }
    
    /**
     * Returns a Direction for the given Genome to turn to as indicated by its highest output
     * @param g
     * @return 
     */
    private Direction getOutput(Genome g) {
        if(g == null) return Direction.DOWN;
        double[] gOutputs = g.getOutputValues();
        
        int index = 0;
        double max = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < gOutputs.length; ++i) {
            if(gOutputs[i] > max) {
                index = i;
                max = gOutputs[i];
            }
        }
        
        return Direction.values()[index];
//        return Direction.DOWN;
    }
    
    /**
     * Sets the score of each Genome in games according to the score in its GameController
     */
    private void setScores() {
        for(Genome g : games.keySet()) {
            g.setScore(games.get(g).getScore() / g.getComplexity());
        }
    }
    
  //----------------------------------------------------------------------------
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        gcBoard = canvas.getGraphicsContext2D();
        gcGenome = canvasGenome.getGraphicsContext2D();
        sizeX = (canvas.getWidth() / boardSize);
        sizeY = (canvas.getHeight() / boardSize);
        
        xMod = canvasGenome.getWidth() / 100d;
        yMod = canvasGenome.getHeight() / 100d;
        
        createPairs();
        drawBoard();
    }    
    
}
