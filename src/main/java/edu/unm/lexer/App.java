package edu.unm.lexer;

import edu.unm.lexer.fa.FA;
import edu.unm.lexer.fa.FAFactory;
import edu.unm.lexer.fa.FAUtils;
import edu.unm.lexer.fa.NFA;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        NFA r = FAFactory.buildSingleCharNFA('a');
        r.generateID();
       // System.out.println(r.toString());

        NFA r2 = FAFactory.buildSingleCharNFA('c');
        r2.generateID();
       // System.out.println(r2.toString());

        NFA r3 = FAFactory.concatNFA(r, r2);
        r3.generateID();
        System.out.println(r3.toString());
        System.out.println(r3.toStringReverse());

//        System.out.println(r.toString());
//        System.out.println(r.toStringReverse());
//
//        System.out.println(r2.toString());
//        System.out.println(r2.toStringReverse());

//        r3.generateID();
//        String s = r3.toString();
//        System.out.println(s);

        NFA r4 = FAFactory.alterNFA(r, r2);
        r4.generateID();
        System.out.println(r4.toString());
        System.out.println(r4.toStringReverse());

        NFA r5 = FAFactory.concatNFA(r3, r4);
        r5.generateID();
        System.out.println(r5.toString());
        System.out.println(r5.toStringReverse());

        NFA r6 = FAFactory.makeNFAStar(r);
        NFA r7 = r6.copyNFA();
        r6.generateID();
        r7.generateID();

        //System.out.println(r6.toString());
      //  System.out.println(r6.toStringReverse());

    }
}
