package com.kasyno.gry;

import com.kasyno.menu.MainMenu;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.kasyno.player.Player;

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

    @FXML
    private Label winAmountLabel;

    @FXML
    private Button playButton;


    private Deck deck;
    private BlackJackPlayer player;
    private BlackJackPlayer dealer;
    private double playerBalance = 1000;
    private int currentBet = 0;
    private Player loggedInPlayer;


    public void setPlayer(Player player) {
        this.loggedInPlayer = player;
        this.playerBalance = player.getBalance();
    }

    @FXML
    private void initialize() {
        hitButton.setDisable(true);
        standButton.setDisable(true);
        resultLabel.setText("Kliknij 'Nowa gra' aby rozpocząć.");
        betButton.setDisable(false);
    }


    // nowa metoda wywoływana po setPlayer()
    public void initGame() {
        this.player = new BlackJackPlayer(loggedInPlayer);
        this.dealer = new BlackJackPlayer("Dealer");
        this.deck = new Deck();
        updateBalance();
    }



    @FXML
    private void onHit() {
        player.addCard(deck.drawCard());
        updateUI(false);
        if (player.getScore() > 21) {
            disableButtons();
            resultLabel.setText(checkWinner());
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
        Image logo = new Image("Logo_Kasyna.jpg");
        Stage menuStage = new Stage();
        try {
            new MainMenu().start(menuStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void startNewGame() {
        currentBet = 0;
        playButton.setDisable(false);
        betButton.setDisable(false);

        player.removeCards();
        dealer.removeCards();
        updateUI(false); // false = nie pokazuj wszystkich kart krupiera


    }
//    Rozdawanie kart
    @FXML
    private void onPlay() {
        if (currentBet <= 0) {
            resultLabel.setText("Najpierw postaw zakład!");
            return;
        }
        playButton.setDisable(true);
        resultLabel.setText("");
        betButton.setDisable(false);
        betField.setDisable(false);
        hitButton.setDisable(false);
        standButton.setDisable(false);
        betButton.setDisable(true);


        // Rozdaj po 2 karty
        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());

        updateUI(false); // false = nie pokazuj wszystkich kart krupiera
    }

    private void updateBalance() {
        balanceLabel.setText("Saldo: " + playerBalance);
        player.setBalance(playerBalance);
        loggedInPlayer.setBalance(playerBalance);
    }


    private void disableButtons() {
        hitButton.setDisable(true);
        standButton.setDisable(true);
    }




    private String checkWinner() {
        int playerScore = player.getScore();
        int dealerScore = dealer.getScore();

        newGameButton.setDisable(false);
        playButton.setDisable(true);
        betButton.setDisable(true);
        updateUI(true);
        updateBalance();

        if (playerScore > 21) {
            updateBalance();
            return "Bust! Przegrałeś zakład " + currentBet + "!";
        }
        if (dealerScore > 21 || playerScore > dealerScore) {
            int winAmount = currentBet * 2;
            playerBalance += winAmount;
            showWinAmount("+ " + winAmount); //
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
        if(dealer.getHand().isEmpty()) {
            dealerLabel.setText("Krupier: [???]");
        }
        else{
            if (showDealerAll) {
                dealerLabel.setText("Krupier: " + dealer.getHand() + " (" + dealer.getScore() + ")");
            } else {
                dealerLabel.setText("Krupier: [" + dealer.getHand().get(0) + ", ???]");
            }
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
            resultLabel.setText("Zakład przyjęty. Kliknij Rozdaj karty!");
            betButton.setDisable(true);
            betField.setDisable(true);
            newGameButton.setDisable(true);



        } catch (NumberFormatException e) {
            resultLabel.setText("Nieprawidłowy zakład.");
        }
    }

    private void showWinAmount(String amount) {
        winAmountLabel.setText(amount);
        winAmountLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        // Zniknięcie po 2 sekundach
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> winAmountLabel.setText(""));
        }).start();
    }


}
