package edu.unm.lexer.fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public abstract class FA {
    List<HashMap<Character, Integer>> data;
    Integer initState;
    HashSet<Integer> acceptState;

    public Integer mov(Integer curState, Character c) {
        //given current state, and next char
        //output the next state or null if no path for c
        HashMap<Character, Integer> transition = data.get(curState);
        return transition.get(c);
    }

    public boolean isAccpeted(Integer state) {
        return this.acceptState.contains(state);
    }
}
