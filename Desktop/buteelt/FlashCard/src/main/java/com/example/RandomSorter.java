package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomSorter implements CardOrganizer {
    @Override
    public List<Card> organize(List<Card> cards) {
        List<Card> result = new ArrayList<>(cards);
        Collections.shuffle(result);
        return result;
    }
}