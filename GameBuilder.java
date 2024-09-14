package com.example.proj;

public class GameBuilder {
    String gameName;
    String player1Name;
    String player2Name;
    int player1Score;
    int player2Score;
    int targetScore;
    double ballX;
    double ballY;
    double player1Y;
    double player2Y;

    public GameBuilder setGameName(String gameName) {
        this.gameName = gameName;
        return this;
    }

    public GameBuilder setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
        return this;
    }

    public GameBuilder setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
        return this;
    }

    public GameBuilder setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
        return this;
    }

    public GameBuilder setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
        return this;
    }

    public GameBuilder setTargetScore(int targetScore) {
        this.targetScore = targetScore;
        return this;
    }

    public GameBuilder setBallX(double ballX) {
        this.ballX = ballX;
        return this;
    }

    public GameBuilder setBallY(double ballY) {
        this.ballY = ballY;
        return this;
    }

    public GameBuilder setPlayer1Y(double player1Y) {
        this.player1Y = player1Y;
        return this;
    }

    public GameBuilder setPlayer2Y(double player2Y) {
        this.player2Y = player2Y;
        return this;
    }

    public Game build() {
        return new Game(this);
    }
}
