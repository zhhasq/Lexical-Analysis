package edu.unm.lexer.description;

import edu.unm.lexer.regexps.RE;
import edu.unm.lexer.regexps.REFactory;
import edu.unm.lexer.regexps.RENode;
import edu.unm.lexer.regexps.REUtils;

import java.util.*;

public class AlphabetDescription extends Description{
    TokenClass symbols;

    Map<String, Token> symbolMap = new HashMap<>();

    public AlphabetDescription() {
        List<String> keywordsList = Arrays.asList(new String[]{"alphabet", "end"});
        this.keyWords = new TokenClass(TokenUtils.keyWords, createKeywordsRE(keywordsList));
        this.symbols = new TokenClass(TokenUtils.symbols, createSymbolRE());
        //the order matters, keywords has high priority than symbols.
        tokenClasses.add(keyWords);
        tokenClasses.add(symbols);
        for (String s : keywordsList) {
            Token tmp = new Token(this.keyWords, s);
            this.keywordsTokenMap.put(s, tmp);
        }
    }

    public RE createSymbolRE() {
        //'_  empty
        //''_ underscore
        //result 'ascii | ''_
        RE printableAscii = REFactory.generateASCiiRE();
        RE underScore = REFactory.concatRE(new RE("''", new RENode("''")), new RE("'_", new RENode("_")));
        RE result = REFactory.concatRE(new RE("''", new RENode("''")),  REFactory.alterRE(underScore, printableAscii));
        return result;
    }


    @Override
    public Token toToken(String s) {
        for (TokenClass tokenClass : tokenClasses) {
            if (tokenClass.rexp.accepts(s)) {
                if (tokenClass.tokenClassName.equals(TokenUtils.symbols)) {
                    Token symbolToken = new Token(tokenClass, s);
                    symbolMap.put(s, symbolToken);
                    return symbolToken;
                }
                return new Token(tokenClass, s);
            }
        }
        System.out.println("error," + s +  " cannot be tokenize");
        return null;
    }

    public Set<String> parse(List<Token> tokenList) {
        Set<String> alphabet = new HashSet<>();
        Token alphaToken = this.keywordsTokenMap.get("alphabet");
        Token endToken = this.keywordsTokenMap.get("end");

        for (int i = 0; i < tokenList.size(); i++) {
            if (tokenList.get(i).equals(alphaToken)) {
                i++;
                while (i < tokenList.size() && !tokenList.get(i).equals(endToken)) {
                    alphabet.add(tokenList.get(i).value);
                    i++;
                }
            }
        }
        return alphabet;
    }
}
