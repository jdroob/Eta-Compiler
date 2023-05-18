package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * Node class that represents the parent class for all AST Nodes. Abstract
 * class since a Node itself cannot be instantiated.
 */
public abstract class Node {

	/**
	 * position of node in program file
	 */
	private String pos;

	/**
	 * type of node in the AST
	 */
	private Type nodeType;

	/**
	 * Constructor for setting position of Node
	 * @param pos - position of AST node
	 */
	public Node(String pos) {
		this.pos = pos;
	}

	/**
	 * Pretty printing function to print parsed AST node to file. Different
	 * AST nodes pretty print differently, so this method is just a stub
	 * @param c - printer object that is used to pretty print node
	 * @return the printer object after it is modified with node's pretty print
	 */
	public abstract CodeWriterSExpPrinter prettyPrint(CodeWriterSExpPrinter c);

	/**
	 * Function to type check an AST node in the tree.
	 * @param c - Context that represents the symbol table of storing variables
	 * @return the type associated with the AST node after type checking
	 * @throws SemanticError if the AST node does not type check
	 */
	public abstract Type typeCheck(Context c) throws SemanticError;


	/**
	 * Function to set the type of node
	 * @param nodeType - type to set to AST node
	 */
	public void setNodeType(Type nodeType) {
		this.nodeType = nodeType;
	}

	public Type getStoredType() {
		return nodeType;
	}

	/**
	 *
	 * @return position of the AST node
	 */
	public String getPos(){
			return pos;
	}

}