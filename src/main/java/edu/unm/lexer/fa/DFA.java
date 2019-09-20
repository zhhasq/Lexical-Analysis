package edu.unm.lexer.fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DFA extends FA {

    public DFA(List<State> data, State start, State end) {
        this.data = data;
        this.initState = start;
        this.acceptState = end;
    }
    public State mov(State curState, String s) {
        //given current state, and next char
        //output the next state or null if no path for c
        if (s.length() != 1) {
            System.out.println("for DFA move condition has to be single char");
            return null;
        }
        List<State> next = curState.getNext(s);
        if (next.size() > 1) {
            System.out.println("for DFA, there can have at most one state for a condition at one state");
            return null;
        }
        return next.get(0);
    }
}
