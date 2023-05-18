package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;

import java.util.ArrayList;
import java.util.List;

/** An intermediate representation for expressions */
public abstract class IRExpr_c extends IRNode_c implements IRExpr {

    private boolean and;
    private boolean or;
    private int shortCircuitFTVal; // What boolean expression evaluates to in fall-through case
    private String label;

    // for value numbering
    public int number = -1;

    public int setNumber(int i) {
        number = i;
        return i + 1;
    }

    public int number() {
        return number;
    }

    public boolean contains(IRExpr t) {
        // System.out.println("Warning: using default implementation of contains() for "
        // + toString());
        return false;
    }

    public IRExpr get(IRExpr t) {
        // System.out.println("Warning: using default implementation of get() for " +
        // toString());
        return null;
    };

    /** Default valueNumbering implementation */
    public int valueNumbering(int i) {
        // System.out.println("Warning: using default implementation of valueNumbering()
        // for " + toString());
        return i;

    }

    public List<IRTemp> temps() {
        // System.out.println("Warning: using default implementation of temps() for " +
        // toString());
        return new ArrayList<>();

    };

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v) {
        return v.enterExpr();
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return v.inExpr() || !v.inExp();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public long constant() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setShortCircuit(String sc) {
        if (sc.equals("and")) {
            and = true;
        } else if (sc.equals("or") || sc.equals("not")) {
            or = true;
        }
    }

    public void setShortCircuitFTVal(int ftVal) {
        this.shortCircuitFTVal = ftVal;
    }

    public int getShortCircuitFTVal() {
        return shortCircuitFTVal;
    }

    public int getOppShortCircuitFTVal() {
        return shortCircuitFTVal == 1 ? 0 : 1;
    }

    public boolean isShortCircuit() {
        return and || or;
    }

    public boolean isAnd() {
        return and;
    }

    public boolean isOr() {
        return or;
    }

    public IRESeq lower(IRNodeFactory factory) {
        return factory.IRESeq(factory.IRSeq(), this);
    }

    public boolean isMem() {
        return false;
    }

    public abstract String toString();

    public void setLabel(String label, IRNodeFactory factory) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
