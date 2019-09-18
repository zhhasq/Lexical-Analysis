package edu.unm.lexer.regexps;

import edu.unm.lexer.fa.NFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SingleCharRE extends RE{

    public SingleCharRE(String name, NFA nfa) {
        super(name, nfa);
    }
}
