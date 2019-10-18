package edu.unm.lexer.fa;

import edu.unm.lexer.regexps.RE;
import edu.unm.lexer.regexps.REFactory;
import edu.unm.lexer.regexps.RENode;

import java.lang.reflect.Array;
import java.util.*;

public class FAUtils {
    //for question 1
    public static NFA REToNFA(RE re) {
        return inOrder(re.getRoot());
    }

    private static NFA inOrder(RENode root) {
        if (root == null) {
            return null;
        }
        NFA left = inOrder(root.left);
        NFA right = inOrder(root.right);
        if (root.key.equals("|")) {
            return FAFactory.alterNFANoCopy(left, right);
        } else if (root.key.equals("+")) {
           return FAFactory.concatNFANoCopy(left, right);
        }
        else if (root.key.equals("*")) {
            return FAFactory.makeNFAStarNoCopy(left);
        } else {
            if (root.key.equals("'*") || root.key.equals("'+") || root.key.equals("'|") || root.key.equals("''") || root.key.equals("'_")) {
                return FAFactory.buildSingleCharNFA(root.key.charAt(1));
            } else if (root.key.equals("")) {
                return FAFactory.buildEmptyCharNFA();
            }
            return FAFactory.buildSingleCharNFA(root.key.charAt(0));
        }
    }

    //for question 2
    public static DFA NFAToDFA(NFA nfa) {
        HashMap<HashSet<State>, State> map = new HashMap<>(); //map sets to new states in DFA, will create a new state for each set
        Deque<HashSet<State>> queue = new ArrayDeque<>();     //work list

        //initial step, add init state and all it's closure to a set
        HashSet<State> initSet = new HashSet<>();
        if (nfa.initStates != null) {
            //this part is for   Brzozowski's algorithm
            for (State init : nfa.initStates) {
                initSet.addAll(FAUtils.findEclosure(init));
            }
        } else {
            initSet.addAll(FAUtils.findEclosure(nfa.initState));
        }
        map.put(initSet, new State());
        queue.offer(initSet);

        while (!queue.isEmpty()) {
            HashSet<State> curSet = queue.poll();

            for (String c : nfa.alphabet) {
                HashSet<State> closure = new HashSet<>();

                for (State s : curSet) {
                    List<State> tmp = s.next.get(c);
                    if (tmp != null) {
                        closure.addAll(tmp);
                    }
                }
                HashSet<State> tmp = new HashSet<>();
                for (State s : closure) {
                    tmp.addAll(FAUtils.findEclosure(s));
                }
                closure.addAll(tmp);
                if (!closure.isEmpty()) {
                    State tmpState = map.get(closure);
                    if (tmpState == null) {
                        queue.offer(closure);
                        map.put(closure, new State());
                    }
                    createConnection(map.get(curSet), map.get(closure), c);
                }
            }
        }

        //Create the DFA object
        List<State> newData = new ArrayList<>();
        newData.add(map.get(initSet));
        Set<State> acceptStates = new HashSet<>();
        State acceptState = null;
        for (Map.Entry<HashSet<State>, State> entry : map.entrySet()) {
            if (!entry.getKey().equals(initSet)) {
                newData.add(entry.getValue());
            }
            for (State s : entry.getKey()) {
                if (s == nfa.acceptState) {
                    acceptState = entry.getValue();
                    acceptStates.add(acceptState);
                    break;
                }
            }
        }
        DFA result = new DFA(newData, map.get(initSet), acceptState, nfa.alphabet);
        result.acceptStates = acceptStates;
        return result;
    }

