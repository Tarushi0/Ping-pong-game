package com.example.proj;

public class Game {
    private String gameName;
    private String player1Name;
    private String player2Name;
    private int player1Score;
    private int player2Score;
    private int targetScore;
    private double ballX;
    private double ballY;
    private double player1Y;
    private double player2Y;

    public Game(GameBuilder builder) {
        this.gameName = builder.gameName;
        this.player1Name = builder.player1Name;
        this.player2Name = builder.player2Name;
        this.player1Score = builder.player1Score;
        this.player2Score = builder.player2Score;
        this.targetScore = builder.targetScore;
        this.ballX = builder.ballX;
        this.ballY = builder.ballY;
        this.player1Y = builder.player1Y;
        this.player2Y = builder.player2Y;
    }

    public String getGameName() {
        return gameName;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public double getBallX() {
        return ballX;
    }

    public double getBallY() {
        return ballY;
    }

    public double getPlayer1Y() {
        return player1Y;
    }

    public double getPlayer2Y() {
        return player2Y;
    }
}
