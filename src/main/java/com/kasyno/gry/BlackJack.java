package com.kasyno.gry;

import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



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
        Scanner scanner = new Scanner(System.in);
        Deck deck = new Deck();
        Player user = new Player();
        Player dealer = new Player();

        // Initial cards
        user.addCard(deck.drawCard());
        user.addCard(deck.drawCard());

        dealer.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());

        // Show hands
        System.out.println("Your cards: " + user.getHand() + " | Score: " + user.getScore());
        System.out.print("Dealer's cards: ");
        dealer.showHand(false);

        // User turn
        while (true) {
            System.out.print("Hit or Stand? (h/s): ");
            String action = scanner.nextLine();

            if (action.equalsIgnoreCase("h")) {
                Card newCard = deck.drawCard();
                user.addCard(newCard);
                System.out.println("You drew: " + newCard);
                System.out.println("Your score: " + user.getScore());

                if (user.getScore() > 21) {
                    System.out.println("You busted! Dealer wins.");
                    return;
                }
            } else {
                break;
            }
        }

        // Dealer turn
        System.out.println("\nDealer's turn:");
        dealer.showHand(true);
        while (dealer.getScore() < 17) {
            Card card = deck.drawCard();
            dealer.addCard(card);
            System.out.println("Dealer draws: " + card);
        }

        System.out.println("Dealer's score: " + dealer.getScore());

        // Determine winner
        int userScore = user.getScore();
        int dealerScore = dealer.getScore();

        if (dealerScore > 21 || userScore > dealerScore) {
            System.out.println("You win!");
        } else if (dealerScore > userScore) {
            System.out.println("Dealer wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }
}
