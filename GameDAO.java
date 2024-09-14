package com.example.proj;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO implements IGameDAO {

    @Override
    public void upsertGame(Game game) {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        String selectQuery = "SELECT COUNT(*) FROM Game WHERE game_name = ?";
        String insertQuery = "INSERT INTO Game (game_name, player1_name, player2_name, player1_score, player2_score, target_score, ball_x, ball_y, player1_y, player2_y) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE Game SET player1_name = ?, player2_name = ?, player1_score = ?, player2_score = ?, target_score = ?, ball_x = ?, ball_y = ?, player1_y = ?, player2_y = ? WHERE game_name = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, game.getGameName());
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Game exists, perform an update
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, game.getPlayer1Name());
                    updateStatement.setString(2, game.getPlayer2Name());
                    updateStatement.setInt(3, game.getPlayer1Score());
                    updateStatement.setInt(4, game.getPlayer2Score());
                    updateStatement.setInt(5, game.getTargetScore());
                    updateStatement.setDouble(6, game.getBallX());
                    updateStatement.setDouble(7, game.getBallY());
                    updateStatement.setDouble(8, game.getPlayer1Y());
                    updateStatement.setDouble(9, game.getPlayer2Y());
                    updateStatement.setString(10, game.getGameName());
                    updateStatement.executeUpdate();
                }
            } else {
                // Game does not exist, perform an insert
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setString(1, game.getGameName());
                    insertStatement.setString(2, game.getPlayer1Name());
                    insertStatement.setString(3, game.getPlayer2Name());
                    insertStatement.setInt(4, game.getPlayer1Score());
                    insertStatement.setInt(5, game.getPlayer2Score());
                    insertStatement.setInt(6, game.getTargetScore());
                    insertStatement.setDouble(7, game.getBallX());
                    insertStatement.setDouble(8, game.getBallY());
                    insertStatement.setDouble(9, game.getPlayer1Y());
                    insertStatement.setDouble(10, game.getPlayer2Y());
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Game loadGame(String gameName) {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT * FROM Game WHERE game_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, gameName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new GameBuilder()
                        .setGameName(resultSet.getString("game_name"))
                        .setPlayer1Name(resultSet.getString("player1_name"))
                        .setPlayer2Name(resultSet.getString("player2_name"))
                        .setPlayer1Score(resultSet.getInt("player1_score"))
                        .setPlayer2Score(resultSet.getInt("player2_score"))
                        .setTargetScore(resultSet.getInt("target_score"))
                        .setBallX(resultSet.getDouble("ball_x"))
                        .setBallY(resultSet.getDouble("ball_y"))
                        .setPlayer1Y(resultSet.getDouble("player1_y"))
                        .setPlayer2Y(resultSet.getDouble("player2_y"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Game loadLatestGame() {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT * FROM Game ORDER BY id DESC LIMIT 1"; // Assuming 'id' is an auto-increment field
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return new GameBuilder()
                        .setGameName(resultSet.getString("game_name"))
                        .setPlayer1Name(resultSet.getString("player1_name"))
                        .setPlayer2Name(resultSet.getString("player2_name"))
                        .setPlayer1Score(resultSet.getInt("player1_score"))
                        .setPlayer2Score(resultSet.getInt("player2_score"))
                        .setTargetScore(resultSet.getInt("target_score"))
                        .setBallX(resultSet.getDouble("ball_x"))
                        .setBallY(resultSet.getDouble("ball_y"))
                        .setPlayer1Y(resultSet.getDouble("player1_y"))
                        .setPlayer2Y(resultSet.getDouble("player2_y"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getAllGameNames() {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        List<String> gameNames = new ArrayList<>();
        String query = "SELECT game_name FROM Game";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                gameNames.add(resultSet.getString("game_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameNames;
    }
}
