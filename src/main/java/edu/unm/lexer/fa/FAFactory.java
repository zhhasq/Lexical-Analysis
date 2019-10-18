package edu.unm.lexer.fa;

import java.lang.reflect.Array;
import java.util.*;

public class FAFactory {
    public static NFA buildEmptyCharNFA() {
        //0(start) -- "" --> 1(final)
        List<State> data = new ArrayList<>();
        State initState = new State();
        State acceptState = new State();
        data.add(initState);
        data.add(acceptState);

        //initialize the init state
        HashMap<String, List<State>> next = new HashMap<>();

        next.put("", Arrays.asList(new State[]{acceptState}));

        initState.next = next;

        //initialize the accept state
        HashMap<String, List<State>> pre = new HashMap<>();
        pre.put("", Arrays.asList(new State[]{initState}));

        acceptState.prev = pre;

        HashSet<String> alpha = new HashSet<>();

       // alpha.add("");

        return new NFA(data, initState, acceptState, alpha);
    }

    public static NFA buildSingleCharNFA(char c) {
        //0(start) -- c --> 1(final)
        List<State> data = new ArrayList<>();
        State initState = new State();
        State acceptState = new State();
        data.add(initState);
        data.add(acceptState);

        //initialize the init state
        HashMap<String, List<State>> next = new HashMap<>();

        next.put(new String(new char[]{c}), Arrays.asList(new State[]{acceptState}));

        initState.next = next;

        //initialize the accept state
        HashMap<String, List<State>> pre = new HashMap<>();

        pre.put(new String(new char[]{c}), Arrays.asList(new State[]{initState}));

        acceptState.prev = pre;

        HashSet<String> alpha = new HashSet<>();

        alpha.add(new String(new char[]{c}));

        return new NFA(data, initState, acceptState, alpha);
    }

    public static NFA concatNFA(NFA nfa1, NFA nfa2) {
        if (nfa1 == null || nfa2 == null) {
            System.out.println("cannot concat null NFA");
            return null;
        }
        //After concatenation, nfa1 and nfa2 are not valid anymore
        //since the State in the list will be changed.
        nfa1 = copyNFA(nfa1);
        nfa2 = copyNFA(nfa2);
        return concatNFANoCopy(nfa1, nfa2);
    }

    public static NFA makeNFAStar(NFA nfa) {
        if (nfa == null) {
            System.out.println("cannot star null NFA");
            return null;
        }
        nfa = copyNFA(nfa);
        return makeNFAStarNoCopy(nfa);
    }

    public static NFA alterNFA(NFA nfa1, NFA nfa2) {
        if (nfa1 == null || nfa2 == null) {
            System.out.println("cannot alter null NFA");
            return null;
        }
        nfa1 = copyNFA(nfa1);
        nfa2 = copyNFA(nfa2);
        return alterNFANoCopy(nfa1, nfa2);

    }

    public static NFA concatNFANoCopy(NFA nfa1, NFA nfa2) {
        if (nfa1 == null || nfa2 == null) {
            System.out.println("cannot concat null NFA");
            return null;
        }
        //After concatenation, nfa1 and nfa2 are not valid anymore
        //since the State in the list will be changed.

        List<State> data = new ArrayList<>();
        data.addAll(nfa1.data);
        data.addAll(nfa2.data);

        List<State> tmpNext = nfa1.acceptState.next.get("");
        if (tmpNext == null) {
            tmpNext = new ArrayList<>();
            tmpNext.add(nfa2.initState);
            nfa1.acceptState.next.put("", tmpNext);
        } else {
            tmpNext.add(nfa2.initState);
        }

        List<State> tmpPrev = nfa2.initState.prev.get("");
        if (tmpPrev == null) {
            tmpPrev = new ArrayList<>();
            tmpPrev.add(nfa1.acceptState);
            nfa2.initState.prev.put("", tmpPrev);
        } else {
            tmpPrev.add(nfa1.acceptState);
        }
        HashSet<String> alpha = new HashSet<>();
        alpha.addAll(nfa1.alphabet);
        alpha.addAll(nfa2.alphabet);
        return new NFA(data, nfa1.initState, nfa2.acceptState, alpha);
    }

