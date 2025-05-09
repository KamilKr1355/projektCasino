package com.kasyno.menu;

import com.kasyno.gry.BlackJack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Ustawienia okna głównego menu
        primaryStage.setTitle("Kasyno - Menu");

        // Tworzenie przycisków dla gier

        Button blackjackButton = new Button("Blackjack");

        
        

        // Funkcja dla przycisku Blackjack
        blackjackButton.setOnAction(e -> {
            BlackJack blackjackGame = new BlackJack();
            blackjackGame.start(primaryStage);
        });

        // Układ VBox do przycisków
        VBox vbox = new VBox(blackjackButton);
        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
