package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.CallStack;
import main.java.jdr299zdh5cew256ans96.assembly.Leave;
import main.java.jdr299zdh5cew256ans96.assembly.Ret;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** RETURN statement */
public class IRReturn extends IRStmt {
    protected List<IRExpr> rets;

    public IRReturn() {
        this(new ArrayList<>());
    }

    /** @param rets values to return */
    public IRReturn(IRExpr... rets) {
        this(Arrays.asList(rets));
    }

    /** @param rets values to return */
    public IRReturn(List<IRExpr> rets) {
        this.rets = rets;
    }

    public List<IRExpr> rets() {
        return rets;
    }

    @Override
    public String label() {
        return "RETURN";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        List<IRExpr> results = new ArrayList<>(rets.size());

        for (IRExpr ret : rets) {
            IRExpr newExpr = (IRExpr) v.visit(this, ret);
            if (newExpr != ret)
                modified = true;
            results.add(newExpr);
        }

        if (modified)
            return v.nodeFactory().IRReturn(results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRExpr ret : rets)
            result = v.bind(result, v.visit(ret));
        return result;
    }

    @Override
    public Assembly munch() {
        Assembly epilogue = new Assembly();
        epilogue.addInstruction(new Leave());
        epilogue.addInstruction(new Ret());
        CallStack.setMultipleReturns(false);
        CallStack.setOddStackSpilled(false);
        return epilogue;
    }

    @Override
    public IRStmt lower(IRNodeFactory factory) {
        ArrayList<IRStmt> seq = new ArrayList<>();
        ArrayList<IRExpr> freshTemps = new ArrayList<>();
        for (IRExpr ret : rets) {
            IRESeq eseq = ret.lower(factory);
            IRStmt sideEffect = eseq.stmt();
            IRExpr pureExpr = eseq.expr();
            seq.add(sideEffect);
            IRTemp freshTemp = factory.generateFreshTemp();
            freshTemps.add(freshTemp);
            seq.add(factory.IRMove(freshTemp, pureExpr));
        }

        seq.add(factory.IRReturn(freshTemps));

        return factory.IRSeq(seq);

    }

    public String targetString() {
        return "targetString() in IRReturn";
    }

    public String toString() {
        String s = "RETURN";
        for (IRExpr ret : rets)
            s = s + "_" + ret.toString();
        return s;
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom("RETURN");
        for (IRExpr ret : rets)
            ret.printSExp(p);
        p.endList();
    }
}