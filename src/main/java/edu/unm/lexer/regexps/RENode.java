package edu.unm.lexer.regexps;

public class RENode {
    public String key; //also want to represent '* for a single node
    public RENode left;
    public RENode right;
    public RENode(String c) {
        this.key = c;
    }
}