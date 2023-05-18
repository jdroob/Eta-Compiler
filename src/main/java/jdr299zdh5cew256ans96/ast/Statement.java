package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Statement class for statements inside of blocks. There are many different
 * kind of statements, so this class is abstract since it can never be
 * instantiated
 */
public abstract class Statement extends Node {

	/**
	 * Constructor for creating statement object
	 * @param pos - position of statement in program file
	 */
	public Statement(String pos) {
		super(pos);
	}

	/**
	 * Pretty printing function to print parsed AST node to file. Different
	 * AST nodes pretty print differently, so this method is just a stub
	 *
	 * @param c - printer object that is used to pretty print node
	 * @return the printer object after it is modified with node's pretty print
	 */
	@Override
	public abstract CodeWriterSExpPrinter prettyPrint(CodeWriterSExpPrinter c);

	public abstract IRStmt translate(IRNodeFactory factory);
		
}
