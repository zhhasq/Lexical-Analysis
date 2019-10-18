package edu.unm.lexer.description;

import edu.unm.lexer.regexps.RE;
import edu.unm.lexer.regexps.REParser;
import edu.unm.lexer.regexps.REUtils;

import java.util.*;

public abstract class Description {

    List<TokenClass> tokenClasses = new ArrayList<>();
    TokenClass keyWords;
    Map<String, Token> keywordsTokenMap = new HashMap<>();
    List<Token> tokenList;

    public Token toToken(String s) {
        for (TokenClass tokenClass : tokenClasses) {
            if (tokenClass.rexp.accepts(s)) {
                return new Token(tokenClass, s);
            }
        }
        System.out.println("error, " + s + " cannot be tokenize");
        return null;
    }

    public List<Token> scan(List<String> inputList) {
        //convert List<String> ---> List<Token>
        List<Token> tokenList = new ArrayList<>();
        for (String s : inputList) {
            if (!s.isEmpty()) {
                Token tmp = toToken(s);
                if (tmp == null) {
                    return null;
                } else {
                    tokenList.add(tmp);
                }
            }
        }
        this.tokenList = tokenList;
        return tokenList;
    }

    public RE createKeywordsRE(List<String> keywordsList) {

        List<RE> keywordsREList = new ArrayList<>();
        for (String s : keywordsList) {
            keywordsREList.add(REParser.parse(s));

        }
        RE result = REUtils.alterAll(keywordsREList);
        return REUtils.alterAll(keywordsREList);
    }
}
