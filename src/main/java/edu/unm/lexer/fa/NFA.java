package edu.unm.lexer.fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class NFA extends FA{

    public NFA(List<HashMap<Character, Integer>> data, Integer start, int[] end) {
        this.data = data;
        this.initState = start;
        for (int i : end) {
            this.acceptState.add(i);
        }
    }
}
