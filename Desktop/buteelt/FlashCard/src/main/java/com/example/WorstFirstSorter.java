package com.example;

import java.util.ArrayList;
import java.util.List;

public class WorstFirstSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        List<Card> result = new ArrayList<>(cards);
        result.sort((c1, c2) -> {
            double rate1 = c1.getTotalAttempts() == 0 ? 1.0 : 
                (double)c1.getCorrectCount() / c1.getTotalAttempts();
            double rate2 = c2.getTotalAttempts() == 0 ? 1.0 : 
                (double)c2.getCorrectCount() / c2.getTotalAttempts();
            return Double.compare(rate1, rate2);
        });
        return result;
    }
}
