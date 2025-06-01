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

    private Deck deck;
    private Player player;
    private Player dealer;


    @FXML
    private void initialize() {
        startNewGame();
    }

    @FXML
    private void onHit() {
        player.addCard(deck.drawCard());
        updateUI(false);

        if (player.getScore() > 21) {
            dealerPlay(); // krupier dobiera
            updateUI(true); // pokaz wszystko
            resultLabel.setText("Przegrałeś – bust!");
        }
    }


    @FXML
    private void onStand() {
        dealerPlay();
        updateUI(true); // pokaz cala reke krupiera
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



    private void disableButtons() {
        hitButton.setDisable(true);
        standButton.setDisable(true);
    }




    private String checkWinner() {
        int playerScore = player.getScore();
        int dealerScore = dealer.getScore();

        if (playerScore > 21) return "Bust! Dealer wins.";
        if (dealerScore > 21) return "Dealer busts! You win!";
        if (playerScore > dealerScore) return "You win!";
        if (dealerScore > playerScore) return "Dealer wins!";
        return "Draw!";
    }


    private void updateUI(boolean showDealerAll) {
        playerLabel.setText("Gracz: " + player.getHand() + " (" + player.getScore() + ")");
        if (showDealerAll) {
            dealerLabel.setText("Krupier: " + dealer.getHand() + " (" + dealer.getScore() + ")");
        } else {
            dealerLabel.setText("Krupier: [" + dealer.getHand().get(0) + ", ???]");
        }
    }

}
