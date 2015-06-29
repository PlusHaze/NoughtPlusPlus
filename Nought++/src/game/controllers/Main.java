package game.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/game/views/sceneHome.fxml"));

        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image("/game/resources/Spaceship.png"));
        primaryStage.sizeToScene();

        primaryStage.setTitle("Nought ++");
        primaryStage.show();
        primaryStage.toFront();
    }

    public static void main(String[] args) {
        launch(args);
    }
}