package edu.unm.lexer.description;


import edu.unm.lexer.regexps.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class REDescription extends Description {
    TokenClass operation;
    AlphabetDescription alphabetDescription;

    public REDescription(AlphabetDescription alphabetDescription) {
        this.operation = new TokenClass(TokenUtils.operation, createOperationRE());
        tokenClasses.add(operation);
        this.alphabetDescription = alphabetDescription;
    }

    public static RE createOperationRE() {
        //'* '| '+
        List<RE> operationREList = new ArrayList<>();
        operationREList.add(new RE("*", new RENode("'*")));
        operationREList.add(new RE("+", new RENode("'+")));
        operationREList.add(new RE("|", new RENode("'|")));
        return REUtils.alterAll(operationREList);
    }

    @Override
    public List<Token> scan(List<String> inputList) {
        List<Token> result = new ArrayList<>();

        for (String string : inputList) {
            if (operation.rexp.accepts(string)) {
                result.add(new Token(operation, string));
            } else {
                if (alphabetDescription.symbolMap.get(string) == null) {
                    System.out.println("Error " + string + " is not in the alphabet");
                    return null;
                } else {
                    result.add(alphabetDescription.symbolMap.get(string));
                }
            }
        }
        return result;
    }

    public RE parse(List<Token> tokenList) {
        StringBuilder expression = new StringBuilder();
        for (Token token : tokenList) {
            if (token.tokenClass == operation) {
                expression.append(token.value);
            } else {
                String s = token.value;
                if (s.equals("''_")) {
                    expression.append("'_");
                } else if (s.charAt(1) == '|' || s.charAt(1) == '\'' || s.charAt(1) == '+' || s.charAt(1) == '*') {
                    expression.append(s); //keep '
                } else {
                    expression.append(s.substring(1, s.length())); //remove '
                }
            }
        }
        return REParser.parsePreFix(expression.toString());
    }
}
