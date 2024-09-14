package com.example.proj;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

public class PongController {

    @FXML
    private StackPane root;
    @FXML
    private TextField player1Name, player2Name;
    @FXML
    private Slider ballSpeed;
    @FXML
    private ToggleButton toggleDarkMode;
    @FXML
    private VBox optionsBox; // Reference to the options VBox
    private VBox inGameOptionsBox; // In-game options menu

    private Canvas canvas; // Declare canvas here
    private double ballX = 300, ballY = 200; // Starting position of the ball
    private double ballXSpeed = 1, ballYSpeed = 1;
    private double player1Y = 200, player2Y = 200; // Starting Y positions of players
    private int player1Score = 0, player2Score = 0;
    private final double ballDiameter = 20;
    private Stage gameStage;
    private boolean gameRunning = false;
    private boolean isPaused = false; // Flag to check if the game is paused
    private final int targetScore = 3; // Target score for winning the game

    public void setStage(Stage stage) {
        this.gameStage = stage;
    }

    @FXML
    private void startGame() {
        resetScores(); // Reset scores when starting a new game
        canvas = new Canvas();
        root.getChildren().add(canvas);

        canvas.widthProperty().bind(gameStage.getScene().widthProperty());
        canvas.heightProperty().bind(gameStage.getScene().heightProperty().subtract(toggleDarkMode.heightProperty()));
        canvas.requestFocus();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if (!isPaused) {
                    updateGame(gc);
                }
            }
        };
        timer.start();

        Scene scene = gameStage.getScene();
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.ESCAPE) {
                isPaused = !isPaused;
                toggleInGameOptions();  // Show or hide the pause menu
            } else if (gameRunning && !isPaused) {
                handlePlayerMovement(code, canvas);
            }
        });

        resetGame();
        gameRunning = true;
    }

    @FXML
    private void loadLatestGame() {
        IGameDAO gameDAO = new GameDAO();
        Game latestGame = gameDAO.loadLatestGame();

        if (latestGame != null) {
            // Set the game state
            this.ballX = gameStage.getScene().getWidth() / 2;
            this.ballY = gameStage.getScene().getHeight() / 2;
            this.player1Y = latestGame.getPlayer1Y();
            this.player2Y = latestGame.getPlayer2Y();
            this.player1Score = latestGame.getPlayer1Score();
            this.player2Score = latestGame.getPlayer2Score();

            // Start the game with the loaded state
            startGame();
            showAlert("Game Loaded", "Your latest game has been successfully loaded.");
        } else {
            showAlert("Load Error", "Failed to load the game. No saved game found.");
        }
    }

    @FXML
    private void loadGameFromDatabase() {
        IGameDAO gameDAO = new GameDAO();
        List<String> gameNames = gameDAO.getAllGameNames();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(gameNames.get(0), gameNames);
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Select a game to load:");
        dialog.setContentText("Game Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String gameName = result.get();
            Game game = gameDAO.loadGame(gameName);
            if (game != null) {
                // Set the game state
                this.ballX = gameStage.getScene().getWidth() / 2;
                this.ballY = gameStage.getScene().getHeight() / 2;
                this.player1Y = game.getPlayer1Y();
                this.player2Y = game.getPlayer2Y();
                this.player1Score = game.getPlayer1Score();
                this.player2Score = game.getPlayer2Score();

                // Start the game with the loaded state
                startGame();
                showAlert("Game Loaded", "Your game has been successfully loaded.");
            } else {
                showAlert("Load Error", "Failed to load the game. No saved game found.");
            }
        }
    }

    private void handlePlayerMovement(KeyCode code, Canvas canvas) {
        final double paddleMoveSpeed = 20;
        if (code == KeyCode.W) {
            player1Y = Math.max(player1Y - paddleMoveSpeed, 0);
        } else if (code == KeyCode.S) {
            player1Y = Math.min(player1Y + paddleMoveSpeed, canvas.getHeight() - racketHeight());
        } else if (code == KeyCode.UP) {
            player2Y = Math.max(player2Y - paddleMoveSpeed, 0);
        } else if (code == KeyCode.DOWN) {
            player2Y = Math.min(player2Y + paddleMoveSpeed, canvas.getHeight() - racketHeight());
        }
    }

    private double racketHeight() {
        return 60 * gameStage.getScene().getHeight() / 400; // Adjust racket height based on window size
    }

    private double racketWidth() {
        return 10 * gameStage.getScene().getWidth() / 600; // Adjust racket width based on window size
    }

    private void updateGame(GraphicsContext gc) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();

        ballX += ballXSpeed;
        ballY += ballYSpeed;

        if (ballY <= 0 || ballY >= height - ballDiameter) ballYSpeed *= -1;

        if ((ballX <= racketWidth() && ballY + ballDiameter >= player1Y && ballY <= player1Y + racketHeight())
                || (ballX + ballDiameter >= width - racketWidth() && ballY + ballDiameter >= player2Y && ballY <= player2Y + racketHeight())) {
            ballXSpeed *= -1;
        }

        if (ballX < 0) {
            player2Score++;
            checkWinCondition(gc);
        } else if (ballX > width - ballDiameter) {
            player1Score++;
            checkWinCondition(gc);
        }

        drawGame(gc);
    }

    private void drawGame(GraphicsContext gc) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();

        gc.clearRect(0, 0, width, height);

        gc.setFill(toggleDarkMode.isSelected() ? Color.GRAY : Color.BLACK);
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        gc.fillOval(ballX, ballY, ballDiameter, ballDiameter);

        gc.fillRect(10, player1Y, racketWidth(), racketHeight());
        gc.fillRect(width - racketWidth() - 10, player2Y, racketWidth(), racketHeight());

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 30));
        gc.fillText(player1Name.getText() + ": " + player1Score, 20, 50);
        gc.fillText(player2Name.getText() + ": " + player2Score, width - 200, 50);
    }

    private void checkWinCondition(GraphicsContext gc) {
        drawGame(gc); // Update the screen with the current score
        if (player1Score >= targetScore) {
            showWinner("Player 1", gc);
        } else if (player2Score >= targetScore) {
            showWinner("Player 2", gc);
        } else {
            resetGame();
        }
    }

    private void showWinner(String winner, GraphicsContext gc) {
        isPaused = true;
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 50));
        gc.setTextAlign(TextAlignment.CENTER); // Center align text
        gc.fillText(winner + " Won!", canvas.getWidth() / 2, canvas.getHeight() / 2);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            resetScores();
            returnToMainMenu();
        });
        pause.play();
    }

    private void returnToMainMenu() {
        isPaused = false;
        gameRunning = false;
        root.getChildren().remove(canvas);
        optionsBox.setVisible(false);
        if (inGameOptionsBox != null) {
            inGameOptionsBox.setVisible(false);
        }
    }

    private void resetScores() {
        player1Score = 0;
        player2Score = 0;
    }

    @FXML
    private void toggleOptions() {
        optionsBox.setVisible(!optionsBox.isVisible());
    }

    @FXML
    private void toggleInGameOptions() {
        if (inGameOptionsBox == null) {
            inGameOptionsBox = new VBox(10);
            inGameOptionsBox.setAlignment(Pos.CENTER);
            inGameOptionsBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20;");

            Label pauseLabel = new Label("Game Paused");
            pauseLabel.setFont(new Font("Arial", 24));
            pauseLabel.setTextFill(Color.WHITE);

            Button resumeButton = new Button("Resume");
            resumeButton.setOnAction(e -> {
                isPaused = false;
                inGameOptionsBox.setVisible(false);
                canvas.requestFocus();
            });

            Button saveGameButton = new Button("Save Game");
            saveGameButton.setOnAction(e -> {
                TextInputDialog dialog = new TextInputDialog("DefaultGame");
                dialog.setTitle("Save Game");
                dialog.setHeaderText("Enter the game name:");
                dialog.setContentText("Game Name:");
                dialog.showAndWait().ifPresent(gameName -> {
                    Game game = new GameBuilder()
                            .setGameName(gameName)
                            .setPlayer1Name(player1Name.getText())
                            .setPlayer2Name(player2Name.getText())
                            .setPlayer1Score(player1Score)
                            .setPlayer2Score(player2Score)
                            .setTargetScore(targetScore)
                            .setBallX(ballX)
                            .setBallY(ballY)
                            .setPlayer1Y(player1Y)
                            .setPlayer2Y(player2Y)
                            .build();
                    IGameDAO gameDAO = new GameDAO();
                    gameDAO.upsertGame(game);
                    showAlert("Game Saved", "Your game has been successfully saved.");
                });
            });

            Button exitButton = new Button("Exit Game");
            exitButton.setOnAction(e -> exitGame());

            inGameOptionsBox.getChildren().addAll(pauseLabel, resumeButton, saveGameButton, exitButton);
            root.getChildren().add(inGameOptionsBox);
        }
        inGameOptionsBox.setVisible(true);
        inGameOptionsBox.setLayoutX((gameStage.getWidth() - inGameOptionsBox.getWidth()) / 2);
        inGameOptionsBox.setLayoutY((gameStage.getHeight() - inGameOptionsBox.getHeight()) / 2);
    }

    private void saveGame() {
        TextInputDialog dialog = new TextInputDialog("DefaultGame");
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter the game name:");
        dialog.setContentText("Game Name:");
        dialog.showAndWait().ifPresent(gameName -> {
            Game game = new GameBuilder()
                    .setGameName(gameName)
                    .setPlayer1Name(player1Name.getText())
                    .setPlayer2Name(player2Name.getText())
                    .setPlayer1Score(player1Score)
                    .setPlayer2Score(player2Score)
                    .setTargetScore(targetScore)
                    .setBallX(ballX)
                    .setBallY(ballY)
                    .setPlayer1Y(player1Y)
                    .setPlayer2Y(player2Y)
                    .build();
            IGameDAO gameDAO = new GameDAO();
            gameDAO.upsertGame(game);
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void exitGame() {
        System.exit(0);
    }

    private void resetGame() {
        ballX = gameStage.getScene().getWidth() / 2;
        ballY = gameStage.getScene().getHeight() / 2;
        ballXSpeed = ballSpeed.getValue() / 2;
        ballYSpeed = 1;
        player1Y = gameStage.getScene().getHeight() / 2;
        player2Y = gameStage.getScene().getHeight() / 2;
    }
}
