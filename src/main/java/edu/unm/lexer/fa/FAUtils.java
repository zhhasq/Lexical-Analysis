package edu.unm.lexer.fa;

public class FAUtils {
    //for question 1
    //for question 2
    //for question 3
    //for question 4

    //for question 5
    public static boolean testDFA(DFA dfa, String input) {
        Integer curState = 0;
        char[] inputArr = input.toCharArray();
        for (char c : inputArr) {
            curState = dfa.mov(curState, c);
            if (curState == null) {
                //no where to go
                return false;
            }
        }
        return dfa.isAccpeted(curState);
    }

    //for question 6
    //for question 7
    //for question 8
    //for question 9
    //for question 10
    //for question 11
    //for question 12
    //for question 13
}
