/**
 * This program is a snake game
 *
 * @author Charles Vidro
 * Date Last Modified: November 20th, 2018
 *
 * CS1131 Fall 2018
 * Lab 3
 */

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class SnakeGame extends Application {
    private ArrayList<Rectangle> snake = new ArrayList<>(); // contains all elements of the "ant" snake
    private ArrayList<ImagePattern> antPattern = new ArrayList<>(); // contains the sequence of ant patterns
    private ArrayList<Rectangle> obstacles = new ArrayList<>(); // contains all of the snake obstacles
    private ArrayList<Rectangle> thrownApples = new ArrayList<>(); // contains all of the thrown apples
    private ArrayList<Double> thrownApplesXVelocity = new ArrayList<>(); // contains all of the thrown apples x velocity, 0 = null
    private ArrayList<Double> thrownApplesYVelocity = new ArrayList<>(); // contains all of the thrown apples y velocity, 0 = null
    private Scene scene; // the scene
    private Stage primaryStage; // the primary stage
    private double dy = -25; // the head ant's y velocity
    private double dx = 0; // the head ant's x velocity
    private Rectangle head; // the head ant
    private Rectangle food = new Rectangle(25, 25); // the food rectangle
    private Rectangle powerup = new Rectangle(25, 25); // the powerup rectangle
    private Rectangle appleCounter = new Rectangle(40, 40); // the apple counter icon in the dashboard
    private int powerupInt; // the powerup case
    private Timeline animation; // the animation timeline
    private BorderPane borderPane; // layout of the window
    private Pane game; // the pane the game is played in
    private Text stage; // the stage in the dashboard
    private Text score; // the score in the dashboard
    private int highscore = 0; // the known highscore
    private Text highscoreText; // the highscore text in the dashboard
    private int scoreNumber = 0; // the current score in the game
    private AudioClip eat; // eat sound
    private AudioClip end; // game end sound
    private AudioClip powerupSound; // powerup sound
    private AudioClip snakeSound; // obstacle sound
    private AudioClip levelup; // stage change sound
    private ImagePattern antUp = new ImagePattern(new Image("/ant1.png")); // ant facing up pattern
    private ImagePattern antRight = new ImagePattern(new Image("/ant2.png")); // ant facing right pattern
    private ImagePattern antDown = new ImagePattern(new Image("/ant3.png")); // ant facing down pattern
    private ImagePattern antLeft = new ImagePattern(new Image("/ant4.png")); // ant facing left pattern
    private ImagePattern newHeadImage; // the new head image
    private int moves = 0; // the total number of moves
    private int movesSinceLastPowerup; // the number of moves since last powerup
    private int stageThreeMoves; // the number of total moves when stage three starts
    private boolean stage1 = true; // sets stage 1 mechanics
    private boolean stage2 = false; // sets stage 2 mechanics
    private boolean stage3 = false; // sets stage 3 mechanics
    private boolean stage4 = false; // sets stage 4 mechanics
    private Scanner scanner; // scanner to read highscore document
    private PrintWriter printWriter; // printwriter to write highscore document

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param paramStage the primary stage for this application, onto which
     * the application scene can be set. The primary stage will be embedded in
     * the browser if the application was launched as an applet.
     * Applications may create other stages, if needed, but they will not be
     * primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage paramStage) {
        this.primaryStage = paramStage;
        this.primaryStage.setResizable(false);
        primaryStage.setHeight(779);
        primaryStage.setWidth(706);
        this.primaryStage.setTitle("Snake Game - Charles Vidro");
        this.primaryStage.getIcons().addAll(new Image("/ant1.png"));
        restart();
        this.primaryStage.show();
    }

    /**
     * A random location on one axis of the gameboard, aligned to the pixel system used (25 pixels per object)
     * @return a random integer, stored as a double
     */
    private double randomLocation() {
        return 25 * ((int) (Math.random() * 28));
    }

    /**
     * Sets the food object in a location not occupied
     */
    private void setFood() {
        double x = randomLocation();
        double y = randomLocation();

        // check if space is occupied
        while (isOccupied(x, y)) {
            x = randomLocation();
            y = randomLocation();
        }

        food.setX(x);
        food.setY(y);
        System.out.println("FOOD: " + x + ", " + y);
    }

    /**
     * Sets the powerup in a location not occupied
     */
    private void setPowerup() {
        movesSinceLastPowerup = 0;

        double x = randomLocation();
        double y = randomLocation();

        // check if space is occupied
        while (isOccupied(x, y)) {
            x = randomLocation();
            y = randomLocation();
        }

        powerup.setX(x);
        powerup.setY(y);

        // choosing the powerup
        if (animation == null || animation.getCurrentRate() <= 1.5 && animation.getCurrentRate() >= .5) {
            powerupInt = (int) (Math.random() * 10);
        } else if (animation.getCurrentRate() < .5) {
            int rand = (int) (Math.random() * 2);
            if (rand == 1) {
                powerupInt = rand;
            } else {
                powerupInt = 0;
            }
        } else {
            int rand = (int) (Math.random() * 2);
            if (rand == 1) {
                powerupInt = 6;
            } else {
                powerupInt = 0;
            }
        }

        // setting the graphic
        switch (powerupInt) {
            case 0:
                powerup.setFill(new ImagePattern(new Image("/picnicBasket.png")));
                break;
            case 1:
                powerup.setFill(new ImagePattern(new Image("/watermelon.png")));
                break;
            case 2:
                powerup.setFill(new ImagePattern(new Image("/watermelon.png")));
                break;
            case 3:
                powerup.setFill(new ImagePattern(new Image("/watermelon.png")));
                break;
            case 4:
                powerup.setFill(new ImagePattern(new Image("/watermelon.png")));
                break;
            case 5:
                powerup.setFill(new ImagePattern(new Image("/watermelon.png")));
                break;
            case 6:
                powerup.setFill(new ImagePattern(new Image("/ham.png")));
                break;
            case 7:
                powerup.setFill(new ImagePattern(new Image("/ham.png")));
                break;
            case 8:
                powerup.setFill(new ImagePattern(new Image("/ham.png")));
                break;
            case 9:
                powerup.setFill(new ImagePattern(new Image("/ham.png")));
                break;

        }

        System.out.println("POWERUP CASE: " + powerupInt);
        System.out.println("POWERUP: " + x + ", " + y);
    }

    /**
     * Sets end game screen and calls end-game methods
     */
    private void endGame() {
        System.out.println("GAME ENDED");
        end.play();
        animation.stop();
        setHighscore();
        Pane endScreen = new Pane();
        BackgroundImage endBackground = new BackgroundImage(new Image("/endScreen.png", 700, 700, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        endScreen.setBackground(new Background(endBackground));

        Button playAgain = new Button("Play Again");
        playAgain.setLayoutX(315);
        playAgain.setLayoutY(480);
        playAgain.setScaleX(2);
        playAgain.setScaleY(2);
        playAgain.setOnAction(e -> restart());
        endScreen.getChildren().add(playAgain);
        borderPane.setCenter(endScreen);
    }

    /**
     * Sets up the dashboard
     */
    private void setDashboard() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setMinHeight(50);
        hBox.setMaxHeight(50);

        BackgroundImage dashboardBackground = new BackgroundImage(new Image("/dashboardBackground.png", 700, 50, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        hBox.setBackground(new Background(dashboardBackground));

        highscoreText = new Text("Best: " + highscore);
        updateScore();
        highscoreText.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 32));

        stage = new Text("STAGE ONE");
        stage.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 32));

        score = new Text("" + scoreNumber);
        score.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 32));
        appleCounter.setFill(new ImagePattern(new Image("/apple.png")));

        Region midLeft = new Region();
        HBox.setHgrow(midLeft, Priority.ALWAYS);

        Region midRight = new Region();
        HBox.setHgrow(midRight, Priority.ALWAYS);

        hBox.getChildren().addAll(highscoreText, midLeft, stage, midRight, appleCounter, score);
        borderPane.setTop(hBox);
    }

    /**
     * Writes a new highscore to file
     */
    private void setHighscore() {
        if (scoreNumber > highscore) {
            highscore = scoreNumber;
            try {
                File highscoreFile = new File("highscore.txt");
                printWriter = new PrintWriter(highscoreFile);
                printWriter.print("" + highscore);
                System.out.println("Highscore " + highscore + " recorded!");
            } catch (FileNotFoundException e) {
                System.out.println("HIGH SCORE FILE NOT FOUND - SET HIGHSCORE");
            } finally {
                printWriter.close();
            }
        }
    }

    private void updateScore() {
        try {
            File highscoreFile = new File("highscore.txt");
            scanner = new Scanner(highscoreFile);
            highscore = scanner.nextInt();
            highscoreText.setText("Best: " + highscore);
        } catch (FileNotFoundException e) {
            System.out.println("HIGH SCORE FILE NOT FOUND - UPDATE HIGHSCORE");
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * sets up the game sounds
     */
    private void setSounds() {
        // eat sound
        URL eatResource = getClass().getResource("eat.mp3");
        try {
            eatResource = new File("eat.mp3").toURI().toURL();
            eat = new AudioClip(eatResource.toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // end sound
        URL endResource = getClass().getResource("end.mp3");
        try {
            endResource = new File("end.mp3").toURI().toURL();
            end = new AudioClip(endResource.toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // powerup sound
        URL powerupResource = getClass().getResource("powerupSound.mp3");
        try {
            powerupResource = new File("powerupSound.mp3").toURI().toURL();
            powerupSound = new AudioClip(powerupResource.toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // snake sound
        URL snakeResource = getClass().getResource("snake.mp3");
        try {
            snakeResource = new File("snake.mp3").toURI().toURL();
            snakeSound = new AudioClip(snakeResource.toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // level up
        URL levelupResource = getClass().getResource("snake.mp3");
        try {
            levelupResource = new File("levelup.mp3").toURI().toURL();
            levelup = new AudioClip(levelupResource.toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an obstacle (snake) to a location not occupied
     */
    private void addObstacle() {
        double x = randomLocation();
        double y = randomLocation();

        // check if space is occupied
        while (isOccupied(x, y)) {
            x = randomLocation();
            y = randomLocation();
        }

        Rectangle obstacle = new Rectangle(x, y, 25, 25);
        obstacle.setFill(new ImagePattern(new Image("/snake.png")));
        game.getChildren().add(obstacle);
        obstacles.add(obstacle);
        System.out.println("Obstacle added!");
    }

    /**
     * Determines if a location is occupied
     * @param x the x position of the suggested coordinate
     * @param y the y position of the suggested coordinate
     * @return true if the space is occupied, false if not
     */
    private boolean isOccupied(double x, double y) {
        // occupied by snake
        for (int i = 0; i < snake.size(); i++) {
            if (snake.get(i).getX() == x && snake.get(i).getY() == y
                    || food.getX() == x && food.getY() == y) {
                return true;
            }
        }

        // occupied by obstacle
        for (int i = 0; i < obstacles.size(); i++) {
            if (obstacles.get(i).getX() == x && obstacles.get(i).getY() == y) {
                return true;
            }
        }

        // occupied by food
        if (x == food.getX() || y == food.getY()) {
            return true;
        }

        // occupied by powerup
        if (x == powerup.getX() || y == powerup.getY()) {
            return true;
        }

        return false;
    }

    /**
     * Creates a rectangle to represent the thrown apple
     */
    private void throwApple() {
        Rectangle apple = new Rectangle(25, 25);

        // set apple velocity & position
        if (dx == 25) {
            thrownApplesXVelocity.add(dx);
            thrownApplesYVelocity.add(null);
            apple.setX(head.getX() + 25);
            apple.setY(head.getY());
        } else if (dx == -25) {
            thrownApplesXVelocity.add(dx);
            thrownApplesYVelocity.add(null);
            apple.setX(head.getX() - 25);
            apple.setY(head.getY());
        } else if (dy == 25) {
            thrownApplesXVelocity.add(null);
            thrownApplesYVelocity.add(dy);
            apple.setX(head.getX());
            apple.setY(head.getY() + 25);
        } else {
            thrownApplesXVelocity.add(null);
            thrownApplesYVelocity.add(dy);
            apple.setX(head.getX());
            apple.setY(head.getY() - 25);
        }

        apple.setFill(new ImagePattern(new Image("/apple.png")));
        thrownApples.add(apple);
        game.getChildren().add(apple);
    }

    /**
     * Sets up or restarts the game screen
     */
    private void restart() {
        System.out.println("restart called");
        dy = -25;
        dx = 0;
        scoreNumber = 0;
        newHeadImage = antUp;
        snake.clear();
        antPattern.clear();
        obstacles.clear();
        moves = 0;
        stage1 = true;
        stage2 = false;
        stage3 = false;
        stage4 = false;

        borderPane = new BorderPane();
        game = new Pane();
        borderPane.setCenter(game);

        // Starting nodes
        head = new Rectangle(25, 25);
        head.setX(15 * 25);
        head.setY(28 * 25);

        snake.add(head);
        antPattern.add(antUp);
        food.setFill(new ImagePattern(new Image("/apple.png")));
        setFood();
        setPowerup();
        game.getChildren().addAll(head, food, powerup);

        BackgroundImage grassBackground = new BackgroundImage(new Image("/gameBackground.png", 700, 700, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        game.setBackground(new Background(grassBackground));


        setDashboard();
        updateScore();
        setSounds();

        // Timeline
        animation = new Timeline(new KeyFrame(Duration.millis(125),
                new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent e) {
                        moves++;

                        // stage 1 to stage 2
                        if (stage1 && scoreNumber >= 10) {
                            levelup.play();
                            stage2 = true;
                            stage1 = false;
                            System.out.println("-----------------------\nSTAGE TWO\n-----------------------");
                            stage.setText("STAGE TWO");
                        }

                        // stage 2 to stage 3
                        if (stage2 && scoreNumber >= 25) {
                            levelup.play();
                            stage3 = true;
                            stage2 = false;
                            stageThreeMoves = moves;

                            // remove all snakes from game pane
                            for (int i = 0; i < obstacles.size(); i++) {
                                game.getChildren().remove(obstacles.get(i));
                            }

                            // remove all snakes from obstacle array
                            obstacles.clear();

                            // remove all ants except head
                            while (1 < snake.size()) {
                                game.getChildren().remove(snake.get(1));
                                antPattern.remove(1);
                                snake.remove(1);
                            }

                            System.out.println("-----------------------\nSTAGE THREE\n-----------------------");
                            stage.setText("STAGE THREE");
                        }

                        // stage 3 to stage 4
                        if (stage3 && (stageThreeMoves + 250) < moves) {
                            levelup.play();
                            stage4 = true;
                            stage3 = false;

                            // remove all snakes from game pane
                            for (int i = 0; i < obstacles.size(); i++) {
                                game.getChildren().remove(obstacles.get(i));
                            }

                            // remove all snakes from obstacle array
                            obstacles.clear();

                            System.out.println("-----------------------\nSTAGE FOUR\n-----------------------");
                            stage.setText("STAGE FOUR");
                        }

                        // stage three wave one setup
                        if (stage3 && stageThreeMoves == moves) {
                            System.out.println("Stage Three Wave One");
                            for (int i = 0; i <= (25 * 28); i += 100) {
                                Rectangle obstacle = new Rectangle(i, -25, 25, 25);
                                obstacle.setFill(new ImagePattern(new Image("/snake.png")));
                                game.getChildren().add(obstacle);
                                obstacles.add(obstacle);
                            }
                        }

                        // stage three wave two setup
                        if (stage3 && stageThreeMoves + 30 == moves) {
                            // remove all snakes from game pane
                            for (int i = 0; i < obstacles.size(); i++) {
                                game.getChildren().remove(obstacles.get(i));
                            }

                            // remove all snakes from obstacle array
                            obstacles.clear();

                            System.out.println("Stage Three Wave Two");

                            for (int i = 0; i <= (25 * 28); i += 50) {
                                Rectangle obstacle = new Rectangle(750, i, 25, 25);
                                obstacle.setFill(new ImagePattern(new Image("/snake.png")));
                                game.getChildren().add(obstacle);
                                obstacles.add(obstacle);
                            }
                        }

                        // stage three wave three setup
                        if (stage3 && stageThreeMoves + 82 == moves) {
                            // remove all snakes from game pane
                            for (int i = 0; i < obstacles.size(); i++) {
                                game.getChildren().remove(obstacles.get(i));
                            }

                            // remove all snakes from obstacle array
                            obstacles.clear();

                            System.out.println("Stage Three Wave Three");

                            int y = 25;
                            for (int x = (-25 * 28); x <= 0; x += 50) {

                                Rectangle obstacle = new Rectangle(x, y, 25, 25);
                                obstacle.setFill(new ImagePattern(new Image("/snake.png")));
                                game.getChildren().add(obstacle);
                                obstacles.add(obstacle);

                                y += 50;
                            }
                        }

                        // stage three wave four setup
                        if (stage3 && stageThreeMoves + 182 == moves) {
                            // remove all snakes from game pane
                            for (int i = 0; i < obstacles.size(); i++) {
                                game.getChildren().remove(obstacles.get(i));
                            }

                            // remove all snakes from obstacle array
                            obstacles.clear();

                            System.out.println("Stage Three Wave Four");

                            int x;
                            for (int y = 900; y >= 775; y -= 25) {
                                x = 28 * (int) (Math.random() * 25);
                                Rectangle obstacle = new Rectangle(x, y, 25, 25);

                                x = 28 * (int) (Math.random() * 25);
                                Rectangle obstacle2 = new Rectangle(x, y, 25, 25);

                                obstacle.setFill(new ImagePattern(new Image("/snake.png")));
                                obstacle2.setFill(new ImagePattern(new Image("/snake.png")));

                                game.getChildren().addAll(obstacle, obstacle2);
                                obstacles.add(obstacle);
                                obstacles.add(obstacle2);
                            }
                        }

                        double lastX = snake.get(0).getX();
                        double lastY = snake.get(0).getY();

                        // Add head pattern to ant pattern
                        antPattern.add(newHeadImage);

                        //System.out.println(lastX + ", " + lastY + " | dx: " + dx + " dy: " + dy);

                        // Adjust first element manually
                        head.setX(lastX + dx);
                        head.setY(lastY + dy);
                        head.setFill(newHeadImage);

                        // Move snake
                        for (int i = 1; i < snake.size(); i++) {
                            double currentX = snake.get(i).getX();
                            double currentY = snake.get(i).getY();
                            snake.get(i).setX(lastX);
                            snake.get(i).setY(lastY);
                            snake.get(i).setFill(antPattern.get(antPattern.size() - i));

                            lastX = currentX;
                            lastY = currentY;
                        }

                        // Check game ending scenarios
                        // Head out of bounds
                        if (head.getX() > 675 || head.getX() < 0 || head.getY() > 675 || head.getY() < 0) {
                            endGame();
                        }

                        // snake crossover
                        for (int i = 1; i < snake.size(); i++) {
                            if (head.getX() == snake.get(i).getX() && head.getY() == snake.get(i).getY()) {
                                System.out.println("Snake Ate Itself");
                                endGame();
                            }
                        }

                        // Deal with food scenario
                        if (food.getX() == head.getX() && food.getY() == head.getY()) {
                            scoreNumber++;
                            eat.play();
                            score.setText("" + scoreNumber);
                            setFood();

                            // grow snake
                            if (!stage3) {
                                Rectangle body = new Rectangle(lastX, lastY, 25, 25);
                                body.setFill(new ImagePattern(new Image("/ant1.png")));
                                game.getChildren().add(body);
                                snake.add(body);
                            }
                        }

                        // Add obstacles for stage 2
                        if (stage2 && ((moves + 1) % 50 == 1)) {
                            addObstacle();
                            System.out.print("stage2 ");
                        }

                        // add obstacles for stage 4
                        if (stage4 && ((moves + 1) % 15 == 1)) {
                            addObstacle();
                            System.out.print("stage4 ");
                        }

                        // stage three wave one update
                        if (stage3 && stageThreeMoves < moves && moves < stageThreeMoves + 30) {
                            for (int i = 0; i < obstacles.size(); i++) {
                                obstacles.get(i).setY(obstacles.get(i).getY() + 25);
                            }
                        }

                        // stage three wave two update
                        if (stage3 && ((stageThreeMoves + 50) < moves) && (moves < (stageThreeMoves + 82))) {
                            for (int i = 0; i < obstacles.size(); i++) {
                                obstacles.get(i).setX(obstacles.get(i).getX() - 25);
                            }
                        }

                        // stage three wave three update
                        if (stage3 && ((stageThreeMoves + 120) < moves) && (moves < (stageThreeMoves + 182))) {
                            for (int i = 0; i < obstacles.size(); i++) {
                                obstacles.get(i).setX(obstacles.get(i).getX() + 25);
                            }
                        }

                        // stage three wave four update
                        if (stage3 && ((stageThreeMoves + 200) < moves) && (moves < (stageThreeMoves + 300))) {
                            for (int i = 0; i < obstacles.size(); i++) {
                                obstacles.get(i).setY(obstacles.get(i).getY() - 25);
                            }
                        }

                        // hit with obstacle scenario
                        for (int i = 0; i < obstacles.size(); i++) {
                            if (head.getX() == obstacles.get(i).getX() && head.getY() == obstacles.get(i).getY()) {
                                System.out.println("Hit Obstacle");
                                endGame();
                            }
                        }

                        // Deal with powerup scenario
                        if (powerup.getX() == head.getX() && powerup.getY() == head.getY()) {
                            powerupSound.play();

                            // apply powerup
                            switch (powerupInt) {
                                case 0:
                                    scoreNumber += 10;
                                    score.setText("" + scoreNumber);
                                    animation.setRate(1.0);
                                    break;
                                case 1:
                                    animation.setRate(animation.getCurrentRate() + 0.25);
                                    break;
                                case 2:
                                    animation.setRate(animation.getCurrentRate() + 0.25);
                                    break;
                                case 3:
                                    animation.setRate(animation.getCurrentRate() + 0.25);
                                    break;
                                case 4:
                                    animation.setRate(animation.getCurrentRate() + 0.25);
                                    break;
                                case 5:
                                    animation.setRate(animation.getCurrentRate() + 0.25);
                                    break;
                                case 6:
                                    animation.setRate(animation.getCurrentRate() - 0.25);
                                    break;
                                case 7:
                                    animation.setRate(animation.getCurrentRate() - 0.25);
                                    break;
                                case 8:
                                    animation.setRate(animation.getCurrentRate() - 0.25);
                                    break;
                                case 9:
                                    animation.setRate(animation.getCurrentRate() - 0.25);
                                    break;
                            }

                            // System.out.println("POWERUP: " + powerupInt);
                            setPowerup();
                        }

                        // Deal with thrown apple scenario
                        if (thrownApples.size() > 0) {
                            // move thrown apple
                            for (int i = 0; i < thrownApples.size(); i++) {
                                if (thrownApplesXVelocity.get(i) != null) {
                                    thrownApples.get(i).setX(thrownApples.get(i).getX() + thrownApplesXVelocity.get(i));
                                } else {
                                    thrownApples.get(i).setY(thrownApples.get(i).getY() + thrownApplesYVelocity.get(i));
                                }
                            }

                            // Check if apples have hit player
                            for (int i = 0; i < thrownApples.size(); i++) {
                                for (int j = 0; j < snake.size(); j++) {
                                    if (thrownApples.get(i).getX() == snake.get(j).getX()
                                            && thrownApples.get(i).getY() == snake.get(j).getY()) {
                                        System.out.println("Thrown Apple Hit Player");
                                        endGame();
                                    }
                                }
                            }

                            // check if apples have hit snake
                            for (int i = 0; i < thrownApples.size(); i++) {
                                for (int j = 0; j < obstacles.size(); j++) {
                                    if (thrownApples.get(i).getX() == obstacles.get(j).getX()
                                            && thrownApples.get(i).getY() == obstacles.get(j).getY()) {
                                        System.out.println("Thrown Apple Hit Snake");
                                        snakeSound.play();
                                        game.getChildren().remove(obstacles.get(j));
                                        obstacles.remove(j);
                                    }
                                }
                            }

                            // check if apples are out of bounds
                            for (int i = 0; i < thrownApples.size(); i++) {
                                if (thrownApples.get(i).getX() > 675 || thrownApples.get(i).getY() > 675
                                        || thrownApples.get(i).getX() < 0 || thrownApples.get(i).getY() < 0) {
                                    System.out.println("Thrown Apple out of bounds");
                                    game.getChildren().remove(thrownApples.get(i));
                                    thrownApples.remove(i);
                                    thrownApplesXVelocity.remove(i);
                                    thrownApplesYVelocity.remove(i);
                                }
                            }
                        }

                        // choose a new powerup automatically every 100 moves since last powerup
                        if ((++movesSinceLastPowerup % 100) == 0) {
                            System.out.println("Reset powerup");
                            animation.setRate(1.0);
                            setPowerup();
                        }

                        // remove any unnecessary ant patterns
                        while (antPattern.size() > snake.size()) {
                            antPattern.remove(0);
                        }
                    }
                }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        // Detecting user input movement
        game.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN:
                    dx = 0;
                    dy = 25;
                    newHeadImage = antDown;
                    break;
                case S:
                    dx = 0;
                    dy = 25;
                    newHeadImage = antDown;
                    break;
                case UP:
                    dx = 0;
                    dy = -25;
                    newHeadImage = antUp;
                    break;
                case W:
                    dx = 0;
                    dy = -25;
                    newHeadImage = antUp;
                    break;
                case LEFT:
                    dx = -25;
                    dy = 0;
                    newHeadImage = antLeft;
                    break;
                case A:
                    dx = -25;
                    dy = 0;
                    newHeadImage = antLeft;
                    break;
                case RIGHT:
                    dx = 25;
                    dy = 0;
                    newHeadImage = antRight;
                    break;
                case D:
                    dx = 25;
                    dy = 0;
                    newHeadImage = antRight;
                    break;
                case P:
                    if (animation.getStatus() == Animation.Status.PAUSED) {
                        animation.play();
                    } else {
                        animation.pause();
                    }
                case SPACE: {
                    if (stage2 || stage4 && scoreNumber > 0) {
                        throwApple();
                        scoreNumber--;
                        score.setText("" + scoreNumber);
                    }
                }
            }
        });

        scene = new Scene(borderPane, 700, 750);
        primaryStage.setScene(scene);
        //System.out.println("Width: " + primaryStage.getWidth() + " | Height: " + primaryStage.getHeight());
        game.requestFocus();
    }

    /**
     * starts the game
     * @param args arguments passed by the user
     */
    public static void main(String[] args) {
        launch(args);
    }
}