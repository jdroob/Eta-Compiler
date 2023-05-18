package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.types.IntType;
import main.java.jdr299zdh5cew256ans96.types.Type;

/**
 * AST Node representing the int type class for declaring int types
 */
public class IntTypeNode extends TypeNode {

	/**
	 * Constructor for creating a node in the AST for an int type
	 * @param pos - position of int type in program file
	 */
	public IntTypeNode(String pos) {
		super("int",pos);
	}

	/**
	 * Function to type check an AST node in the tree.
	 *
	 * @param c - Context that represents the symbol table of storing variables
	 * @return the type associated with the AST node after type checking
	 */
	@Override
	public Type typeCheck(Context c) throws SemanticError {
		IntType intType = new IntType();
		setNodeType(intType);
		return intType;
	}
}
