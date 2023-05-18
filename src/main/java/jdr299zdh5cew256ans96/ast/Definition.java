package main.java.jdr299zdh5cew256ans96.ast;

/**
 * AST Node for definitions. There are many different
 * types of definitions in Eta, so this class cannot actually
 * be instantiated so it is abstract
 */
public abstract class Definition extends Node {
	
	public Definition(String pos) {
		super(pos);
	}

	/**
	 * Method for adding this definition to context before type checking it.
	 * This is done to allow for programs to use this definition before
	 * it is declared
	 * @param c - context symbol table where this definition will be added
	 * @throws SemanticError when there is an error putting definition into the
	 * context
	 */
	public abstract void add(Context c) throws SemanticError;

	public abstract boolean isMethod();

}