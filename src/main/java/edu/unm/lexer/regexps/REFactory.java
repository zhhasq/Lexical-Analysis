package edu.unm.lexer.regexps;

import edu.unm.lexer.fa.FAFactory;
import edu.unm.lexer.fa.FAUtils;
import edu.unm.lexer.fa.NFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static edu.unm.lexer.regexps.REUtils.alterAll;

public class REFactory {

    public static RE buildSingleCharRE(char c) {
        RE result = null;
        if (c =='_') {
            result = new RE(c +"'_", new RENode("_"));
        } else {
            result = result = new RE(c +"", new RENode(c + ""));
        }
        return result;
    }
    public static RE buildEmptyRE() {
        RE result = new RE("_", new RENode(""));
        return result;
    }
    public static RE concatRE(RE a, RE b) {
        RE result = new RE("+" + a.preFixExpression + b.preFixExpression, concatTree(a.root, b.root));
        return result;
    }

    public static RE alterRE(RE a, RE b) {
        RE result = new RE("|" + a.preFixExpression + b.preFixExpression, alterTree(a.root, b.root));
        return result;
    }

    public static RE starRE(RE a) {
        RE result =  new RE("*" + a.preFixExpression, starTree(a.root));
        return result;
    }

    public static RE concatRENoCopy(RE a, RE b) {
        RE result = new RE("+" + a.preFixExpression + b.preFixExpression, concatTreeNoCopy(a.root, b.root));
        return result;
    }

    public static RE alterRENoCopy(RE a, RE b) {
        RE result = new RE("|" + a.preFixExpression + b.preFixExpression, alterTreeNoCopy(a.root, b.root));
        return result;
    }

    public static RE starRENoCopy(RE a) {
        RE result =  new RE("*" + a.preFixExpression, starTreeNoCopy(a.root));
        return result;
    }

    private static RENode concatTree(RENode root1, RENode root2) {
        root1 = copyTree(root1);
        root2 = copyTree(root2);
        return concatTreeNoCopy(root1, root2);
    }

    private static RENode alterTree(RENode root1, RENode root2) {
        root1 = copyTree(root1);
        root2 = copyTree(root2);
        return alterTreeNoCopy(root1, root2);
    }

    private static RENode starTree(RENode root1) {
        root1 = copyTree(root1);
        return starTreeNoCopy(root1);
    }

    private static RENode concatTreeNoCopy(RENode root1, RENode root2) {
        RENode root = new RENode("+");
        root.left = root1;
        root.right = root2;
        return root;
    }

    private static RENode alterTreeNoCopy(RENode root1, RENode root2) {

        RENode root = new RENode("|");
        root.left = root1;
        root.right = root2;
        return root;
    }

    private static RENode starTreeNoCopy(RENode root1) {
        RENode root = new RENode("*");
        root.left = root1;
        return root;
    }

    public static RENode alterAllNode(List<RENode> list) {
        if (list == null) {
            return null;
        }
        RENode root = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            RENode cur = new RENode("|");
            cur.left = root;
            cur.right = list.get(i);
            root = cur;
        }
        return root;
    }


    public static RENode copyTree(RENode root) {
        RENode newRoot = new RENode(root.key);
        copyTreeHelper(root, newRoot);
        return newRoot;
    }
    private static void copyTreeHelper(RENode root1, RENode root2) {
        if (root1.left != null) {
            root2.left = new RENode(root1.left.key);
            copyTreeHelper(root1.left, root2.left);
        }
        if (root1.right != null) {
            root2.right = new RENode(root1.right.key);
            copyTreeHelper(root1.right, root2.right);
        }
    }

    public static RE generateASCiiRE() {
        //non empty string of
        //subsets of the printable ASCii which is [32 to 126]
        List<RENode> letterREList = new ArrayList<>();
        char c = ' ';

        for (Integer i = 0; i < 95; i++) {
            char tmp = (char)(c + i);
            if (tmp != '*' && tmp != '|' && tmp!= '+' && tmp != '\'' && tmp != '_') {
                letterREList.add(new RENode(tmp + ""));
            } else {
                letterREList.add(new RENode("'" + tmp));
            }
        }
        RENode root = alterAllNode(letterREList);
        return new RE("ascii", root);
        //result = REFactory.concatRE(result, REFactory.starRE(result));
    }
}
