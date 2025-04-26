package com.example;

import java.util.ArrayList;
import java.util.List;

public class RecentMistakesFirstSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        List<Card> mistakes = new ArrayList<>();
        List<Card> others = new ArrayList<>();

        for (Card card : cards) {
            if (!card.wasLastAttemptCorrect()) {
                mistakes.add(card);
            } else {
                others.add(card);
            }
        }

        List<Card> result = new ArrayList<>(mistakes);
        result.addAll(others);
        return result;
    }
}