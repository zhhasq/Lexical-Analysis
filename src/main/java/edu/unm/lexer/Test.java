package edu.unm.lexer;

import edu.unm.lexer.description.*;
import edu.unm.lexer.fa.DFA;
import edu.unm.lexer.fa.FAUtils;
import edu.unm.lexer.fa.NFA;
import edu.unm.lexer.fa.State;
import edu.unm.lexer.regexps.BTreePrinter;
import edu.unm.lexer.regexps.RE;
import edu.unm.lexer.regexps.REParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Test {
    //(a|b)*ab(a|b)*                   "+*|ab+a+b*|ab"
    //"_|a"                            "|_a"
    //('+ | '* | '| | '' | '_ | _)    "|'*|'||'+|''|'__"
    //(a(b|c)a*|((a|c*)|b))           "|+a+|bc*a||a*cb"
    // ($00*(a*|b*|c*)11*$)*          "*++&+0*0+|*a|*b*c+1+*1&"
    //


    //([0-9]+)  "+|1|2|3|4|5|6|7|8|90*|1|2|3|4|5|6|7|8|90"
    // (ab*)|(cf*)|(dk)*  |+a*b|+c*f*+dk

    public static List<String> q1 = new ArrayList<>(Arrays.asList(new String[]{"+*|ab+a+b*|ab" ,
                                                                                    "|_a",
                                                                                    "|'*|'||'+|''|'__",
                                                                                     "|+a+|bc*a||a*cb",
                                                                                    "*++&+0*0+|*a|*b*c+1+*1&"
           // "+|1|2|3|4|5|6|7|8|90*|1|2|3|4|5|6|7|8|90"
    }));

    public static HashMap<String, List<String>> testStringMap = new HashMap<>();

    public static void init() {
        List<String> tmp = new ArrayList<>(Arrays.asList(new String[]{"", "ab", "ba", "bbbaaa", "bbbaaabbbaaab","babaababa", "bababbab", "bbbbbb", "aaaaaa", "a", "b"}));
        testStringMap.put(q1.get(0), tmp);
        tmp = new ArrayList<>(Arrays.asList(new String[]{"", " ", "  "," a", "_","a", "aa", "b"}));
        testStringMap.put(q1.get(1), tmp);
        tmp = new ArrayList<>(Arrays.asList(new String[]{"*", "|", "+", "'", "", "_", "__","'_", " ","**", "--", "||", "''", "|+", "*+", "*'"}));
        testStringMap.put(q1.get(2), tmp);

        tmp = new ArrayList<>(Arrays.asList(new String[]{"", " ","ab", "abc", "abaaa", "acaaaaa", "a", "b","cccccc", "cb", "bc"}));
        testStringMap.put(q1.get(3), tmp);
        tmp = new ArrayList<>(Arrays.asList(new String[]{"&01&", "", "000011111", "&0000011111&", "abc", "a", "&0abc1&", "&00a1&", "&0b11&", "&&", "&0c1&&", "&01&&00111&"}));
        testStringMap.put(q1.get(4), tmp);
        tmp = new ArrayList<>(Arrays.asList(new String[]{"0000", "1", "2", "03", "54", "12412321", "10", "1234567890", ""}));
         //testStringMap.put(q1.get(5), tmp);
    }

    public static void main(String[] args) {
        init();
        for (int i = 0; i < q1.size();  i++) {
            System.out.println("Test case " + (i + 1) + ": " );
            System.out.println("__________________________________________________________________________________");

            String testString = q1.get(i);
            NFA nfa = test1(testString);
//            NFA reverseNFA = nfa.reverseNFA();
//            System.out.print(reverseNFA);
            DFA dfa = test2(nfa);
            DFA mini = test4(dfa);
            DFA mini2 = test3(dfa);
            DFA brzoMini = test6(nfa);
            if (isDFAsame(mini, mini2) && isDFAsame(mini2, brzoMini)) {
                System.out.println("*******************************");

                System.out.println("All three minimized dfa are same");
                System.out.println("*******************************");

            } else {
                System.out.println("false");
            }
            List<String> testingList = testStringMap.get(testString);
            if (testingList != null) {
                System.out.println();
                System.out.println();
                for (String ss : testingList) {
                    String printS = ss;
                    if (ss.equals("")) {
                        printS = "" + (char)(949);
                    }
                    System.out.println(printS + (test5(mini, ss) ? " accepted" : " refused"));
                    //System.out.println(printS + (test5(mini, ss) ? " accepted" : " refused") + "  " + (test5(brzoMini, ss) ? " accepted" : " refused"));
                }
            }
            System.out.println();
            System.out.println();
            System.out.println();

        }
        for (int i = 1; i < 6;i++) {
            String file11 ="src/testFiles/dfa/alphabet" + i + ".txt";
            String file12 ="src/testFiles/dfa/dfa" + i + ".txt";
            test8(file11, file12);
        }
        for (int i = 1; i < 4;i++) {
            String file11 ="src/testFiles/nfa/alphabet" + i + ".txt";
            String file12 ="src/testFiles/nfa/nfa" + i + ".txt";
            test9(file11, file12);
        }
        for (int i = 1; i < 5;i++) {
            String file11 ="src/testFiles/RE/alphabet" + i + ".txt";
            String file12 ="src/testFiles/RE/re" + i + ".txt";
            test10(file11, file12);
        }
        for (int i = 1; i < 4;i++) {
            String file11 ="src/testFiles/lexical/lexical" + i + ".txt";
            test11(file11);
        }
//        String file ="src/testFiles/lexical/lexical" + i + ".txt";
//        String file ="src/testFiles/RE/alphabet" + i + ".txt";


        // test11(2);
//        test11(2);
//        test11(3);

     //   //test9(1);
       // test10(1);
//        System.out.println("".charAt(0));
    }

    public static NFA test1(String inputString) {
        RE re = REParser.parsePreFix(inputString);
        System.out.println(re);
        BTreePrinter.printNode(re.getRoot());
        NFA nfa = FAUtils.REToNFA(re);
        System.out.println("NFA: ");
        System.out.println(nfa);

        return nfa;
    }

    public static DFA test2(NFA nfa) {
        DFA dfa = FAUtils.NFAToDFA(nfa);
        System.out.println();
        System.out.println();

        System.out.println("DFA: ");
        System.out.print(dfa);
        return dfa;
    }

    public static DFA test4(DFA dfa) {
        DFA mini = FAUtils.miniDFA(dfa);
        System.out.println();
        System.out.println();
        System.out.println("miniDFA: ");
        System.out.print(mini);
        return mini;
    }

    public static DFA test3(DFA dfa) {
        RE result = FAUtils.DFAToRE4(dfa);
        System.out.println();
        System.out.println();

        System.out.println("RE from DFA is ");
        System.out.println(result.toString());
        NFA nfa = FAUtils.REToNFA(result);
        DFA dfa2 = FAUtils.NFAToDFA(nfa);
        DFA mini = FAUtils.miniDFA(dfa2);
        System.out.println("mini DFA for this RE is : ");
        System.out.println(mini);
        return mini;
    }

    public static boolean test5(DFA dfa, String testInput) {
        return FAUtils.testDFA(dfa, testInput);
    }

    public static DFA test6(NFA nfa) {
        DFA mini = FAUtils.brzozowski(nfa);
        System.out.println();
        System.out.println();
        System.out.println("miniDFA from Brzozowski: ");
        System.out.print(mini);
        return mini;
    }

    public static void test8(String file1, String file2) {
        String file = file1;
        AlphabetDescription alphabetDescription = new AlphabetDescription();
        List<Token> tokenList = alphabetDescription.scan(readDescription(file));
        System.out.println("scan: " + file +": ");
        if (tokenList != null) {
            for (Token token : tokenList) {
                System.out.println(token);
            }
            Set<String> alphabet = alphabetDescription.parse(tokenList);
            System.out.println("alphabet set from AlphabetDescription.parser");
            for (String s : alphabet) {
                System.out.print(s + "  ");
            }
            System.out.println();
            file = file2;
            System.out.println("scan: " + file +": ");
            DFADescription dfaDescription = new DFADescription();
            tokenList = dfaDescription.scan(readDescription(file));
            if (tokenList != null) {
                printTokenList(tokenList);
                DFA dfa = dfaDescription.parse(alphabet, tokenList);
                if (dfa != null) {
                    System.out.println();
                    System.out.println();
                    System.out.println("dfa from: " + file);
                    System.out.println(dfa);
                }
            }
        }

        System.out.println();
    }

    public static void test9(String file1, String file2) {
        String file = file1;
        AlphabetDescription alphabetDescription = new AlphabetDescription();
        List<Token> tokenList = alphabetDescription.scan(readDescription(file));
        System.out.println("scan: " + file +": ");
        if (tokenList != null) {
            for (Token token : tokenList) {
                System.out.println(token + " ");
            }
            Set<String> alphabet = alphabetDescription.parse(tokenList);
            System.out.println("alphabet set from AlphabetDescription.parser");
            for (String s : alphabet) {
                System.out.print(s + "  ");
            }
            System.out.println();
            file = file2;
            System.out.println("scan: " + file +": ");

            NFADescription nfaDescription = new NFADescription();
            tokenList = nfaDescription.scan(readDescription(file));

            if (tokenList != null) {
                printTokenList(tokenList);
                NFA nfa = nfaDescription.parse(alphabet, tokenList);
                if (nfa != null) {
                    System.out.println();
                    System.out.println();
                    System.out.println("nfa from description " + file + ":");
                    System.out.println(nfa);
                }
            }

        }

        System.out.println();
    }

    public static void test10(String file1, String file2) {
        String file = file1;
        AlphabetDescription alphabetDescription = new AlphabetDescription();
        List<Token> tokenList = alphabetDescription.scan(readDescription(file));
        if (tokenList != null) {
            System.out.println("scan: " + file +": ");
            for (Token token : tokenList) {
                System.out.println(token);
            }
            Set<String> alphabet = alphabetDescription.parse(tokenList);
            System.out.println("alphabet set from AlphabetDescription.parser");
            for (String s : alphabet) {
                System.out.print(s + "  ");
            }
            System.out.println();
            file = file2;
            REDescription reDescription = new REDescription(alphabetDescription);
            tokenList = reDescription.scan(readDescription(file));
            if (tokenList != null) {
                System.out.println("scan: " + file +": ");
                for (Token token : tokenList) {
                    System.out.println(token);
                }
                RE re = reDescription.parse(tokenList);
                if (re != null) {
                    System.out.println("parsing " + file + "get: ");
                    System.out.println(re);
                }
                BTreePrinter.printNode(re.getRoot());
            }
        }

        System.out.println();
    }

    public static void test11(String file) {

        LexiDescription lexiDescription = new LexiDescription();
        List<Token> lexToken = lexiDescription.scan(readDescription(file));
        System.out.println("scan: " + file +": ");
        printTokenList(lexToken);
        System.out.println("parsing " + file + "get: ");
        Language language = lexiDescription.parse(lexToken);
        if (language != null) {
            System.out.println(language.toString());
        }
    }

    public static List<String> readDescription(String file){

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String currentLine = null;
        try {
            currentLine = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> inputList = new ArrayList<>();
        while(currentLine != null) {
            String[] tmp = currentLine.split(" ");
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].equals("'")) {
                    inputList.add("' ");
                } else {
                    inputList.add(tmp[i]);
                }
            }
            try {
                currentLine = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        LexiDescription lexiDescription = new LexiDescription();
//        List<Token> lexToken = lexiDescription.scan(inputList);
//        for (Token token : lexToken) {
//            System.out.println(token);
//        }
//        System.out.println(lexiDescription.parse(lexToken).toString());
        return inputList;

    }
    public static void printTokenList(List<Token> tokenList) {
        int count = 0;
        for (Token token : tokenList) {
            System.out.print(token);
            count++;
            if (count % 6 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public static boolean isDFAsame(DFA dfa1, DFA dfa2) {
        List<State> stateList1 = dfa1.getData();
        List<State> stateList2 = dfa2.getData();
        if (stateList1.size() != stateList2.size()) {
            return false;
        }
        HashMap<State, State> map = new HashMap<>();
        Deque<State> queue1 = new ArrayDeque<>();
        queue1.offer(dfa1.getInitState());

        Deque<State> queue2 = new ArrayDeque<>();
        queue2.offer(dfa2.getInitState());

        while(!queue1.isEmpty()) {
            if (queue2.isEmpty()) {
                return false;
            }
            State cur1 = queue1.poll();
            State cur2 = queue2.poll();
            if (map.containsKey(cur1) == false) {
                map.put(cur1, cur2);
                for (String s : dfa1.getAlphabet()) {
                    List<State> tmpList1 = cur1.getNext(s);
                    List<State> tmpList2 = cur2.getNext(s);
                    if (tmpList1 == null && tmpList2 != null) {
                        return false;
                    } else if (tmpList2 == null && tmpList1 != null) {
                        return false;
                    } else if (tmpList1 != null && tmpList2 != null) {
                        for (State tmpStat1 : tmpList1) {
                            queue1.offer(tmpStat1);
                        }
                        for (State tmpStat2 : tmpList2) {
                            queue2.offer(tmpStat2);
                        }
                    }
                }

            } else {
                if(cur2 != map.get(cur1)) {
                    return false;
                }
            }

        }
        if (!queue2.isEmpty()) {
            return false;
        }
        return true;
    }
}
