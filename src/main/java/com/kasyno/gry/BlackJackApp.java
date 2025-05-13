package com.kasyno.gry;

import com.kasyno.menu.MainMenu;
import javafx.stage.WindowEvent;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackJackApp extends Application {

    private final List<Integer> playerCards = new ArrayList<>();
    private final List<Integer> dealerCards = new ArrayList<>();
    private final Random random = new Random();

    private Label playerLabel = new Label("Twoje karty: ");
    private Label dealerLabel = new Label("Karty krupiera: ");
    private Label resultLabel = new Label("");

    @Override
    public void start(Stage primaryStage) {
        Button hitButton = new Button("Dobierz kartę");
        Button standButton = new Button("Pas");

        Button backButton = new Button("Powrót do menu");

        backButton.setOnAction(event -> {
            // Zamykamy aktualne okno
            primaryStage.close();

            // Tworzymy nowe okno i uruchamiamy menu
            Stage menuStage = new Stage();
            try {
                new MainMenu().start(menuStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        hitButton.setOnAction(e -> {
            playerCards.add(drawCard());
            updateLabels();
            if (getSum(playerCards) > 21) {
                resultLabel.setText("Przegrałeś! Przekroczyłeś 21.");
                disableButtons(hitButton, standButton);
            }
        });

        standButton.setOnAction(e -> {
            while (getSum(dealerCards) < 17) {
                dealerCards.add(drawCard());
            }
            updateLabels();
            showResult();
            disableButtons(hitButton, standButton);
        });

        startNewGame();

        VBox root = new VBox(10,
                playerLabel,
                dealerLabel,
                new HBox(10, hitButton, standButton),
                resultLabel,
                backButton
        );
        root.setStyle("-fx-padding: 20; -fx-font-size: 16px;");

        primaryStage.setTitle("BlackJack – JavaFX");
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.show();

        
        

        
        


    }

    private void startNewGame() {
        playerCards.clear();
        dealerCards.clear();
        playerCards.add(drawCard());
        playerCards.add(drawCard());
        dealerCards.add(drawCard());
        updateLabels();
        resultLabel.setText("");
    }

    private void updateLabels() {
        playerLabel.setText("Twoje karty: " + playerCards + " (suma: " + getSum(playerCards) + ")");
        dealerLabel.setText("Karty krupiera: " + dealerCards + " (suma: " + getSum(dealerCards) + ")");
    }

    private void showResult() {
        int playerSum = getSum(playerCards);
        int dealerSum = getSum(dealerCards);
        if (dealerSum > 21 || playerSum > dealerSum) {
            resultLabel.setText("Wygrałeś!");
        } else if (playerSum == dealerSum) {
            resultLabel.setText("Remis.");
        } else {
            resultLabel.setText("Przegrałeś.");
        }
    }

    private void disableButtons(Button... buttons) {
        for (Button b : buttons) b.setDisable(true);
    }

    private int drawCard() {
        return random.nextInt(10) + 2; // zwraca wartość 2–11
    }

    private int getSum(List<Integer> cards) {
        return cards.stream().mapToInt(Integer::intValue).sum();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
