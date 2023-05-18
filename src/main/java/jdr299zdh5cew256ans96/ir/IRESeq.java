package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * An intermediate representation for an expression evaluated under side effects
 * ESEQ(stmt, expr)
 */
public class IRESeq extends IRExpr_c {
    private IRStmt stmt;
    private IRExpr expr;

    /**
     * @param stmt IR statement to be evaluated for side effects
     * @param expr IR expression to be evaluated after {@code stmt}
     */
    public IRESeq(IRStmt stmt, IRExpr expr) {
        this.stmt = stmt;
        this.expr = expr;
    }

    public IRStmt stmt() {
        return stmt;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "ESEQ";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRStmt stmt = (IRStmt) v.visit(this, this.stmt);
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr || stmt != this.stmt)
            return v.nodeFactory().IRESeq(stmt, expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(stmt));
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public IRESeq lower(IRNodeFactory factory) {
        IRStmt loweredStmt = stmt.lower(factory);
        IRESeq eseq = expr.lower(factory);
        IRExpr pureExpr = eseq.expr();
        IRStmt sideEffectStmts = eseq.stmt();
        return factory.IRESeq(factory.IRSeq(loweredStmt, sideEffectStmts), pureExpr);
    }

    public static IRESeq getShortCircuitForm(IRExpr testExpr1, IRExpr testExpr2,
            String shortCircuitLabel, IRExpr expr,
            IRNodeFactory factory) {
        // All true labels are there for Basic Block purposes only
        if (testExpr2 != null) {
            IRStmt shortCircuitLogic = new IRSeq(
                    new IRCJump(testExpr1, shortCircuitLabel),
                    new IRLabel(factory.generateFreshTrueLabel()),
                    new IRCJump(testExpr2, shortCircuitLabel),
                    new IRLabel(factory.generateFreshTrueLabel()));
            return new IRESeq(shortCircuitLogic, expr);
        }
        IRStmt unopShortCircuitLogic = new IRSeq(
                new IRCJump(testExpr1, shortCircuitLabel),
                new IRLabel(factory.generateFreshTrueLabel()));
        return new IRESeq(unopShortCircuitLogic, expr);
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return false;
    }

    public String toString() {
        return label();
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("ESEQ");
        stmt.printSExp(p);
        expr.printSExp(p);
        p.endList();
    }
}