    public static NFA makeNFAStarNoCopy(NFA nfa) {
        if (nfa == null) {
            System.out.println("cannot star null NFA");
            return null;
        }
        State initial = new State();
        State accept = new State();
        List<State> tmpList = new ArrayList<>();
        tmpList.add(accept);
        tmpList.add(nfa.initState);
        initial.next.put("", tmpList);

        List<State> preList = nfa.initState.prev.get("");
        if (preList == null) {
            preList = new ArrayList<>();
            preList.add(initial);
            nfa.initState.prev.put("", preList);
        } else {
            preList.add(initial);
        }

        List<State> nextList = nfa.acceptState.next.get("");
        if (nextList == null) {
            nextList = new ArrayList<>();
            nextList.add(initial);
            nfa.acceptState.next.put("", nextList);
        } else {
            nextList.add(initial);
        }
        accept.prev.put("", Arrays.asList(new State[]{initial}));

        List<State> newData = new ArrayList<>();
        newData.add(initial);
        newData.addAll(nfa.data);
        newData.add(accept);

        HashSet<String> alpha = new HashSet<>();
        alpha.addAll(nfa.alphabet);
        return new NFA(newData, initial, accept,alpha);
    }

    public static NFA alterNFANoCopy(NFA nfa1, NFA nfa2) {
        if (nfa1 == null || nfa2 == null) {
            System.out.println("cannot alter null NFA");
            return null;
        }

        State init = new State();
        State accept = new State();
        List<State> data = new ArrayList<>();
        data.add(init);
        data.addAll(nfa1.data);
        data.addAll(nfa2.data);
        data.add(accept);

        init.next.put("", Arrays.asList(new State[]{nfa1.initState, nfa2.initState}));

        List<State> preList = nfa1.initState.prev.get("");
        if (preList == null) {
            preList = new ArrayList<>();
            preList.add(init);
            nfa1.initState.prev.put("", preList);
        } else {
            preList.add(init);
        }

        preList = nfa2.initState.prev.get("");
        if (preList == null) {
            preList = new ArrayList<>();
            preList.add(init);
            nfa2.initState.prev.put("", preList);
        } else {
            preList.add(init);
        }

        List<State> tmpNext = nfa1.acceptState.next.get("");
        if (tmpNext == null) {
            tmpNext = new ArrayList<>();
            tmpNext.add(accept);
            nfa1.acceptState.next.put("", tmpNext);
        } else {
            tmpNext.add(accept);
        }

        tmpNext = nfa2.acceptState.next.get("");
        if (tmpNext == null) {
            tmpNext = new ArrayList<>();
            tmpNext.add(accept);
            nfa2.acceptState.next.put("", tmpNext);
        } else {
            tmpNext.add(accept);
        }

        HashSet<String> alpha = new HashSet<>();
        alpha.addAll(nfa1.alphabet);
        alpha.addAll(nfa2.alphabet);
        accept.prev.put("",  Arrays.asList(new State[]{nfa1.acceptState, nfa2.acceptState}));

        return new NFA(data, init, accept, alpha);
    }
    public static NFA copyNFA(NFA nfa) {
        HashMap<State, State> map = new HashMap<>();
        HashSet<State> visited = new HashSet<>();
        Deque<State> queue = new ArrayDeque<>();

        State cur = nfa.initState;
        queue.offer(cur);
        visited.add(cur);

        //BFS
        while(!queue.isEmpty()) {
            State state = queue.poll();
            if (!map.containsKey(state)) {
                map.put(state, new State());
            }

            HashMap<String, List<State>> copyNext = new HashMap<>();
            map.get(state).next = copyNext;

            for (Map.Entry<String, List<State>> entry : state.next.entrySet()) {
                List<State> copyList = new ArrayList<>();
                for (State s : entry.getValue()) {
                    if (!visited.contains(s)) {
                        visited.add(s);
                        queue.offer(s);
                        map.put(s, new State());
                    }

                    if (!map.get(s).prev.containsKey(entry.getKey())) {
                        List<State> tmp = new ArrayList<>();
                        tmp.add(map.get(state));
                        map.get(s).prev.put(entry.getKey(), tmp);
                    } else {
                        List<State> tmpList = map.get(s).prev.get(entry.getKey());
                        tmpList.add(map.get(state));
                    }

                    copyList.add(map.get(s));

                }
                copyNext.put(entry.getKey(), copyList);
            }
        }

        List<State> copyData = new ArrayList<>();
        State copyInit = map.get(nfa.initState);
        State copyAccept = map.get(nfa.acceptState);
        copyData.add(copyInit);

        for (Map.Entry<State, State> entry : map.entrySet()) {
            if(entry.getKey() != nfa.initState && entry.getKey() != nfa.acceptState) {
                copyData.add(entry.getValue());
            }
        }
        copyData.add(copyAccept);
        HashSet<String> newAlpha = new HashSet<>();
        newAlpha.addAll(nfa.alphabet);
        return new NFA(copyData, copyInit, copyAccept, newAlpha);
    }

