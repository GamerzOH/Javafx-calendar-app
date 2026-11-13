package com.shreyo.calendar;

import javafx.fxml.*;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layout.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 900);

        String cssPath = getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setTitle("Calendar App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
