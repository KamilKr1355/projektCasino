package com.kasyno.gry;

import com.kasyno.menu.MainMenu;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackJackController {

    @FXML
    private Label playerLabel;

    @FXML
    private Label dealerLabel;

    @FXML
    private Label resultLabel;

    @FXML
    private Button hitButton;

    @FXML
    private Button standButton;

    @FXML
    private Button backButton;

    @FXML
    private Button newGameButton;

    @FXML
    private Label balanceLabel;

    @FXML
    private TextField betField;

    @FXML
    private Button betButton;

    private Deck deck;
    private Player player;
    private Player dealer;
    private int playerBalance = 1000;
    private int currentBet = 0;



    @FXML
    private void initialize() {
        hitButton.setDisable(true);
        standButton.setDisable(true);
        resultLabel.setText("Kliknij 'Nowa gra' aby rozpocząć.");
//        startNewGame();
        betButton.setDisable(false);
        updateBalance();

    }



    @FXML
    private void onHit() {
        player.addCard(deck.drawCard());
        updateUI(false);

        if (player.getScore() > 21) {
            dealerPlay(); // krupier dobiera
            updateUI(true); // pokaz wszystko
            resultLabel.setText("Przegrałeś – bust!");
            disableButtons();
        }
    }


    @FXML
    private void onStand() {
        dealerPlay();
        updateUI(true); // pokaz cala reke krupiera
        disableButtons();
        resultLabel.setText(checkWinner());
    }

    private void dealerPlay() {
        while (dealer.getScore() < 17) {
            dealer.addCard(deck.drawCard());
        }
    }


    @FXML
    private void onBack() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        currentStage.close();

        Stage menuStage = new Stage();
        try {
            new MainMenu().start(menuStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void startNewGame() {
        betButton.setDisable(false);
        betField.setDisable(false);
        betField.clear();
        currentBet = 0;

        resultLabel.setText("");
        hitButton.setDisable(false);
        standButton.setDisable(false);
        deck = new Deck(); // tworzy 6 talii
        player = new Player();
        dealer = new Player();

        // Rozdaj po 2 karty
        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());

        updateUI(false); // false = nie pokazuj wszystkich kart krupiera
    }

    private void updateBalance() {
        balanceLabel.setText("Saldo: " + playerBalance);
    }


    private void disableButtons() {
        hitButton.setDisable(true);
        standButton.setDisable(true);
    }




    private String checkWinner() {
        int playerScore = player.getScore();
        int dealerScore = dealer.getScore();

        if (playerScore > 21) {
            return "Bust! Przegrałeś zakład " + currentBet + "!";
        }
        if (dealerScore > 21 || playerScore > dealerScore) {
            int winAmount = currentBet * 2;
            playerBalance += winAmount;
            updateBalance();
            return "Wygrałeś " + winAmount + "!";
        }
        if (dealerScore == playerScore) {
            playerBalance += currentBet; // zwrot stawki
            updateBalance();
            return "Remis – zakład zwrócony.";
        }

        return "Przegrałeś zakład " + currentBet + "!";
    }


    private void updateUI(boolean showDealerAll) {
        playerLabel.setText("Gracz: " + player.getHand() + " (" + player.getScore() + ")");
        if (showDealerAll) {
            dealerLabel.setText("Krupier: " + dealer.getHand() + " (" + dealer.getScore() + ")");
        } else {
            dealerLabel.setText("Krupier: [" + dealer.getHand().get(0) + ", ???]");
        }
    }

    @FXML
    private void onPlaceBet() {
        try {
            int bet = Integer.parseInt(betField.getText());

            if (bet <= 0) {
                resultLabel.setText("Zakład musi być większy niż 0!");
                return;
            }

            if (bet > playerBalance) {
                resultLabel.setText("Nie masz wystarczająco środków!");
                return;
            }

            currentBet = bet;
            playerBalance -= bet;
            updateBalance();
            resultLabel.setText("Zakład przyjęty. Kliknij New Game!");
            betButton.setDisable(true);
            betField.setDisable(true);

        } catch (NumberFormatException e) {
            resultLabel.setText("Nieprawidłowy zakład.");
        }
    }

}
