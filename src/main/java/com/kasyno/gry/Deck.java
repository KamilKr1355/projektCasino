package com.kasyno.gry;

import java.util.*;

public class Deck {
    private List<Card> cards;
    private static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    private static final int[] VALUES =   {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};

    public Deck() {
        cards = new ArrayList<>();
        for (int i = 0; i < RANKS.length; i++) {
            for (String suit : SUITS) {
                cards.add(new Card(suit, RANKS[i], VALUES[i]));
            }
        }
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return cards.remove(0);
    }
}
