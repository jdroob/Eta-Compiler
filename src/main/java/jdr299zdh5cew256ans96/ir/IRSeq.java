package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An intermediate representation for a sequence of statements SEQ(s1,...,sn)
 */
public class IRSeq extends IRStmt {
    private List<IRStmt> stmts;

    /** @param stmts the statements */
    public IRSeq(IRStmt... stmts) {
        this(Arrays.asList(stmts));
    }

    /**
     * Create a SEQ from a list of statements. The list should not be modified
     * subsequently.
     *
     * @param stmts the sequence of statements
     */
    public IRSeq(List<IRStmt> stmts) {
        ArrayList<IRStmt> validStmts = new ArrayList<>();
        for (IRStmt irStmt : stmts) {
            if (irStmt.isValid()) {
                validStmts.add(irStmt);
            }
        }

        this.stmts = validStmts;
    }

    public List<IRStmt> stmts() {
        return stmts;
    }

    @Override
    public String label() {
        return "SEQ";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        List<IRStmt> results = new ArrayList<>(stmts.size());
        for (IRStmt stmt : stmts) {
            IRStmt newStmt = (IRStmt) v.visit(this, stmt);
            if (newStmt != stmt)
                modified = true;
            results.add(newStmt);
        }

        if (modified)
            return v.nodeFactory().IRSeq(results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRStmt stmt : stmts)
            result = v.bind(result, v.visit(stmt));
        return result;
    }

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v) {
        return v.enterSeq();
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inSeq();
    }

    public String targetString() {
        return "targetString() in IRSeq";
    }

    public String toString() {
        return "SEQ";
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startUnifiedList();
        p.printAtom("SEQ");
        for (IRStmt stmt : stmts)
            stmt.printSExp(p);
        p.endList();
    }

    @Override
    public boolean isNestedSeq() {
        return true;
    }

    @Override
    public IRStmt lower(IRNodeFactory factory) {
        List<IRStmt> singleStmts = stmts;
        while (hasNestedStmts(singleStmts)) { // write a flatten() function?
            List<IRStmt> newSingleStmts = new ArrayList<>();
            for (IRStmt s : singleStmts) {
                if (s.isNestedSeq()) {
                    IRSeq seq = (IRSeq) s;
                    List<IRStmt> seqStmts = seq.stmts;
                    newSingleStmts.addAll(seqStmts);
                } else {
                    newSingleStmts.add(s);
                }
            }
            singleStmts = newSingleStmts;
        }

        ArrayList<IRStmt> noNestedStmts = new ArrayList<>();
        for (IRStmt stmt : singleStmts) {
            noNestedStmts.add(stmt.lower(factory));
        }

        List<IRStmt> flattenedSeq = noNestedStmts;
        while (hasNestedStmts(flattenedSeq)) { // write a flatten() function?
            List<IRStmt> newSingleStmts = new ArrayList<>();
            for (IRStmt s : flattenedSeq) {
                if (s.isNestedSeq()) {
                    IRSeq seq = (IRSeq) s;
                    List<IRStmt> seqStmts = seq.stmts;
                    newSingleStmts.addAll(seqStmts);
                } else {
                    newSingleStmts.add(s);
                }
            }
            flattenedSeq = newSingleStmts;
        }

        return factory.IRSeq(flattenedSeq);
    }

    private boolean hasNestedStmts(List<IRStmt> stmts) {
        for (IRStmt stmt : stmts) {
            if (stmt.isNestedSeq()) {
                return true;
            }
        }
        return false;
    }

    /*
     * Purpose of this method is to eliminate code duplication in:
     * localDecAssign, IdAssign, functionCall, procedureCall, Return
     * if, while, localMultiAssign are special cases this method currently
     * does not support
     */
    public static IRSeq getShortCircuitSeq(IRStmt eseqStmt, IRExpr temp, IRExpr irExpr,
            String endLabel, String currLabel) {
        return new IRSeq(
                eseqStmt,
                new IRMove(temp,
                        new IRConst(irExpr.getShortCircuitFTVal())),
                new IRJump(new IRName(endLabel)),
                new IRLabel(currLabel),
                new IRMove(temp,
                        new IRConst(irExpr.getOppShortCircuitFTVal())),
                new IRLabel(endLabel));
    }
}
