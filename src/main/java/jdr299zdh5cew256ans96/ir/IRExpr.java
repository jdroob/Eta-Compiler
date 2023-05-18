package main.java.jdr299zdh5cew256ans96.ir;

import java.util.List;

public interface IRExpr extends IRNode {

    /** Sets the value number for this expression. Returns i+1 */
    int setNumber(int i);

    int number();

    boolean isConstant();

    /** number the values in this expression, starting at number i */
    int valueNumbering(int i);

    /** If this expression contains an instance of t, return it. */
    IRExpr get(IRExpr t);

    /** Return a list of temps used in this expression */
    List<IRTemp> temps();

    /**
     * Returns true if this expression contains an instance of t,
     * or false otherwise.
     */
    boolean contains(IRExpr t);

    long constant();

    void setShortCircuit(String shortCircuit);

    boolean isShortCircuit();

    boolean isAnd();

    boolean isOr();

    void setShortCircuitFTVal(int ftVal);

    int getShortCircuitFTVal();

    int getOppShortCircuitFTVal();

    IRESeq lower(IRNodeFactory factory);

    boolean isMem();

    void setLabel(String label, IRNodeFactory factory);

    String getLabel();
}
