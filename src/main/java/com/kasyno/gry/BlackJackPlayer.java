package com.kasyno.gry;

import com.kasyno.player.Player;

import java.util.*;

public class BlackJackPlayer extends Player {
    private List<Card> hand;

    public BlackJackPlayer(String id) {
        super(id);
        hand = new ArrayList<>();
    }


    public BlackJackPlayer(Player basePlayer) {
        super(basePlayer.getId());
        this.setBalance(basePlayer.getBalance());
        this.hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void removeCards() {
        hand.clear();
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


}
