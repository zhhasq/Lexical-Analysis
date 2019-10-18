package edu.unm.lexer.description;

import edu.unm.lexer.regexps.RE;

public class TokenClass {
    final String tokenClassName;
    final int relevant;
    final RE rexp; //-1 discard, irrelevant 0, relevant 1
    public TokenClass(String name, RE re, int relevant) {
        this.tokenClassName = name;
        this.rexp = re;
        this.relevant = relevant;
    }
    public TokenClass(String name, RE re) {
        this.tokenClassName = name;
        this.rexp = re;
        relevant = 3;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Class: ");
        sb.append(System.lineSeparator());
        sb.append( "    " + this.tokenClassName);
        sb.append(System.lineSeparator());
        sb.append("    expression: " );
        sb.append(System.lineSeparator());
        sb.append("        preFix: " + rexp.getPreFixExpression());
        sb.append(System.lineSeparator());
        sb.append("        inFix: " + rexp.getInFixExpression());

        sb.append(System.lineSeparator());
        if (relevant == -1) {
            sb.append("    discard");
        } else if (relevant == 0) {
            sb.append("    irrelevant");
        } else if (relevant == 1) {
            sb.append("    relevant");
        }
        return sb.toString();
    }
}
