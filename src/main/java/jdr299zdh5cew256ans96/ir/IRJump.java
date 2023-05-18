package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.Jmp;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/** An intermediate representation for a transfer of control */
public class IRJump extends IRStmt {
    private IRExpr target;

    /** @param expr the destination of the jump */
    public IRJump(IRExpr expr) {
        target = expr;
    }

    public IRExpr target() {
        return target;
    }

    @Override
    public String label() {
        return "JUMP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, target);

        if (expr != target)
            return v.nodeFactory().IRJump(expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        return result;
    }

    @Override
    public IRStmt lower(IRNodeFactory factory) {
        IRESeq eseq = target.lower(factory);
        IRStmt sideEffect = eseq.stmt();
        IRExpr pureExpr = eseq.expr();
        return factory.IRSeq(sideEffect, factory.IRJump(pureExpr));
    }

    @Override
    public Assembly munch() {
        IRName name = (IRName) target;
        return new Assembly(new Jmp("jmp", name.name()));
    }

    public String targetString() {
        return target.toString();
    }

    public String toString() {
        return "JUMP_" + target.toString();
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("JUMP");
        target.printSExp(p);
        p.endList();
    }
}
