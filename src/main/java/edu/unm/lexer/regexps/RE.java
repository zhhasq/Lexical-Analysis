package edu.unm.lexer.regexps;

import edu.unm.lexer.fa.FA;
import edu.unm.lexer.fa.NFA;

public class RE {
    final String expression;
    final NFA nfa;

    public RE(String name, NFA nfa) {
        this.expression = name;
        this.nfa = nfa;
    }
}
