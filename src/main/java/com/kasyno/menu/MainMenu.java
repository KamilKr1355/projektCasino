package com.kasyno.menu;





import com.kasyno.database.UserDAO;
import com.kasyno.gry.BlackJackApp;
import com.kasyno.gry.Ruletka;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainMenu extends Application {
    private static com.kasyno.player.Player player;

    public static void setPlayer(com.kasyno.player.Player p) {
        player = p;
    }

    private void showBetHistory() {
        Stage historyStage = new Stage();
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        Label title = new Label("Ostatnie 100 zakładów");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(UserDAO.getLastBets(player.getId(), 100));

        Button closeButton = new Button("Zamknij");
        closeButton.setOnAction(e -> historyStage.close());

        layout.getChildren().addAll(title, listView, closeButton);

        Scene scene = new Scene(layout, 800, 600);
        historyStage.setScene(scene);
        historyStage.setTitle("Historia zakładów");
        historyStage.show();
    }
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

        Button slotsMachineButton = new Button("Historia zakładów");

        Button historyButton = new Button("Historia zakładów");
        historyButton.setPrefWidth(250);
        historyButton.setPrefHeight(60);
        historyButton.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        historyButton.setLayoutX(213);
        historyButton.setLayoutY(490);
        historyButton.setOnAction(e -> showBetHistory());

        //przycisk z doladowaniem salda
        Button topUpButton = new Button("Doładuj konto +1000");
        topUpButton.setPrefWidth(250);
        topUpButton.setPrefHeight(60);
        topUpButton.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        topUpButton.setLayoutX(213);
        topUpButton.setLayoutY(490);


        // Funkcja dla przycisku Blackjack
        blackjackButton.setOnAction(e -> {
            try {
                BlackJackApp blackjackApp = new BlackJackApp(player);
                blackjackApp.start(new Stage()); // uruchom w nowym oknie
                primaryStage.hide(); //ukryj menu
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        //obsluga przycisku rulette
        ruletkaButton.setOnAction(e -> { 
            try{
            Ruletka ruletka = new Ruletka(player);
            ruletka.start(primaryStage);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });







        slotsMachineButton.setOnAction(e -> showBetHistory());
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

        Label balanceLabel = new Label("Saldo: " + player.getBalance());
        balanceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        balanceLabel.setLayoutX(1100);  // prawa strona
        balanceLabel.setLayoutY(20);    // górna część

        //obsluga przycisku z doladowaniem salda
        topUpButton.setOnAction(e -> {
            if (player != null) {
                player.setBalance(player.getBalance() + 1000);
                balanceLabel.setText("Saldo: " + player.getBalance());
                System.out.println("Saldo po doładowaniu: " + player.getBalance());
            }
        });


        root.getChildren().addAll(blackjackButton,ruletkaButton,topUpButton,slotsMachineButton,balanceLabel);


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

        topUpButton.setBackground(new Background(buttonBack));


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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (player != null) {
                UserDAO.updatePlayerBalance(player.getId(), player.getBalance());
            }
        }));
    }
    @Override
    public void stop() {
        if (player != null) {
            System.out.println("Aktualizuję bilans: " + player.getBalance());
            UserDAO.updatePlayerBalance(player.getId(), player.getBalance());
        } else {
            System.out.println("Brak gracza do aktualizacji bilansu.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
