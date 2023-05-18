package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;

/**
 * AST Node for expressions. There are many different
 * types of expressions in Eta, so this class cannot actually
 * be instantiated so it is abstract
 */
public abstract class Expression extends Node {

	public Expression(String pos) {
		super(pos);
	}

	public boolean hasType() {
		return false;
	}

	public abstract IRExpr translate(IRNodeFactory factory);

	public long[] getLiteralValue() {
		return new long[0];
	}

}