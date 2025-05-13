package com.kasyno.menu;





import com.kasyno.gry.BlackJackApp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {

        //Group root = new Group();

        // Ustawienia okna głównego menu
        primaryStage.setTitle("Kasyno - Menu");


        // Tworzenie logo
        Image logo = new Image("Logo_Kasyna.jpg");

        // Tworzenie przycisków dla gier

        Button blackjackButton = new Button("Blackjack");

        Button ruletkaButton = new Button("Ruletka");

        Button slotsMachineButton = new Button("Slots Machine");
  

        // Funkcja dla przycisku Blackjack
        blackjackButton.setOnAction(e -> {
            try {
                BlackJackApp blackjackApp = new BlackJackApp();
                blackjackApp.start(new Stage()); // uruchom w nowym oknie
                primaryStage.hide(); //ukryj menu
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        // Układ VBox do przycisków
        //VBox vbox_bj = new VBox(blackjackButton);

        //VBox vbox_ruletka = new VBox(ruletkaButton);

        //VBox vbox_slots = new VBox(slotsMachineButton);

        //Text przywitanie = new Text();
        //przywitanie.setFont(Font.font("Verdana",17));
        //przywitanie.setText("Witaj w naszym kasynie");

        Pane root = new Pane();
        
        // Obrazek tła dla przycisku
        BackgroundSize buttonBackSize = new BackgroundSize(
        250, 160, false, false, false, false
        );

        blackjackButton.setPrefWidth(250);
        blackjackButton.setPrefHeight(60);
        blackjackButton.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        blackjackButton.setLayoutX(213);
        blackjackButton.setLayoutY(370);

        ruletkaButton.setPrefWidth(250);
        ruletkaButton.setPrefHeight(60);
        ruletkaButton.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        ruletkaButton.setLayoutX(213);
        ruletkaButton.setLayoutY(410);

        slotsMachineButton.setPrefWidth(250);
        slotsMachineButton.setPrefHeight(60);
        slotsMachineButton.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        slotsMachineButton.setLayoutX(213);
        slotsMachineButton.setLayoutY(450);

        root.getChildren().addAll(blackjackButton,ruletkaButton,slotsMachineButton);


        Image backgroundImage = new Image("background.jpg");
        BackgroundSize backgroundSize = new BackgroundSize(1472, 832, true, true, true, false);
        BackgroundImage background = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
        );
        
        Image backgroundButton = new Image("button_background.png");
        BackgroundImage buttonBack = new BackgroundImage(
            backgroundButton,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            buttonBackSize
        );

        
        blackjackButton.setBackground(new Background(buttonBack));

       
        ruletkaButton.setBackground(new Background(buttonBack));


        slotsMachineButton.setBackground(new Background(buttonBack));

        root.setBackground(new Background(background));

        Scene scene = new Scene(root, 1472, 832);
        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(1472);
        primaryStage.setHeight(832);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(logo);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