    public static NFA reverseNFA(NFA nfa) {
        NFA reverseNFA = copyNFA(nfa);
        swapInitAcceptState(reverseNFA);
        for (State s : reverseNFA.getData()) {
            HashMap<String, List<State>> tmp = s.prev;
            s.prev = s.next;
            s.next = tmp;
        }
        return reverseNFA;
    }

    public static NFA reverseDFA(DFA dfa) {
        DFA reverseDFA = copyDFA(dfa);
        swapInitAcceptState(reverseDFA);
        for (State s : reverseDFA.getData()) {
            HashMap<String, List<State>> tmp = s.prev;
            s.prev = s.next;
            s.next = tmp;
        }
        return new NFA(reverseDFA.data, reverseDFA.initState, reverseDFA.acceptState, reverseDFA.alphabet, reverseDFA.initStates);
    }

    private static void swapInitAcceptState(FA fa) {
        if (fa.acceptStates.size() > 1) {
            Set<State> newInitStates = new HashSet<>();
            for (State acceptState : fa.acceptStates) {
                newInitStates.add(acceptState);
            }
            fa.initStates = newInitStates;
        }

        State tmp = fa.initState;
        fa.initState = fa.acceptState;
        fa.acceptState = tmp;
        Set<State> newAcceptStates = new HashSet<>();
        newAcceptStates.add(fa.acceptState);
        fa.acceptStates = newAcceptStates;

    }

    public static DFA copyDFA(DFA dfa) {
        HashMap<State, State> map = new HashMap<>();
        HashSet<State> visited = new HashSet<>();
        Deque<State> queue = new ArrayDeque<>();

        State cur = dfa.initState;
        queue.offer(cur);
        visited.add(cur);

        //BFS
        while(!queue.isEmpty()) {
            State state = queue.poll();
            if (!map.containsKey(state)) {
                map.put(state, new State());
            }

            HashMap<String, List<State>> copyNext = new HashMap<>();
            map.get(state).next = copyNext;

            for (Map.Entry<String, List<State>> entry : state.next.entrySet()) {
                List<State> copyList = new ArrayList<>();
                for (State s : entry.getValue()) {
                    if (!visited.contains(s)) {
                        visited.add(s);
                        queue.offer(s);
                        map.put(s, new State());
                    }

                    if (!map.get(s).prev.containsKey(entry.getKey())) {
                        List<State> tmp = new ArrayList<>();
                        tmp.add(map.get(state));
                        map.get(s).prev.put(entry.getKey(), tmp);
                    } else {
                        List<State> tmpList = map.get(s).prev.get(entry.getKey());
                        tmpList.add(map.get(state));
                    }

                    copyList.add(map.get(s));

                }
                copyNext.put(entry.getKey(), copyList);
            }
        }

        List<State> copyData = new ArrayList<>();
        State copyInit = map.get(dfa.initState);
        State copyAcceptState = map.get(dfa.acceptState);

        Set<State> copyAcceptStates = new HashSet<>();

        for (State acceptState : dfa.acceptStates) {
            copyAcceptStates.add(map.get(acceptState));
        }

        copyData.add(copyInit);

        for (Map.Entry<State, State> entry : map.entrySet()) {
            if(entry.getKey() != dfa.initState && !dfa.acceptStates.contains(entry.getKey())) {
                copyData.add(entry.getValue());
            }
        }

        for (State copyAccept : copyAcceptStates) {
            if (copyAccept != copyInit) {
                copyData.add(copyAccept);
            }
        }

        HashSet<String> newAlpha = new HashSet<>();
        newAlpha.addAll(dfa.alphabet);
        DFA result =  new DFA(copyData, copyInit, copyAcceptState, newAlpha);
        result.acceptStates = copyAcceptStates;
        return result;
    }
}
