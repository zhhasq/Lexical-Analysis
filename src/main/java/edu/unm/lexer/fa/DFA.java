package edu.unm.lexer.fa;

import java.util.HashMap;
import java.util.HashSet;

public class DFA extends FA {

    public DFA(HashMap<Character, Integer>[] data, Integer start, HashSet<Integer> end) {
        this.data = data;
        this.acceptState = end;
        this.initState = start;
    }

    @Override
    public Integer mov(Integer curState, Character c) {
        //given current state, and next char
        //output the next state or null if no path for c
        HashMap<Character, Integer> transition = data[curState];
        return transition.get(c);
    }

    @Override
    public boolean isAccpeted(Integer state) {
        return this.acceptState.contains(state);
    }

}
