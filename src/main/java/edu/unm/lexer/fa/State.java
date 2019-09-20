package edu.unm.lexer.fa;

        import java.util.HashMap;
        import java.util.List;

public class State {

    private Integer ID = null;

    HashMap<String, List<State>> prev = new HashMap<>();
    //0 -- a --> 1
    //0 -- a --> 2
    HashMap<String, List<State>> next = new HashMap<>();

    public List<State> getNext(String s) {
        return next.get(s);
    }

    public void setID(int id) {
        this.ID = id;
    }

    public Integer getID() {
        return this.ID;
    }
    public boolean nextContainAccept(State accept, String s) {
        List<State> tmp = next.get(s);
        if (tmp == null) {
            return false;
        }
        for (State a :tmp) {
            if (a == accept) {
                return true;
            }
        }
        return false;
    }
}
