package edu.unm.lexer.fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFA extends FA {

    public DFA(List<State> data, State start, State end, Set<String> alphabet) {
        this.data = data;
        this.initState = start;
        this.acceptState = end;
        acceptStates.add(acceptState);
        this.alphabet = alphabet;
    }

    public DFA(List<State> data, State start, Set<State> end, Set<String> alphabet) {
        this.data = data;
        this.initState = start;
        this.acceptStates = end;
        this.alphabet = alphabet;
    }

    @Override
    public boolean isAccpeted(State state) {
       return acceptStates.contains(state);
    }
    public Set<String> getAlphabet() {
        return this.alphabet;
    }
    public Set<State> getAcceptStates() {
        return this.acceptStates;
    }
    public State mov(State curState, String s) {
        //given current state, and next char
        //output the next state or null if no path for c
        if (s.length() != 1) {
            System.out.println("for DFA move condition has to be single char");
            return null;
        }
        List<State> next = curState.getNext(s);
        if (next == null) {
            return null;
        }
        if (next.size() > 1) {
            System.out.println("for DFA, there can have at most one state for a condition at one state");
            return null;
        }
        return next.get(0);
    }


}
