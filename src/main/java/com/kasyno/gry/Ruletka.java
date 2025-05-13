package com.kasyno.gry;

import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Ruletka extends Application {

    private static final String[] NUMBERS = {
        "0", "32", "15", "19", "4", "21", "2", "25", "17", "34", "6", "27", "13",
        "36", "11", "30", "8", "23", "10", "5", "24", "16", "33", "1", "20", "14",
        "31", "9", "22", "18", "29", "7", "28", "12", "35", "3", "26"
    };

    private double currentAngle = 0;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        ImageView wheel = new ImageView(new Image("ruletka.png"));
        wheel.setFitWidth(400);
        wheel.setFitHeight(400);
        wheel.setX(536);
        wheel.setY(50);

        ImageView arrowUp = new ImageView(new Image("arrowUp.png"));
        arrowUp.setFitWidth(50);
        arrowUp.setFitHeight(50);
        arrowUp.setX(711);
        arrowUp.setY(450); 

        Button spinButton = new Button("Zakręć");
        spinButton.setLayoutX(270);
        spinButton.setLayoutY(500);

        Text resultText = new Text("");
        resultText.setLayoutX(250);
        resultText.setLayoutY(470);
        resultText.setStyle("-fx-font-size: 20px;");

        spinButton.setOnAction(e -> {
            Random rand = new Random();
            int selectedIndex = rand.nextInt(NUMBERS.length);

            double degreesPerField = 360.0 / NUMBERS.length; 
            double targetAngle = selectedIndex * degreesPerField;

            
            double desiredAngle = 180 - targetAngle;

        
            double relativeAngle = (360 - (currentAngle % 360) + desiredAngle) % 360;

            
            double fullSpin = 5 * 360;
            double angleToRotate = fullSpin + relativeAngle;

            
            currentAngle += angleToRotate;

            RotateTransition rt = new RotateTransition(Duration.seconds(4), wheel);
            rt.setInterpolator(Interpolator.EASE_OUT);
            rt.setByAngle(angleToRotate);
            rt.setCycleCount(1);
            rt.setOnFinished(event -> resultText.setText("Wylosowano: " + NUMBERS[selectedIndex]));
            rt.play();
        });

        root.getChildren().addAll(arrowUp, wheel, spinButton, resultText);
        Scene scene = new Scene(root, 700, 600);
        stage.setTitle("Ruletka");
        stage.setScene(scene);
        stage.show();
    }
}
