package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.assembly.MemRegister;
import main.java.jdr299zdh5cew256ans96.assembly.Mov;
import main.java.jdr299zdh5cew256ans96.assembly.Register;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.tiles.Tile;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.InternalCompilerError;

import java.util.ArrayList;
import java.util.List;

/** An intermediate representation for a memory location MEM(e) */
public class IRMem extends IRExpr_c {
    public enum MemType {
        NORMAL,
        IMMUTABLE;

        @Override
        public String toString() {
            switch (this) {
                case NORMAL:
                    return "MEM";
                case IMMUTABLE:
                    return "MEM_I";
            }
            throw new InternalCompilerError("Unknown mem type!");
        }
    };

    private IRExpr expr;
    private MemType memType;

    /** @param expr the address of this memory location */
    public IRMem(IRExpr expr) {
        this(expr, MemType.NORMAL);
    }

    public IRMem(IRExpr expr, MemType memType) {
        this.expr = expr;
        this.memType = memType;
    }

    public IRExpr expr() {
        return expr;
    }

    public List<IRTemp> temps() {
        return expr.temps();
    }

    public boolean contains(IRExpr e) {
        return expr.contains(e);
    }

    public IRExpr get(IRExpr e) {
        return expr.get(e);
    }

    public MemType memType() {
        return memType;
    }

    @Override
    public String label() {
        return memType.toString();
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr)
            return v.nodeFactory().IRMem(expr);

        return this;
    }

    @Override
    public IRESeq lower(IRNodeFactory factory) {
        IRESeq eseq = expr.lower(factory);
        IRStmt sideEffect = eseq.stmt();
        IRExpr pureExpr = eseq.expr();
        return factory.IRESeq(sideEffect, factory.IRMem(pureExpr));
    }

    @Override
    public Assembly recursiveMunch(ArrayList<IRNode> recursiveChildren, Tile tile) {
        IRNode child = recursiveChildren.get(0);

        Assembly a = child.munch();

        String newShuttleTemp = Assembly.generateFreshTemp();

        Assembly mem = new Assembly(new Mov(new Register(newShuttleTemp),
                new MemRegister(a.getShuttleTemp())));

        Assembly total = new Assembly();
        total.addInstructions(a);
        total.addInstructions(mem);
        total.setShuttleTemp(newShuttleTemp);
        return total;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(expr));
        return result;
    }

    public String toString() {
        return memType.toString() + expr.toString();
    }

    @Override
    public void printSExp(CodeWriterSExpPrinter p) {
        p.startList();
        p.printAtom(memType.toString());
        expr.printSExp(p);
        p.endList();
    }

    public boolean isMem() {
        return true;
    }
}
