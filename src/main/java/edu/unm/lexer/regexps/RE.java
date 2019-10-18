package edu.unm.lexer.regexps;

import edu.unm.lexer.fa.*;

public class RE {
    final String preFixExpression;
    String inFixExpression;
    NFA nfa;
    DFA miniDFA;

    RENode root;

    public RE(String name, RENode root) {
        this.preFixExpression = name;
        this.root = root;
    }

    public String getPreFixExpression() {
        return this.preFixExpression;
    }
    public String getInFixExpression() {
        if (inFixExpression == null) {
            this.inFixExpression = REUtils.preFixToInfix(root);
        }
        return inFixExpression;
    }

    public NFA getNFA() {
        if (this.nfa == null) {
            nfa = FAUtils.REToNFA(this);
        }
        return this.nfa;
    }
    public RENode getRoot() {
        return this.root;
    }
    public void setMiniDFA(DFA miniDFA) {
        this.miniDFA = miniDFA;
    }
    public boolean accepts(String s) {
        if (this.miniDFA == null) {
            this.miniDFA = FAUtils.miniDFA(FAUtils.NFAToDFA(FAUtils.REToNFA(this)));
        }
        return FAUtils.testDFA(this.miniDFA, s);
    }

    @Override
    public String toString() {
        //BTreePrinter.printNode(root);
        return  "pre-fix:" + this.preFixExpression + System.lineSeparator()
                + " in-fix: " + REUtils.preFixToInfix(root);
    }
}

