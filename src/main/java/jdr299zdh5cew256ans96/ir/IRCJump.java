package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Jmp;
import main.java.jdr299zdh5cew256ans96.assembly.Test;
import main.java.jdr299zdh5cew256ans96.assembly.TempStack;
import main.java.jdr299zdh5cew256ans96.tiles.Tile;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * An intermediate representation for a conditional transfer of control
 * CJUMP(expr, trueLabel,
 * falseLabel)
 */
public class IRCJump extends IRStmt {
    private IRExpr cond;
    private String trueLabel, falseLabel;

    /**
     * Construct a CJUMP instruction with fall-through on false.
     *
     * @param cond      the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates to
     *                  true
     */
    public IRCJump(IRExpr cond, String trueLabel) {
        this(cond, trueLabel, null);
    }

    /**
     * @param cond       the condition for the jump
     * @param trueLabel  the destination of the jump if {@code expr} evaluates to
     *                   true
     * @param falseLabel the destination of the jump if {@code expr} evaluates to
     *                   false
     */
    public IRCJump(IRExpr cond, String trueLabel, String falseLabel) {
        this.cond = cond;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    public IRExpr cond() {
        return cond;
    }

    public String trueLabel() {
        return trueLabel;
    }

    public String falseLabel() {
        return falseLabel;
    }

    public boolean hasFalseLabel() {
        return falseLabel != null;
    }

    public List<IRTemp> temps() {
        return cond.temps();

    }

    @Override
    public String label() {
        return "CJUMP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.cond);

        if (expr != this.cond)
            return v.nodeFactory().IRCJump(expr, trueLabel, falseLabel);

        return this;
    }

    @Override
    public Assembly recursiveMunch(ArrayList<IRNode> recursiveChildren, Tile tile) {
        IRNode child = recursiveChildren.get(0);

        Assembly a = child.munch();

        Assembly cjmp = new Assembly();
        cjmp.addInstruction(new Test(a.getShuttleTemp(), a.getShuttleTemp()));
        cjmp.addInstruction(new Jmp("jnz", trueLabel));

        Assembly total = new Assembly();
        total.addInstructions(a);
        total.addInstructions(cjmp);
        return total;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(cond));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !hasFalseLabel();
    }

    public IRStmt lower(IRNodeFactory factory) {
        IRESeq eseq = cond.lower(factory);
        IRStmt sideEffectStmts = eseq.stmt();
        IRExpr pureExpr = eseq.expr();

        return factory.IRSeq(sideEffectStmts, factory.IRCJump(pureExpr, trueLabel, falseLabel));
    }

    public String targetString() {
        return trueLabel;
    }

    public String toString() {
        String s = "CJUMP_" + cond.toString() + "_" + trueLabel;
        if (hasFalseLabel()) {
            s = s + "_" + falseLabel;
        }
        return s;
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("CJUMP");
        cond.printSExp(p);
        p.printAtom(trueLabel);
        if (hasFalseLabel())
            p.printAtom(falseLabel);
        p.endList();
    }
}
