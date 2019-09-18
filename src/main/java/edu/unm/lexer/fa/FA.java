package edu.unm.lexer.fa;

import java.util.HashMap;
import java.util.HashSet;

public abstract class FA {
    HashMap<Character, Integer>[] data;
    Integer initState;
    HashSet<Integer> acceptState;

    public abstract Integer mov(Integer curState, Character c);
    public abstract boolean isAccpeted(Integer state);
}
