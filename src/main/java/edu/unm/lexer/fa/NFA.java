package edu.unm.lexer.fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class NFA extends FA{

    public NFA(List<State> data, State start, State end) {
        this.data = data;
        this.initState = start;
        this.acceptState = end;
    }

    @Override
    public State mov(State curState, String c) {
        return null;
    }
}
