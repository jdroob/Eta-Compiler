package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.types.BoolType;
import main.java.jdr299zdh5cew256ans96.types.Type;

/**
 * AST Node representing the boolean type class for declaring boolean types
 */
public class BoolTypeNode extends TypeNode {

	/**
	 * Constructor for creating a node in the AST for a boolean type
	 * @param pos - position of bool type in program file
	 */
	public BoolTypeNode(String pos) {
		super("bool",pos);
	}

	/**
	 * Function to type check an AST node in the tree.
	 *
	 * @param c - Context that represents the symbol table of storing variables
	 * @return the type associated with the AST node after type checking
	 */
	@Override
	public Type typeCheck(Context c) {
		BoolType boolType = new BoolType();
		setNodeType(boolType);
		return boolType;
	}
}
