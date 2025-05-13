package com.kasyno.gry;

import java.util.*;

public class Player {
    private List<Card> hand;

    public Player() {
        hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public int getScore() {
        int score = 0;
        int aces = 0;
        for (Card card : hand) {
            score += card.getValue();
            if (card.isAce()) {
                aces++;
            }
        }

        // Aces can be 1 if score > 21
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void showHand(boolean showAll) {
        if (!showAll) {
            System.out.println("[" + hand.get(0) + ", Hidden]");
        } else {
            System.out.println(hand);
        }
    }
}
