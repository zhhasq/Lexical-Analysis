package edu.unm.lexer.fa;


import java.lang.reflect.Array;
import java.util.*;

public abstract class FA {
    List<State> data;
    State initState;
    State acceptState;
    Set<String> alphabet;
    Set<State> acceptStates = new HashSet<>();
    Set<State> initStates; //for Brzozowski's algorithm

    public abstract State mov(State curState, String c);

    public boolean isAccpeted(State state) {
        return acceptState == state;
    }


    public State getInitState() {
        return this.initState;
    }

    public int generateID() {
        //return how many states in the FA
        int i = 0;
        for (State s : data) {
            s.setID(i++);
        }
        return i;
    }
    public State getState(Integer id) {
        for (State s : this.data) {
            if (s.getID() == id) {
                return s;
            }
        }
        return null;
    }
    public List<State> getData() {
        return this.data;
    }

    @Override
    public String toString() {
        generateID();
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();
        Map<State, Integer> visited = new HashMap<State, Integer>();
        visited.put(initState, -1);
        DFS(this.initState, new StringBuilder(), result, visited, new HashSet<State>());
        sb.append("initial State: " + this.initState.getIDString());
        sb.append(System.getProperty("line.separator"));
        sb.append("accept State: ");
        for (State s : acceptStates) {
            sb.append(s.getIDString() + " ");
        }
        sb.append(System.getProperty("line.separator"));
        for (String s : result) {
           sb.append(s);
           sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public void DFS(State s, StringBuilder sb, List<String> result, Map<State, Integer> visited, Set<State> visited2) {
        Integer count = visited.get(s);
        if (count == null) {
            visited.put(s, 1);
        } else {
            visited.put(s, ++count);
        }

        if (s.next.isEmpty() || (visited.get(s) == countPreState(s) && visited.get(s) > 1) || visited2.contains(s)) {
            sb.append("-->" + "[" + s.getIDString() + "]");
            result.add(sb.toString());
            sb.delete(sb.length() - 3 - 2 - s.getIDString().length(), sb.length());
            return;
        }

        sb.append("-->" + "[" + s.getIDString() + "]");
        visited2.add(s);
        for(Map.Entry<String, List<State>> entry : s.next.entrySet()) {
            String condition = entry.getKey();
            if (condition.equals("")) {
                condition = "" + (char)(949);
            }
            sb.append("--" + condition);
            for (State state : entry.getValue()) {
                if (s.equals(state)) {
                    //self loop
                    sb.append("-->" + "[" + s.getIDString() + "]");
                    result.add(sb.toString());
                    sb.delete(sb.length() - 3 - 2 - s.getIDString().length(), sb.length());
                    Integer tmp = visited.get(s);
                    visited.put(s, ++tmp);
                } else if (state.equals(initState)) {
                    sb.append("-->" + "[" + state.getIDString() + "]");
                    result.add(sb.toString());
                    sb.delete(sb.length() - 3 - 2 - state.getIDString().length(), sb.length());
                    Integer tmp = visited.get(state);
                    visited.put(s, ++tmp);
                } else {
                    DFS(state, sb, result, visited, visited2);
                }
            }
            sb.delete(sb.length() - 2 - condition.length(), sb.length());
        }
        sb.delete(sb.length() -3 - 2 - s.getIDString().length(), sb.length());
    }

    private Integer countPreState(State s) {
        int result = 0;
        for (Map.Entry<String, List<State>> entry : s.prev.entrySet()) {
            if (entry.getValue() != null) {
                for (State state : entry.getValue()) {
                    result++;
                }
            }
        }
        return result;
    }

    private Integer countNextState(State s) {
        int result = 0;
        for (Map.Entry<String, List<State>> entry : s.next.entrySet()) {
            if (entry.getValue() != null) {
                for (State state : entry.getValue()) {
                    result++;
                }
            }
        }
        return result;
    }

}
