package main.java.com.kasyno;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class BlackJack extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Ustawienia okna gry blackjacka
        primaryStage.setTitle("Gra Blackjack");

        // Tworzenie prostego tekstu
        Label label = new Label("Witamy w Blackjacku!");
        StackPane root = new StackPane();
        root.getChildren().add(label);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
