package edu.unm.lexer.regexps;

import edu.unm.lexer.fa.FAFactory;
import edu.unm.lexer.fa.NFA;

public class REUtils {

    public static RE concat(RE r, RE t) {
        String newName = r.expression + t.expression;
        NFA newNFA = FAFactory.concatNFA(r.nfa, t.nfa);
        return new RE(newName, newNFA);
    }

//    public static RE star(RE r) {
//        String newName = "(" + r.expression + ")" + "*";
//        NFA newNFA = FAFactory.makeNFAStar(r.nfa);
//        return new RE(newName, newNFA);
//    }
//
//    public static RE alter(RE r, RE t) {
//        String newName = r.expression + "|" + t.expression;
//        NFA newNFA = FAFactory.alterNFA(r.nfa, t.nfa);
//        return new RE(newName, newNFA);
//    }
}