    public static void createConnection(State a, State b, String s) {
        //!!!note this operation is in memory
        List<State> aNext = a.next.get(s);
        if (aNext == null) {
            aNext = new ArrayList<>();
            aNext.add(b);
            a.next.put(s, aNext);
        } else {
            aNext.add(b);
        }

        List<State> bPrev = b.prev.get(s);
        if (bPrev == null) {
            bPrev = new ArrayList<>();
            bPrev.add(a);
            b.prev.put(s, bPrev);
        } else {
           bPrev.add(a);
        }
    }
//
//
//    //for question 3
    public static RE DFAToRE2(DFA dfa) {

        List<State> data = dfa.data;
        int numState = data.size();

        RE[] b = new RE[numState];
        RE[][] a = new RE[numState][numState];

        for (int i = 0; i < numState; i++) {
            if (dfa.acceptStates.contains(data.get(i))) {
                RE re = REFactory.buildEmptyRE();
                b[i] = re;
            } else {
                b[i] = null;
            }
        }

        for (int i = 0; i < numState; i++) {
            for (int j = 0; j < numState; j++) {
                for (String alpha : dfa.alphabet) {
                    if (trans(data.get(i), data.get(j), alpha)) {
                        RE tmp = null;
                        if (alpha.equals("*") || alpha.equals("|") || alpha.equals("'") || alpha.equals("+")) {
                            tmp = new RE("'" + alpha, new RENode("'" + alpha));
                        } else {
                            tmp = REFactory.buildSingleCharRE(alpha.charAt(0));
                        }
                        if (a[i][j] == null) {
                            a[i][j] = tmp;
                        } else {
                            a[i][j] = REFactory.alterRE(a[i][j], tmp);
                        }
                    }
                }
            }
        }

        for (int n = numState - 1; n >=0; n--) {
            if (a[n][n] != null && b[n] != null) {
                //optimization
                if (isEmtpy(a[n][n]) && isEmtpy(b[n])) {
                    b[n] = b[n];
                } else if (isEmtpy(a[n][n])) {
                    b[n] = b[n];
                } else if (isEmtpy(b[n])){
                    b[n] = REFactory.starRE(a[n][n]);
                } else {
                    b[n] = REFactory.concatRE(REFactory.starRE(a[n][n]), b[n]);
                }
               // b[n] = REFactory.concatRE(REFactory.starRE(a[n][n]), b[n]);
            }

            for (int j = 0; j <= n; j++) {
                if (a[n][n] != null && a[n][j] != null) {
                    //optimization
                    if (isEmtpy(a[n][n]) && isEmtpy(a[n][j])) {
                        a[n][j] = a[n][j];
                    } else if (isEmtpy(a[n][n])) {
                        a[n][j] = a[n][j];
                    } else if (isEmtpy(a[n][j])){
                        a[n][j] = REFactory.starRE(a[n][n]);
                    } else {
                        a[n][j] = REFactory.concatRE(REFactory.starRE(a[n][n]), a[n][j]);
                    }
                   // a[n][j] = REFactory.concatRE(REFactory.starRE(a[n][n]), a[n][j]);
                }
            }

            for (int i = 0; i <= n; i++) {
                if (b[i] == null) {
                    if (a[i][n] != null && b[n] != null) {
                        //optimization
                        if (isEmtpy(a[i][n]) && isEmtpy(b[n])) {
                            b[i] = b[n];
                        } else if (isEmtpy(a[i][n])) {
                            b[i] = b[n];
                        } else if (isEmtpy(b[n])){
                            b[i] = a[i][n];
                        } else {
                            b[i] = REFactory.concatRE(a[i][n], b[n]);
                        }
                       // b[i] = REFactory.concatRE(a[i][n], b[n]);
                    }
                } else {
                    if (a[i][n] != null && b[n] != null) {
                        //optimization
                        if (isEmtpy(a[i][n]) && isEmtpy(b[n]) && isEmtpy(b[i])) {
                            b[i] = b[i];
                        } else if (isEmtpy(a[i][n]) && isEmtpy(b[n])) {
                            b[i] = REFactory.alterRE(b[i], b[n]);
                        } else if (isEmtpy(b[n])){
                            b[i] =REFactory.alterRE(b[i], a[i][n]);
                        } else if (isEmtpy(a[i][n])) {
                            b[i] =REFactory.alterRE(b[i], b[n]);
                        } else {
                            b[i] = REFactory.alterRE(b[i], REFactory.concatRE(a[i][n], b[n]));
                        }
                        //b[i] = REFactory.alterRE(b[i], REFactory.concatRE(a[i][n], b[n]));
                    }
                }

                for (int j = 0; j <= n; j++) {
                    if (a[i][j] == null) {
                        if (a[i][n] != null && a[n][j] != null) {
                            //optimization
                            if (isEmtpy(a[i][n]) && isEmtpy(a[n][j])) {
                                a[i][j] = a[i][n];
                            } else if (isEmtpy(a[i][n])) {
                                a[i][j] = a[n][j];
                            } else if (isEmtpy(a[n][j])){
                                a[i][j] = a[i][n];
                            } else {
                                a[i][j] = REFactory.concatRE(a[i][n], a[n][j]);
                            }
                          //  a[i][j] = REFactory.concatRE(a[i][n], a[n][j]);
                        }
                    } else {
                        if (a[i][n] != null && a[n][j] != null) {
                            //optimization
                            if (isEmtpy(a[i][j]) && isEmtpy(a[i][n]) && isEmtpy(a[n][j])) {
                                a[i][j] = a[i][j];
                            } else if (isEmtpy(a[i][n]) && isEmtpy(a[n][j])) {
                                a[i][j] = REFactory.alterRE(a[i][j], a[i][n]);
                            } else if (isEmtpy(a[n][j])){
                                a[i][j] = REFactory.alterRE(a[i][j], a[i][n]);
                            } else if (isEmtpy(a[i][n])) {
                                a[i][j] = REFactory.alterRE(a[i][j], a[n][j]);
                            } else {
                                a[i][j] = REFactory.alterRE(a[i][j], REFactory.concatRE(a[i][n], a[n][j]));
                            }
                           // a[i][j] = REFactory.alterRE(a[i][j], REFactory.concatRE(a[i][n], a[n][j]));
                        }
                    }
                }
            }
        }
        return b[0];
    }
    public static RE DFAToRE3(DFA dfa) {

        List<State> data = dfa.data;
        int numState = data.size();

        RE[] b = new RE[numState];
        RE[][] a = new RE[numState][numState];

        for (int i = 0; i < numState; i++) {
            if (dfa.acceptStates.contains(data.get(i))) {
                RE re = REFactory.buildEmptyRE();
                b[i] = re;
            } else {
                b[i] = null;
            }
        }

        for (int i = 0; i < numState; i++) {
            for (int j = 0; j < numState; j++) {
                for (String alpha : dfa.alphabet) {
                    if (trans(data.get(i), data.get(j), alpha)) {
                        RE tmp = null;
                        if (alpha.equals("*") || alpha.equals("|") || alpha.equals("'") || alpha.equals("+")) {
                            tmp = new RE("'" + alpha, new RENode("'" + alpha));
                        } else {
                            tmp = REFactory.buildSingleCharRE(alpha.charAt(0));
                        }
                        if (a[i][j] == null) {
                            a[i][j] = tmp;
                        } else {
                            a[i][j] = REFactory.alterRE(a[i][j], tmp);
                        }
                    }
                }
            }
        }

        for (int n = numState - 1; n >=0; n--) {
            if (a[n][n] != null && b[n] != null) {
                //optimization
                if (isEmtpy(a[n][n]) && isEmtpy(b[n])) {
                    b[n] = b[n];
                } else if (isEmtpy(a[n][n])) {
                    b[n] = b[n];
                } else if (isEmtpy(b[n])){
                    b[n] = REFactory.starRE(a[n][n]);
                } else {
                    b[n] = REFactory.concatRENoCopy(REFactory.starRENoCopy(a[n][n]), b[n]);
                }
                // b[n] = REFactory.concatRE(REFactory.starRE(a[n][n]), b[n]);
            }

            for (int j = 0; j <= n; j++) {
                if (a[n][n] != null && a[n][j] != null) {
                    //optimization
                    if (isEmtpy(a[n][n]) && isEmtpy(a[n][j])) {
                        a[n][j] = a[n][j];
                    } else if (isEmtpy(a[n][n])) {
                        a[n][j] = a[n][j];
                    } else if (isEmtpy(a[n][j])){
                        a[n][j] = REFactory.starRENoCopy(a[n][n]);
                    } else {
                        a[n][j] = REFactory.concatRENoCopy(REFactory.starRENoCopy(a[n][n]), a[n][j]);
                    }
                    // a[n][j] = REFactory.concatRE(REFactory.starRE(a[n][n]), a[n][j]);
                }
            }

            for (int i = 0; i <= n; i++) {
                if (b[i] == null) {
                    if (a[i][n] != null && b[n] != null) {
                        //optimization
                        if (isEmtpy(a[i][n]) && isEmtpy(b[n])) {
                            b[i] = b[n];
                        } else if (isEmtpy(a[i][n])) {
                            b[i] = b[n];
                        } else if (isEmtpy(b[n])){
                            b[i] = a[i][n];
                        } else {
                            b[i] = REFactory.concatRENoCopy(a[i][n], b[n]);
                        }
                        // b[i] = REFactory.concatRE(a[i][n], b[n]);
                    }
                } else {
                    if (a[i][n] != null && b[n] != null) {
                        //optimization
                        if (isEmtpy(a[i][n]) && isEmtpy(b[n]) && isEmtpy(b[i])) {
                            b[i] = b[i];
                        } else if (isEmtpy(a[i][n]) && isEmtpy(b[n])) {
                            b[i] = REFactory.alterRENoCopy(b[i], b[n]);
                        } else if (isEmtpy(b[n])){
                            b[i] =REFactory.alterRENoCopy(b[i], a[i][n]);
                        } else if (isEmtpy(a[i][n])) {
                            b[i] =REFactory.alterRENoCopy(b[i], b[n]);
                        } else {
                            b[i] = REFactory.alterRENoCopy(b[i], REFactory.concatRENoCopy(a[i][n], b[n]));
                        }
                        //b[i] = REFactory.alterRE(b[i], REFactory.concatRE(a[i][n], b[n]));
                    }
                }

                for (int j = 0; j <= n; j++) {
                    if (a[i][j] == null) {
                        if (a[i][n] != null && a[n][j] != null) {
                            //optimization
                            if (isEmtpy(a[i][n]) && isEmtpy(a[n][j])) {
                                a[i][j] = a[i][n];
                            } else if (isEmtpy(a[i][n])) {
                                a[i][j] = a[n][j];
                            } else if (isEmtpy(a[n][j])){
                                a[i][j] = a[i][n];
                            } else {
                                a[i][j] = REFactory.concatRENoCopy(a[i][n], a[n][j]);
                            }
                            //  a[i][j] = REFactory.concatRE(a[i][n], a[n][j]);
                        }
                    } else {
                        if (a[i][n] != null && a[n][j] != null) {
                            //optimization
                            if (isEmtpy(a[i][j]) && isEmtpy(a[i][n]) && isEmtpy(a[n][j])) {
                                a[i][j] = a[i][j];
                            } else if (isEmtpy(a[i][n]) && isEmtpy(a[n][j])) {
                                a[i][j] = REFactory.alterRENoCopy(a[i][j], a[i][n]);
                            } else if (isEmtpy(a[n][j])){
                                a[i][j] = REFactory.alterRENoCopy(a[i][j], a[i][n]);
                            } else if (isEmtpy(a[i][n])) {
                                a[i][j] = REFactory.alterRENoCopy(a[i][j], a[n][j]);
                            } else {
                                a[i][j] = REFactory.alterRENoCopy(a[i][j], REFactory.concatRENoCopy(a[i][n], a[n][j]));
                            }
                            // a[i][j] = REFactory.alterRE(a[i][j], REFactory.concatRE(a[i][n], a[n][j]));
                        }
                    }
                }
            }
        }
        return b[0];
    }
    public static RE DFAToRE4(DFA dfa) {

        List<State> data = dfa.data;
        int numState = data.size();

        RE[] b = new RE[numState];
        RE[][] a = new RE[numState][numState];

        for (int i = 0; i < numState; i++) {
            if (dfa.acceptStates.contains(data.get(i))) {
                RE re = REFactory.buildEmptyRE();
                b[i] = re;
            } else {
                b[i] = null;
            }
        }

        for (int i = 0; i < numState; i++) {
            for (int j = 0; j < numState; j++) {
                for (String alpha : dfa.alphabet) {
                    if (trans(data.get(i), data.get(j), alpha)) {
                        RE tmp = null;
                        if (alpha.equals("*") || alpha.equals("|") || alpha.equals("'") || alpha.equals("+")) {
                            tmp = new RE("'" + alpha, new RENode("'" + alpha));
                        } else if (alpha.equals("_")) {
                            tmp = new RE("'" + alpha, new RENode(alpha));
                        }else {

                            tmp = REFactory.buildSingleCharRE(alpha.charAt(0));

                        }
                        if (a[i][j] == null) {
                            a[i][j] = tmp;
                        } else {
                            a[i][j] = REFactory.alterRE(a[i][j], tmp);
                        }
                    }
                }
            }
        }

        for (int n = numState - 1; n >=0; n--) {
            if (a[n][n] != null && b[n] != null) {

                b[n] = REFactory.concatRE(REFactory.starRE(a[n][n]), b[n]);
            }

            for (int j = 0; j <= n; j++) {
                if (a[n][n] != null && a[n][j] != null) {
                    a[n][j] = REFactory.concatRE(REFactory.starRE(a[n][n]), a[n][j]);
                }
            }

            for (int i = 0; i <= n; i++) {
                if (b[i] == null) {
                    if (a[i][n] != null && b[n] != null) {
                        b[i] = REFactory.concatRE(a[i][n], b[n]);
                    }
                } else {
                    if (a[i][n] != null && b[n] != null) {
                        b[i] = REFactory.alterRE(b[i], REFactory.concatRE(a[i][n], b[n]));
                    }
                }

                for (int j = 0; j <= n; j++) {
                    if (a[i][j] == null) {
                        if (a[i][n] != null && a[n][j] != null) {
                            a[i][j] = REFactory.concatRE(a[i][n], a[n][j]);
                        }
                    } else {
                        if (a[i][n] != null && a[n][j] != null) {
                            a[i][j] = REFactory.alterRE(a[i][j], REFactory.concatRE(a[i][n], a[n][j]));
                        }
                    }
                }
            }
        }
        return b[0];
    }
    private static boolean trans(State a, State b, String condition) {
        List<State> next = a.next.get(condition);
        if (next != null) {
            for (State s : next) {
                if (s == b) {
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean isEmtpy(RE re) {
        if (re.getPreFixExpression().equals("_")) {
            return true;
        }
        return false;
    }
    public static RE DFAToRE(DFA dfa) {
        int numStates = dfa.generateID();
        //    k  i j
        RE[][][] table = new RE[numStates + 1][numStates + 1][numStates + 1];
        //initial step
        // k = 0, not passing any states, direct from i to j
        for (int i = 1; i < numStates + 1; i++) {
            for (int j = 1; j < numStates + 1; j++) {
                HashSet<String> tmp = new HashSet<>();
                tmp.addAll(findDirectLink(dfa, i - 1, j - 1));

                RE re = null;
                for (String s : tmp) {
                    if (s.length() != 0) {
                        RE reForS = null;
                        if (s.equals("*") || s.equals("|") || s.equals("+") || s.equals("'")) {
                            reForS = new RE("'" + s, new RENode("'" + s));
                        } else {
                            reForS = REFactory.buildSingleCharRE(s.charAt(0));
                        }
                        if (re == null) {
                            re = reForS;
                        } else {
                            re = REFactory.alterRENoCopy(re, reForS);
                        }
                    }
                }
                if (re == null && i == j) {
                    re = REFactory.buildEmptyRE();
                }
                table[0][i][j] = re;
            }
        }
        for (int k = 1; k < numStates + 1; k++) {
            for (int i = 1; i < numStates + 1; i++) {
                for (int j = 1; j < numStates + 1; j++) {
                    if (table[k - 1][i][j] != null && table[k - 1][i][k] != null && table[k - 1][k][k] != null &&table[k-1][k][j] != null) {
                        RE tmp1 = table[k - 1][i][j];
                        RE tmp2 = table[k - 1][i][k];
                        RE tmp3 = table[k - 1][k][k];
                        RE tmp4 = table[k - 1][k][j];
                        table[k][i][j] = REFactory.alterRENoCopy(tmp1, REFactory.concatRENoCopy(tmp2, REFactory.concatRENoCopy(REFactory.starRENoCopy(tmp3), tmp4)));

                    } else if (table[k - 1][i][j] != null){
                        table[k][i][j] = table[k - 1][i][j];
                    } else if (table[k - 1][i][k] != null && table[k - 1][k][k] != null &&table[k-1][k][j] != null) {
                        RE tmp2 = table[k - 1][i][k];
                        RE tmp3 = table[k - 1][k][k];
                        RE tmp4 = table[k - 1][k][j];
                        table[k][i][j] = REFactory.concatRENoCopy(tmp2, REFactory.concatRENoCopy(REFactory.starRENoCopy(tmp3), tmp4));
                    }
                }
            }
        }
        RE result = null;
        for (State s : dfa.acceptStates) {
            if (table[numStates][dfa.initState.getID() + 1][s.getID() + 1] != null) {
                if (result == null) {
                    result = (table[numStates][dfa.initState.getID() + 1][s.getID() + 1]);
                } else {
                    result = REFactory.alterRENoCopy(result, table[numStates][dfa.initState.getID() + 1][s.getID() + 1]);
                }
            }
        }
        return result;
    }
//    public static String DFAToRE(DFA dfa) {
//        int numStates = dfa.generateID();
//        //    k  i j
//        String[][][] table = new String[numStates + 1][numStates + 1][numStates + 1];
//        //initial step
//        // k = 0, not passing any states, direct from i to j
//        for (int i = 1; i < numStates + 1; i++) {
//            for (int j = 1; j < numStates + 1; j++) {
//                HashSet<String> tmp = new HashSet<>();
//                if (i == j) {
//                    tmp.add("_");
//                }
//                tmp.addAll(findDirectLink(dfa, i - 1, j - 1));
//                StringBuilder expression = new StringBuilder();
//                if (tmp.size() == 0) {
//                    table[0][i][j] = null;
//                    continue;
//                }
//
//                for (String s : tmp) {
//                   expression.append("|");
//                   expression.append(s);
//                }
//                expression.deleteCharAt(0);  //delete first "|"
//                expression.insert(0, "(");
//                expression.append(")");
//
//                table[0][i][j] = expression.toString();
//            }
//        }
//
//        for (int k = 1; k < numStates + 1; k++) {
//            for (int i = 1; i < numStates + 1; i++) {
//                for (int j = 1; j < numStates + 1; j++) {
//                    if (table[k - 1][i][j] != null && table[k - 1][i][k] != null && table[k - 1][k][k] != null &&table[k-1][k][j] != null) {
//                        //optimization
//                        if (table[k - 1][i][k].equals(table[k - 1][k][k])) {
//                            if (table[k - 1][i][k].equals("_")) {
//                                if (table[k-1][k][j].equals("_")) {
//                                    table[k][i][j] = table[k - 1][i][j];
//                                } else {
//                                    table[k][i][j] = "(" + table[k - 1][i][j] + "|" + table[k-1][k][j] + ")";
//                                }
//                            } else {
//                                table[k][i][j] = "(" + table[k - 1][i][j] + "|" + "(" + table[k - 1][k][k] + ")" + "*" + table[k-1][k][j] + ")";
//                            }
//
//                        } else {
//                            table[k][i][j] = "(" + table[k - 1][i][j] + "|" + table[k - 1][i][k] + "(" + table[k - 1][k][k] + ")" + "*" + table[k-1][k][j] + ")";
//                        }
//                    } else if (table[k - 1][i][j] != null){
//                        table[k][i][j] = table[k - 1][i][j];
//                    } else if (table[k - 1][i][k] != null && table[k - 1][k][k] != null &&table[k-1][k][j] != null) {
//                        if (table[k - 1][i][k].equals(table[k - 1][k][k])) {
//                            if (table[k - 1][i][k].equals("_")) {
//                                if (table[k-1][k][j].equals("_")) {
//                                    table[k][i][j] = "_";
//                                } else {
//                                    table[k][i][j] = "(" + table[k - 1][k][k] + ")" + "*" + table[k-1][k][j];
//                                }
//                            }
//                        } else {
//                            table[k][i][j] = table[k - 1][i][k] + "(" + table[k - 1][k][k] + ")" + "*" + table[k-1][k][j];
//                        }
//
//                    }
//
//                }
//            }
//        }
//        StringBuilder result = new StringBuilder();
//        for (State s : dfa.acceptStates) {
//
//            result.append("|");
//            if (table[numStates][dfa.initState.getID() + 1][s.getID() + 1] != null) {
//                result.append(table[numStates][dfa.initState.getID() + 1][s.getID() + 1]);
//            }
//        }
//        result.deleteCharAt(0);
//        return result.toString();
//
//    }

    private static Set<String> findDirectLink(DFA dfa, int startIndex, int targetIndex) {
        Set<String> result = new HashSet<>();
        State start = dfa.getState(startIndex);
        State target = dfa.getState(targetIndex);
        for (Map.Entry<String, List<State>> entry : start.next.entrySet()){
            for (State s : entry.getValue()) {
                if (s == target) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

   // for question 4
    public static DFA miniDFA(DFA dfa) {
        HashSet<State> nonFinalStates = new HashSet<>();
        HashSet<State> FinalStates = new HashSet<>();
        HashMap<HashSet<State>, Integer> map = new HashMap<>();

        for (State s : dfa.getData()) {
            if (dfa.acceptStates.contains(s)) {
                FinalStates.add(s);
            } else {
                nonFinalStates.add(s);
            }
        }
        List<String> alphaList = new ArrayList<>(dfa.alphabet);
        map.put(nonFinalStates, 0);
        map.put(FinalStates, 0);
        int size = map.size();
        split(map, alphaList);
        while (size != map.size()) {
            size = map.size();
            split(map, alphaList);
        }
        return setsToDFA(dfa, map, alphaList);
    }

    private static DFA setsToDFA(DFA dfa, HashMap<HashSet<State>, Integer> map, List<String> alphaList) {
        HashMap<HashSet<State>, State> connectionMap = new HashMap<>();
        HashMap<State, HashSet<State>> belongMap = new HashMap<>();
        //build belongMap
        for (Map.Entry<HashSet<State>, Integer> entry : map.entrySet()) {
            connectionMap.put(entry.getKey(), new State());
            for (State state : entry.getKey()) {
                belongMap.put(state, entry.getKey());
            }
        }

        for (String s : alphaList) {
            for (Map.Entry<HashSet<State>, State> entry : connectionMap.entrySet()) {
                for (State state : entry.getKey()) {
                    List<State> nextTmpList = state.next.get(s);
                    if (nextTmpList != null && nextTmpList.size() > 0) {
                        createConnection(entry.getValue(), connectionMap.get(belongMap.get(nextTmpList.get(0))), s);
                    }
                    break;
                }
            }
        }
        State newInit = connectionMap.get(belongMap.get(dfa.initState));
        HashSet<State> acceptStates = new HashSet<>();
        for (State state : dfa.acceptStates) {
            acceptStates.add(connectionMap.get(belongMap.get(state)));
        }
        List<State> newData = new ArrayList<>();
        newData.add(newInit);
        for (Map.Entry<HashSet<State>, State> entry : connectionMap.entrySet()) {
            if (entry.getValue() != newInit) {
                newData.add(entry.getValue());
            }
        }
        List<State> newDataNoAlone = new ArrayList<>();
        for (State s : newData) {
            if (!s.next.isEmpty() || !s.prev.isEmpty()) {
                newDataNoAlone.add(s);
            }
        }

        return new DFA(newDataNoAlone, newInit, acceptStates, dfa.alphabet);
    }

    private static void split(HashMap<HashSet<State>, Integer> map, List<String> alphaList) {
        //generate ID for each element, and put them in the queue
        Deque <HashSet<State>> queue = new ArrayDeque<>();
        int index = 0;
        for (Map.Entry<HashSet<State>, Integer> entry : map.entrySet()) {
            queue.offer(entry.getKey());
            map.put(entry.getKey(), index++);
        }
        while (!queue.isEmpty()) {
            HashSet<State> curSet = queue.poll();
            Map<State, Integer[]> curTable = toTransformTable(map, curSet, alphaList);
            List<HashSet<State>> splited = splitTable(curTable);
            if (splited.size() > 1) {
                //indicate that the table can be split.
                queue.clear();
                map.remove(curSet);
                for (HashSet<State> set : splited) {
                    map.put(set, 0);
                }
            }
        }
    }

    private static List<HashSet<State>> splitTable(Map<State, Integer[]> table) {
        List<HashSet<State>> result = new ArrayList<>();
        HashSet<State> visited = new HashSet<>();

        for (Map.Entry<State, Integer[]> entry : table.entrySet()) {
            if(!visited.contains(entry.getKey())) {
                HashSet<State> tmp = findSame(table, entry.getKey());
                if (tmp.size() > 0) {
                    result.add(tmp);
                    visited.addAll(tmp);
                }
            }
        }
        return result;
    }

    private static HashSet<State> findSame(Map<State, Integer[]> table, State target) {
        //find all the state that has same category as target and put them in the result set.
        HashSet<State> result = new HashSet<>();
        result.add(target);
        Integer[] targetArr = table.get(target);
        for (Map.Entry<State, Integer[]> entry : table.entrySet()) {
            Integer[] compare = entry.getValue();
            if (entry.getKey() != target) {
                int i = 0;
                for (; i < targetArr.length; i++) {
                    if (targetArr[i] != null && compare[i] != null) {
                        if (targetArr[i] != compare[i]) {
                            break;
                        }
                    } else if (targetArr[i] == null && compare[i] == null){
                        //do nothing
                    } else {
                        break;
                    }
                }
                if (i == targetArr.length) {
                    //two array are same
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

    private static Map<State, Integer[]> toTransformTable(HashMap<HashSet<State>, Integer> map, Set<State> stateSet, List<String> alphaList) {

        Map<State, Integer[]> result = new HashMap<>();
        for (State state : stateSet) {
            Integer[] tmpArray = new Integer[alphaList.size()];
            for (int j = 0; j < alphaList.size(); j++) {
                List<State> tmp = state.next.get(alphaList.get(j));
                //since this is DFA so, tmp should be null or only 1 elements.
                if (tmp == null || tmp.size() == 0) {
                    tmpArray[j] = null;
                } else if (tmp.size() > 1) {
                    System.out.println("toTransformTable error there are multiple state for DFA");
                } else {
                    //tmp.size() == 1
                    tmpArray[j] = findCategory(map, tmp.get(0));
                }
            }
            result.put(state, tmpArray);
        }
        return result;
    }

    private static int findCategory(HashMap<HashSet<State>, Integer> map, State state) {
        for (Map.Entry<HashSet<State>, Integer> entry : map.entrySet()) {
            if (entry.getKey().contains(state)) {
                return entry.getValue();
            }
        }
        return -1;
    }

    //for question 5
    public static boolean testDFA(DFA dfa, String input) {

        State start = dfa.getInitState();
        State cur = start;
        char[] inputArr = input.toCharArray();
        for (char c : inputArr) {
            cur = dfa.mov(cur, new String(new char[]{c}));
            if (cur == null) {
                //no where to go
                return false;
            }
        }
        return dfa.isAccpeted(cur);
    }

    public static HashSet<State> findEclosure(State state) {
        if (state == null) {
            return null;
        }
        HashSet<State> result = new HashSet<>();
        findEclosureDFS(state, result);
        return result;
    }

    private static void findEclosureDFS(State state, HashSet<State> result) {
        result.add(state);
        List<State> eList = state.next.get("");
        if (eList == null || eList.size() == 0) {
            return;
        }
        for (State s : eList) {
            if (result.add(s) == true) {
                findEclosureDFS(s, result);
            }
        }
    }

    //for question 6
    public static DFA brzozowski(NFA nfa) {
        NFA reverseNFA = FAFactory.reverseNFA(nfa);
        DFA dfa = NFAToDFA(reverseNFA);
        NFA reverseDFA = FAFactory.reverseDFA(dfa);
        DFA minidfa = NFAToDFA(reverseDFA);
        return minidfa;
    }

}
