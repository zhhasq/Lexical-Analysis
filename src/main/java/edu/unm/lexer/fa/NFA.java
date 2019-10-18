package edu.unm.lexer.fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NFA extends FA{
    public NFA(List<State> data, State start, State end) {
        this.data = data;
        this.initState = start;
        this.acceptState = end;
        this.acceptStates.add(end);
    }

    public NFA(List<State> data, State start, State end, Set<String> alphabet) {
        this.data = data;
        this.initState = start;
        this.acceptState = end;
        this.alphabet = alphabet;
        this.acceptStates.add(end);
    }
    public NFA(List<State> data, State start, Set<State> acceptStates, Set<String> alphabet) {
        this.data = data;
        this.initState = start;
        this.alphabet = alphabet;
        this.acceptStates = acceptStates;
    }
    public NFA(List<State> data, State start, State end, Set<String> alphabet, Set<State> initStates) {
        this.data = data;
        this.initState = start;
        this.acceptState = end;
        this.alphabet = alphabet;
        this.acceptStates.add(end);
        this.initStates = initStates;
    }

    @Override
    public State mov(State curState, String c) {
        return null;
    }

}
