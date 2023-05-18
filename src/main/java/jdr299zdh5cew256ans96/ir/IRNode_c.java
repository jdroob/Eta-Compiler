package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.tiles.DummyTile;
import main.java.jdr299zdh5cew256ans96.tiles.Tile;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckConstFoldedIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.InsnMapsBuilder;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/** A node in an intermediate-representation abstract syntax tree. */
public abstract class IRNode_c implements IRNode {

    private boolean invalid;

    @Override
    public IRNode visitChildren(IRVisitor v) {
        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        return v.unit();
    }

    @Override
    public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
        return v;
    }

    @Override
    public IRNode buildInsnMaps(InsnMapsBuilder v) {
        v.addInsn(this);
        return this;
    }

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v) {
        return v;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return true;
    }

    @Override
    public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
        return true;
    }

    @Override
    public abstract String label();

    @Override
    public abstract void printSExp(CodeWriterSExpPrinter p);

    @Override
    public boolean isValid() {
        return !invalid;
    }

    @Override
    public void setValid(boolean valid) {
        this.invalid = !valid;
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw);
             CodeWriterSExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
            printSExp(sp);
        }
        return sw.toString();
    }

    @Override
    public Tile findLargestMatchingTile() {
        for (Tile t : Assembly.insnList) {
            if (t.isMatch(this)) {
//                System.out.println(t.getClass().getSimpleName());
                return t;
            }
        }
        // TODO: throw exception later here
        System.out.println("no tile match found");
        return new DummyTile();
    }

    @Override
    public Assembly recursiveMunch(ArrayList<IRNode> recursiveChildren,
                                   Tile tile) {
        return new Assembly();
    }



    @Override
    public Assembly munch() {
        Tile largestMatchingTile = findLargestMatchingTile();
        ArrayList<IRNode> recursive =
                largestMatchingTile.getRecursiveChild(this);
        if (recursive.isEmpty()) {
            return largestMatchingTile.generateAssembly(this);
        } else {
            return recursiveMunch(recursive, largestMatchingTile);
        }
    }
}