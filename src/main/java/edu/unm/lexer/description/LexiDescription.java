package edu.unm.lexer.description;

import edu.unm.lexer.regexps.*;

import java.util.*;

public class LexiDescription extends Description{
    TokenClass identifier;
    TokenClass symbols;
    TokenClass operation;

    public Set<String> alphabet;


    public LexiDescription() {
        List<String> keywordsList = Arrays.asList(new String[]{"language", "class", "end", "alphabet", "relevant", "irrelevant", "discard", "is"});
        this.keyWords = new TokenClass(TokenUtils.keyWords, createKeywordsRE(keywordsList));
        this.identifier = new TokenClass(TokenUtils.identifier, createIdentifierRE());
        this.symbols = new TokenClass(TokenUtils.symbols, createSymbolRE());
        this.operation = new TokenClass(TokenUtils.operation, createOperationRE());

        tokenClasses.add(keyWords);
        tokenClasses.add(symbols);
        tokenClasses.add(identifier);
        tokenClasses.add(operation);

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

    private RE createIdentifierRE() {
        //non-empty strings of letters and digits
        RE result = REFactory.alterRE(REUtils.generateDigitsRE(), REUtils.generateLetterRE());
        result = REFactory.concatRE(result, REFactory.starRE(result));
        return result;
    }

    private static RE createOperationRE() {
        //'* '| '+
        List<RE> operationREList = new ArrayList<>();
        operationREList.add(new RE("*", new RENode("'*")));
        operationREList.add(new RE("+", new RENode("'+")));
        operationREList.add(new RE("|", new RENode("'|")));
        return REUtils.alterAll(operationREList);
    }

    public Language parse(List<Token> tokenList) {
        Language language = new Language();
        String languageName = "";

        Set<String> alphabet = new HashSet<>();
        Token alphaToken = this.keywordsTokenMap.get("alphabet");
        Token endToken = this.keywordsTokenMap.get("end");
        Token classToken = this.keywordsTokenMap.get("class");
        Token isToken = this.keywordsTokenMap.get("is");
        Token relevantToken = this.keywordsTokenMap.get("relevant");
        Token irrelevantToken = this.keywordsTokenMap.get("irrelevant");
        Token discardToken = this.keywordsTokenMap.get("discard");
        Token languageToken = this.keywordsTokenMap.get("language");

        for (int i = 0; i < tokenList.size(); i++) {
            if (tokenList.get(i).equals(languageToken)) {
                i++;
                languageName = tokenList.get(i).value;
            }
            if (tokenList.get(i).equals(alphaToken)) {
                i++;
                while (i < tokenList.size() && !tokenList.get(i).equals(endToken)) {
                    alphabet.add(tokenList.get(i).value);
                    i++;
                }
                this.alphabet = alphabet;
                //don't need i--, can pass end by for loop
            } else if (tokenList.get(i).equals(classToken)) {
                i++; //skip class_keyword
                StringBuilder expression = new StringBuilder();
                String className = "";
                int relevant = 0;
                while (i < tokenList.size() && !tokenList.get(i).equals(endToken)) {
                    if (tokenList.get(i).tokenClass.equals(identifier)) {
                        className = tokenList.get(i).value;
                    } else if(tokenList.get(i).tokenClass.equals(symbols)) {
                        String symbol = tokenList.get(i).value;
                        if (!alphabet.contains(symbol)) {
                            System.out.println("parsing error " + symbol + " is not in the alphabet");
                            return null;
                        }
                        if (symbol.equals("''_")) {
                            expression.append("'_");
                        } else if (symbol.charAt(1) == '|' || symbol.charAt(1) == '\'' || symbol.charAt(1) == '+' || symbol.charAt(1) == '*') {
                            expression.append(symbol); //keep '
                        } else {
                            expression.append(symbol.substring(1, symbol.length())); //remove '
                        }
                        //expression.append(symbol);
                    } else if (tokenList.get(i).tokenClass.equals(operation)) {
                        expression.append(tokenList.get(i).value);
                    } else if (tokenList.get(i).equals(relevantToken)) {
                        relevant = 1;
                    } else if (tokenList.get(i).equals(irrelevantToken)) {
                        relevant = 0;
                    } else if (tokenList.get(i).equals(discardToken)) {
                        relevant = -1;
                    }
                    i++;
                }
                RE re = REParser.parsePreFix(expression.toString());
                language.addTokenClass(languageName, new TokenClass(className, re, relevant));
            }
        }
        return language;
    }

}
