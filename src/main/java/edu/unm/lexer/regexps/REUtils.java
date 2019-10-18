package edu.unm.lexer.regexps;

import edu.unm.lexer.fa.FAFactory;
import edu.unm.lexer.fa.FAUtils;
import edu.unm.lexer.fa.NFA;

import java.util.ArrayList;
import java.util.List;

public class REUtils {



    public static String preFixToInfix(RENode root) {
        //in order visit tree
        return inOrder(root);
    }

    private static String inOrder(RENode root) {
        if (root == null) {
            return "";
        }
        String left = inOrder(root.left);
        String right = inOrder(root.right);
        String rootKey = root.key;
        if (rootKey.equals("")) {
            rootKey = "_";
        } else if (rootKey.equals("_")) {
            rootKey = "'_";
        }
        if (rootKey.equals("|")) {
            return left + rootKey + right;
        } else if (rootKey.equals("+")) {
            String tmp = "";
            if (contains(left, '|')) {
                //add ()
                if (!left.equals("_")) {
                    tmp = "(" + left + ")";
                }
            } else {
                if (!left.equals("_")) {
                    tmp = left;
                }
            }
            if (contains(right, '|')) {
                if (!right.equals("_")) {
                    tmp = tmp + "(" + right + ")";
                }
            } else {
                if (!right.equals("_")) {
                    tmp = tmp + right;
                }
            }
            return tmp;
        }
        else if (rootKey.equals("*")) {
            if (left.length() == 1) {
                if (left.equals(" ")) {
                    return "( )*";
                }
                return left + "*";
            } else {
                return "(" + left + ")" + "*";
            }
        } else {
            return rootKey;
        }
    }
    private static boolean contains(String s, char c) {
        //check if there are any char c outside ()
        int leftPar = 0;
        int rightPar = 0;
        char[] inputArr = s.toCharArray();
        for (int i = 0; i < inputArr.length; i++) {
            char a = inputArr[i];
            if (a == '(') {
                leftPar++;
            } else if (a == ')') {
                rightPar++;
            } else if (a == c) {
                if (leftPar == rightPar) {
                    if(i == 0 || inputArr[i - 1] != '\'') {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static RE generateDigitsRE() {
        //non empty string of digits [0-9]+
        List<RE> digitREList = new ArrayList<>();

        for (Integer i = 0; i < 10; i++) {
            digitREList.add(REFactory.buildSingleCharRE(i.toString().charAt(0)));
        }

        RE result = alterAll(digitREList);
        result = REFactory.concatRE(result, REFactory.starRE(result));
        return result;
    }

    public static RE generateLetterRE() {
        //non empty string of digits [a-z, A-Z]+
        List<RE> letterREList = new ArrayList<>();

        for (Integer i = 0; i < 26; i++) {
            char c = 'a';
            letterREList.add(REFactory.buildSingleCharRE((char)(c + i)));
        }
        for (Integer i = 0; i < 26; i++) {
            char c = 'A';
            letterREList.add(REFactory.buildSingleCharRE((char)(c + i)));
        }
        RE result = alterAll(letterREList);
        result = REFactory.concatRE(result, REFactory.starRE(result));
        return result;
    }

    public static RE alterAll(List<RE> reList) {
        RE result = reList.get(0);
        for (int i = 1; i < reList.size(); i++) {
            result = REFactory.alterRE(result, reList.get(i));
        }
        return result;
    }

}
