package edu.unm.lexer.regexps;


import edu.unm.lexer.fa.FAFactory;
import edu.unm.lexer.fa.NFA;

public class REParser {

    public static RE parse(String s) {
        String[] tmp = nextContinues(s);
        if (tmp[1].length() == 0) {
            return continuesToRE(s);
        }
        return REFactory.alterRE(parse(tmp[0]), parse(tmp[1]));
    }

    public static RE parsePreFix(String s) {
        //there is no ' before symbols unless + * | ' represent as symbol not operator
        //construct the tree;
        if (s == null || s.length() == 0) {
            System.out.println("parse prefix expression s is null or empty");
            return null;
        }
        RENode root = null;
        if (s.charAt(0) == '_') {
            root = new RENode("");
            preOrder(s.toCharArray(), 1, root);
        } else if (s.charAt(0) == '\'') {
            if (s.charAt(1) == '_') {
                //actual _ not empty
                root = new RENode("_");
            } else {
                root= new RENode(s.substring(0,2));
            }
            preOrder(s.toCharArray(), 2, root);
        } else {
            root = new RENode(s.charAt(0) + "");
            preOrder(s.toCharArray(), 1, root);
        }
        return new RE(s, root);
    }

    private static int preOrder(char[] inputArr, int index, RENode root) {
        if (!root.key.equals("*") && !root.key.equals("+") && !root.key.equals("|")) {
            return index;
        }
        if (index < inputArr.length) {
            if (inputArr[index] == '_') {
                //empty
                RENode left = new RENode("");
                root.left = left;
                index += 1;
                index = preOrder(inputArr, index, left);
            } else if (inputArr[index] == '\'') {
                RENode left = null;
                if (inputArr[index + 1] == '_') {
                    //'_ represent _ itself
                    left = new RENode("_");
                } else {
                    left = new RENode(new String(new char[]{inputArr[index], inputArr[index + 1]}));
                }
                root.left = left;
                index += 2;
                index = preOrder(inputArr, index, left);
            } else {
                RENode left = new RENode(inputArr[index]+"");
                root.left = left;
                index++;
                index = preOrder(inputArr, index, left);
            }
        }
        if (!root.key.equals("*") && index < inputArr.length) {
            if (inputArr[index] == '_') {
                //empty
                RENode right = new RENode("");
                root.right = right;
                index += 1;
                index = preOrder(inputArr, index, right);
            } else if (inputArr[index] == '\'') {
                RENode right = null;
                if (inputArr[index + 1] == '_') {
                    right = new RENode("_");
                } else {
                    right = new RENode(new String(new char[]{inputArr[index], inputArr[index + 1]}));
                }
                root.right = right;
                index += 2;
                index = preOrder(inputArr, index, right);
            } else {
                RENode right = new RENode(inputArr[index]+"");
                root.right = right;
                index++;
                index = preOrder(inputArr, index, right);
            }
        }
        return index;
    }

    public static RE continuesToRE(String input) {
        //conver regular expression rt to NFA;
        //
        if (input.length() == 0) {
            System.out.println("continuesToRE error");
            return null;
        }
        if (input.length() == 1) {
            return REFactory.buildSingleCharRE(input.charAt(0));
        }
        if (input.charAt(0) == '(') {
            String[] tmp = getWithinPar(input);
            if (tmp[1].length() == 0) {
                return parse(tmp[0]);
            } else if (tmp[1].length() == 1) {
                if (tmp[1].charAt(0) == '*') {
                    return REFactory.starRE(parse(tmp[0]));
                } else {
                    return REFactory.concatRE(parse(tmp[0]), continuesToRE(tmp[1]));
                }
            } else {
                if (tmp[1].charAt(0) == '*') {
                    return REFactory.concatRE(REFactory.starRE(parse(tmp[0])), continuesToRE(tmp[1].substring(1, tmp[1].length())));
                } else {
                    return REFactory.concatRE(parse(tmp[0]), continuesToRE(tmp[1]));
                }
            }
        }
        if (input.charAt(0) == '|') {
            System.out.println("error for continuesToRE");
            return null;
        }

        if (input.charAt(1) == '*') {
            if (input.length() > 2) {
                return REFactory.concatRE(REFactory.starRE(REFactory.buildSingleCharRE(input.charAt(0))), continuesToRE(input.substring(2, input.length())));
            } else if (input.length() == 2) {
                return REFactory.starRE(REFactory.buildSingleCharRE(input.charAt(0)));
            } else {
                return null;
            }
        } else {
            return REFactory.concatRE(REFactory.buildSingleCharRE(input.charAt(0)), continuesToRE(input.substring(1, input.length())));
        }
    }

    public static String[] getWithinPar(String s) {
        //this is the function to take out the content inside the parentheses
        //only remove 1 layer of ()
        //input string s has to be start with (
        //return result[0] = s1---sj, such that (s1--sj).... is in s
        //return result[1] = ....
        StringBuilder within = new StringBuilder();
        StringBuilder out = new StringBuilder();

        char[] inputArr = s.toCharArray();
        int i = 1;
        int leftPar = 1;
        int rightPar = 0;
        //start from the char at index 1, since 0 is (
        while (leftPar > rightPar) {
            if (inputArr[i] == '(') {
                leftPar++;
            }
            if (inputArr[i] == ')') {
                rightPar++;
            }
            within.append(inputArr[i]);
            i++;
        }
        within.deleteCharAt(within.length() - 1); //delete the outside )

        for (; i < inputArr.length; i++) {
            out.append(inputArr[i]);
        }
        return new String[]{within.toString(), out.toString()};
    }

    public static String[] nextContinues(String input) {
        //find a subString s0--sj of input, such that there is no "|" outside the parentheses and s(j+1) == "|" or end;
        //input a string
        //output a string array that has 2 elements,
            //result[0] = s0-sj;
            //result[1] = sj+1 -- end;
        StringBuilder continues = new StringBuilder();
        StringBuilder rest = new StringBuilder();
        char[] inputArr = input.toCharArray();
        int leftPar = 0;
        int rightPar = 0;
        int i = 0;
        for (; i < inputArr.length; i++) {
            if (inputArr[i] != '|') {
                if (inputArr[i] == '(') {
                    leftPar++;
                }
                if (inputArr[i] == ')') {
                    rightPar++;
                }
                continues.append(inputArr[i]);
            } else {
                if (leftPar > rightPar) {
                    continues.append(inputArr[i]);
                } else {
                    break;
                }
            }
        }
        for (i = i + 1; i < inputArr.length; i++) {
            rest.append(inputArr[i]);
        }
        return new String[]{continues.toString(), rest.toString()};
    }
}
