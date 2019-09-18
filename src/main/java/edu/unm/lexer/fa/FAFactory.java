package edu.unm.lexer.fa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAFactory {

    public static NFA buildSingleCharNFA(char c) {
        //0(start) -- c --> 1(final)
        List<HashMap<Character, Integer>> data = new ArrayList<>();
        HashMap<Character, Integer> transition = new HashMap<>();
        transition.put(c, 1);
        data.add(transition);
        return new NFA(data, 0, new int[]{1});
    }

    public static NFA concatNFA(NFA nfa1, NFA nfa2) {
        //todo
        // nfa1--->nfa2
    }

    public static NFA makeNFAStar(NFA nfa) {
        //todo
    }

    public static NFA alterNFA(NFA nfa1, NFA nfa2) {
        //todo
    }
    
}
