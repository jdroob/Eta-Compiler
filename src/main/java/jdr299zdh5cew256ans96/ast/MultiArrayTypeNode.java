package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.types.MultiArrayType;
import main.java.jdr299zdh5cew256ans96.types.Type;

import java.util.Optional;

/**
 * AST Node representing the multi array type class for declaring multi array
 * types
 */
public class MultiArrayTypeNode extends ArrayTypeNode {

	/**
	 * Content that is inside second bracket content of multi array type
	 * declaration
	 */
	private Optional<Node> bracketContent2;

	/**
	 * Constructor for creating a node in the AST for a multi array type
	 * 
	 * @param type            - type of multi array
	 * @param pos             - position where multi array type is declared in
	 *                        program file
	 * @param bracketContent1 - content in first bracket of multi array
	 * @param bracketContent2 - content in second bracket of multi array
	 */
	public MultiArrayTypeNode(String type, String pos, Node bracketContent1,
			Node bracketContent2) {
		super(type, pos, bracketContent1);
		if (bracketContent2 == null) {
			this.bracketContent2 = Optional.empty();
		} else {
			this.bracketContent2 = Optional.of(bracketContent2);
		}
	}

	@Override
	public String getABIName() {
		return "aa" + getType().charAt(0);
	}

	public boolean isContentNull2() {
		return bracketContent2.isEmpty();
	}

	public Optional<Node> getBracketContent2() {
		return bracketContent2;
	}

	/**
	 * Function to type check an AST node in the tree.
	 *
	 * @param c - Context that represents the symbol table of storing variables
	 * @return the type associated with the AST node after type checking
	 * @throws SemanticError if the AST node does not type check
	 */
	@Override
	public Type typeCheck(Context c) throws SemanticError {

		if (!isContentNull() || bracketContent2.isPresent()) {
			Node index1;
			Node index2;
			if (!isContentNull()) {
				index1 = getBracketContent().get();
				if (!index1.typeCheck(c).equalsStr("int")) {
					throw new SemanticError(index1.getPos() + " error: index must be of type int");
				}
			}
			if (bracketContent2.isPresent()) {
				if (isContentNull()) {
					throw new SemanticError(getPos() + " error: " +
							"error: must provide 1st length to empty 2-D array initialization");
				}
				index2 = bracketContent2.get();
				if (!index2.typeCheck(c).equalsStr("int")) {
					throw new SemanticError(index2.getPos() + " error: index must be of type int");
				}
			}
		}

		MultiArrayType multiArrayType = new MultiArrayType(getType());
		setNodeType(multiArrayType);
		return multiArrayType;
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
		c.startUnifiedList();
		c.printAtom("[]");
		c.startList();
		c.printAtom("[]");
		c.printAtom(getType());
		bracketContent2.ifPresent(node -> node.prettyPrint(c));
		c.endList();
		if (!isContentNull()) {
			Optional<Node> bracketContent1 = getBracketContent();
			bracketContent1.ifPresent(node -> node.prettyPrint(c));
		}
		c.endList();
		return c;
	}
}
