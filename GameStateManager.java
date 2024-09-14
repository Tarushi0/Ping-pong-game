package com.example.proj;

import java.io.*;

public class GameStateManager {
    private static GameStateManager instance;
    private static final String SAVE_FILE = "pong_game_state.ser";

    private GameStateManager() {}

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public static class GameState implements Serializable {
        double ballX, ballY, player1Y, player2Y;
        int player1Score, player2Score;

        public GameState(double ballX, double ballY, double player1Y, double player2Y, int player1Score, int player2Score) {
            this.ballX = ballX;
            this.ballY = ballY;
            this.player1Y = player1Y;
            this.player2Y = player2Y;
            this.player1Score = player1Score;
            this.player2Score = player2Score;
        }
    }

    public void saveGame(GameState gameState) {
        try (FileOutputStream fileOut = new FileOutputStream(SAVE_FILE);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(gameState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameState loadGame() {
        try (FileInputStream fileIn = new FileInputStream(SAVE_FILE);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
