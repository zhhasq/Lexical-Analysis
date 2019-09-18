package edu.unm.lexer.regexps;

import edu.unm.lexer.fa.FAFactory;
import edu.unm.lexer.fa.NFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class REFactory {
    public static RE buildSingleCharRE(char c) {
        return new RE(new String(new char[]{c}), FAFactory.buildSingleCharNFA(c));
    }
}
