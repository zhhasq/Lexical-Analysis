package edu.unm.lexer.description;

import java.util.List;

public class Token {
    //key, value pair
    //key is TokenClass,
    //value is the value;
    TokenClass tokenClass;
    String value;

    public Token(TokenClass tokenClass, String value) {
        this.tokenClass = tokenClass;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + this.tokenClass.tokenClassName + ": " + value + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Token)) {
            return false;
        }
        return (this.tokenClass == ((Token) obj).tokenClass && this.value.equals(((Token) obj).value)) ? true : false;
    }
}
