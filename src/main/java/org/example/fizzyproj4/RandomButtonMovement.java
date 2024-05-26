package org.example.fizzyproj4;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class RandomButtonMovement extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Button button = new Button("Move Me");
        root.getChildren().add(button);

        // Tworzenie Timeline, ale nie uruchamianie go od razu
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
            moveButtonRandomly(button);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Dodanie obsługi zdarzenia kliknięcia na przycisk
        button.setOnAction(event -> {
            timeline.play(); // Rozpoczęcie animacji po kliknięciu przycisku
        });

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Random Button Movement");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void moveButtonRandomly(Button button) {
        Random random = new Random();

        // Generowanie losowych pozycji w granicach sceny
        double newX = random.nextDouble() * (WIDTH - button.getWidth());
        double newY = random.nextDouble() * (HEIGHT - button.getHeight());

        // Ustawienie nowej pozycji przycisku
        button.setLayoutX(newX);
        button.setLayoutY(newY);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
