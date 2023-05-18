package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * An intermediate representation for evaluating an expression for side effects,
 * discarding the
 * result EXP(e)
 */
public class IRExp extends IRStmt {
    private IRExpr expr;

    /** @param expr the expression to be evaluated and result discarded */
    public IRExp(IRExpr expr) {
        this.expr = expr;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "EXP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr)
            return v.nodeFactory().IRExp(expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v) {
        return v.enterExp();
    }

    public String targetString() {
        return "targetString() in IRExp";
    }

    public String toString() {
        return "EXP_" + expr.toString();
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("EXP");
        expr.printSExp(p);
        p.endList();
    }
}
