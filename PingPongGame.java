package com.example.proj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PingPongGame extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Pong-view.fxml"));
        Parent root = loader.load();
        PongController controller = loader.getController();

        // Create a new stage with specified dimensions
        Stage gameStage = new Stage();
        gameStage.setTitle("Ping Pong Game");
        gameStage.setScene(new Scene(root, 900, 450));

        controller.setStage(gameStage);

        gameStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
