package main.java.jdr299zdh5cew256ans96.ir;

import main.java.jdr299zdh5cew256ans96.assembly.Assembly;
import main.java.jdr299zdh5cew256ans96.tiles.Tile;
import main.java.jdr299zdh5cew256ans96.ir.visit.AggregateVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckCanonicalIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.CheckConstFoldedIRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.IRVisitor;
import main.java.jdr299zdh5cew256ans96.ir.visit.InsnMapsBuilder;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/** A node in an intermediate-representation abstract syntax tree. */
public interface IRNode {

    /**
     * Visit the children of this IR node.
     *
     * @param v the visitor
     * @return the result of visiting children of this node
     */
    IRNode visitChildren(IRVisitor v);

    <T> T aggregateChildren(AggregateVisitor<T> v);

    InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v);

    IRNode buildInsnMaps(InsnMapsBuilder v);

    CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v);

    boolean isCanonical(CheckCanonicalIRVisitor v);

    boolean isConstFolded(CheckConstFoldedIRVisitor v);

    String label();

    boolean isValid();

    Tile findLargestMatchingTile();

    void setValid(boolean valid);
    /**
     * Print an S-expression representation of this IR node.
     *
     * @param p the S-expression printer
     */
    void printSExp(CodeWriterSExpPrinter p);

    Assembly recursiveMunch(ArrayList<IRNode> recursiveChildren, Tile tile);

    Assembly munch();
}