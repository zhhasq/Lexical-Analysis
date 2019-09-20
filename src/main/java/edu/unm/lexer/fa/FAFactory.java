package edu.unm.lexer.fa;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FAFactory {

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

        return new NFA(data, initState, acceptState);
    }

    public static NFA concatNFA(NFA nfa1, NFA nfa2) {
        //After concatenation, nfa1 and nfa2 are not valid anymore
        //since the State in the list will be changed.
        nfa1 = nfa1.copyNFA();
        nfa2 = nfa2.copyNFA();

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
        return new NFA(data, nfa1.initState, nfa2.acceptState);
    }

    public static NFA makeNFAStar(NFA nfa) {
        nfa = nfa.copyNFA();
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
        return new NFA(newData, initial, accept);
    }

    public static NFA alterNFA(NFA nfa1, NFA nfa2) {
        nfa1 = nfa1.copyNFA();
        nfa2 = nfa2.copyNFA();

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

        accept.prev.put("",  Arrays.asList(new State[]{nfa1.acceptState, nfa2.acceptState}));

        return new NFA(data, init, accept);
    }
    
}
