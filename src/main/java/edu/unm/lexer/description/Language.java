package edu.unm.lexer.description;

import java.util.ArrayList;
import java.util.List;

public class Language {
    List<TokenClass> tokenClassList = new ArrayList<>();
    String languageName;
    public void addTokenClass(String name, TokenClass tokenClass) {
        this.languageName = name;
        this.tokenClassList.add(tokenClass);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("language: " + languageName);
        sb.append(System.lineSeparator());
        for (TokenClass tokenClass : tokenClassList) {
            sb.append(tokenClass.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
