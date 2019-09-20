package edu.unm.lexer.fa;


import java.util.*;

public abstract class FA {
    List<State> data;
    State initState;
    State acceptState;

    public abstract State mov(State curState, String c);

    public boolean isAccpeted(State state) {
        return acceptState == state;
    }

    public State getInitState() {
        return this.initState;
    }

    public void generateID() {
        int i = 0;
        for (State s : data) {
            s.setID(i++);
        }
    }

    public List<State> getData() {
        return this.data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();
        DFS(this.initState, new StringBuilder(), result);
        sb.append("initial State: " + this.initState.getID());
        sb.append(System.getProperty("line.separator"));
        sb.append("accept State: " + this.acceptState.getID());
        sb.append(System.getProperty("line.separator"));
        for (String s : result) {
           sb.append(s);
           sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public String toStringReverse() {
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();
        DFSReverse(this.acceptState, new StringBuilder(), result);
        sb.append("initial State: " + this.initState.getID());
        sb.append(System.getProperty("line.separator"));
        sb.append("accept State: " + this.acceptState.getID());
        sb.append(System.getProperty("line.separator"));
        for (String s : result) {
            sb.append(s);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public void DFS(State s, StringBuilder sb, List<String> result) {
        if (s.next.isEmpty()) {
            sb.append("-->" + "[" + s.getID() + "]");
            result.add(sb.toString());
            sb.delete(sb.length() - 3 - 2 - s.getID().toString().length(), sb.length());
            return;
        }
        sb.append("-->" + "[" + s.getID() + "]");
        for(Map.Entry<String, List<State>> entry : s.next.entrySet()) {
            String condition = entry.getKey();
            if (condition.equals("")) {
                condition = "`e";
            }
            sb.append("--" + condition);
            for (State state : entry.getValue()) {
                DFS(state, sb, result);
            }
            sb.delete(sb.length() - 2 - condition.length(), sb.length());
        }
        sb.delete(sb.length() -3 - 2 - s.getID().toString().length(), sb.length());
    }

    public void DFSReverse(State s, StringBuilder sb, List<String> result) {
        //to check if the prev HashMap correct
        if (s.prev.isEmpty()) {
            sb.append("[" + s.getID() + "]" + "<--");
            result.add(sb.toString());
            sb.delete(sb.length()- 2 - 3 - s.getID().toString().length(), sb.length());
            return;
        }
        sb.append("[" + s.getID() + "]" + "<--");

        for(Map.Entry<String, List<State>> entry : s.prev.entrySet()) {
            String condition = entry.getKey();
            if (condition.equals("")) {
                condition = "`e";
            }
            sb.append(condition + "--" );
            for (State state : entry.getValue()) {
                DFSReverse(state, sb, result);
            }
            sb.delete(sb.length() - 2 - condition.length(), sb.length());
        }
        sb.delete(sb.length() -3 - 2 - s.getID().toString().length(), sb.length());
    }

    public NFA copyNFA() {
        HashMap<State, State> map = new HashMap<>();
        HashSet<State> visited = new HashSet<>();
        Deque<State> queue = new ArrayDeque<>();

        State cur = this.initState;
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
        State copyInit = map.get(this.initState);
        State copyAccept = map.get(this.acceptState);
        copyData.add(copyInit);

        for (Map.Entry<State, State> entry : map.entrySet()) {
            if(entry.getKey() != this.initState && entry.getKey() != this.acceptState) {
                copyData.add(entry.getValue());
            }
        }
        copyData.add(copyAccept);
        return new NFA(copyData, copyInit, copyAccept);
    }
}
