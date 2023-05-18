package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import main.java.jdr299zdh5cew256ans96.types.Type;

/**
 * Parent class for all type nodes. This type hierarchy is for anytime a type
 * is declared in the program language. This type hierarchy is separate from
 * the type hierarchy used for type checking
 */
public abstract class TypeNode extends Node {

	/**
	 * Which type is being declared
	 */
	private String type;

	/**
	 * Constructor for creating a type node object
	 * 
	 * @param t   - name of type being declared
	 * @param pos - position of declaration type in program file
	 */
	public TypeNode(String t, String pos) {
		super(pos);
		type = t;
	}

	public String getABIName() {
		return type.charAt(0) + "";
	}

	/**
	 * Pretty printing function to print parsed AST node to file. Different
	 * AST nodes pretty print differently, so this method is just a stub
	 *
	 * @param c - printer object that is used to pretty print node
	 * @return the printer object after it is modified with node's pretty print
	 */
	@Override
	public CodeWriterSExpPrinter prettyPrint(CodeWriterSExpPrinter c) {
		c.printAtom(type);
		return c;
	}

	/**
	 * Function to type check an AST node in the tree.
	 *
	 * @param c - Context that represents the symbol table of storing variables
	 * @return the type associated with the AST node after type checking
	 * @throws SemanticError if the AST node does not type check
	 */
	@Override
	public abstract Type typeCheck(Context c) throws SemanticError;

	/**
	 *
	 * @return name of type being declared
	 */
	public String getType() {
		return type;
	}

}
