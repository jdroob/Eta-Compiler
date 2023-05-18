package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.types.ArrayType;
import main.java.jdr299zdh5cew256ans96.types.Type;

import java.util.Optional;

/**
 * AST Node representing the array type class for declaring array types
 */
public class ArrayTypeNode extends TypeNode {

	/**
	 * Bracket content used when declaring an array type
	 */
	private Optional<Node> bracketContent;

	/**
	 * Constructor for creating a node in the AST for an array type
	 * 
	 * @param type           - type of array that was declared
	 * @param pos            - position of node in AST
	 * @param bracketContent - content declared inside brackets of array
	 */
	public ArrayTypeNode(String type, String pos, Node bracketContent) {
		super(type, pos);
		if (bracketContent == null) {
			this.bracketContent = Optional.empty();
		} else {
			this.bracketContent = Optional.of(bracketContent);
		}
	}

	@Override
	public String getABIName() {
		return "a" + super.getABIName();
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
		if (bracketContent.isPresent()) {
			Node index = bracketContent.get();
			if (!index.typeCheck(c).equalsStr("int")) {
				throw new SemanticError(index.getPos() + " error: index must be of type int");
			}
		}

		ArrayType arrayType = new ArrayType(getType());
		setNodeType(arrayType);
		return arrayType;
	}

	public boolean isContentNull() {
		return bracketContent.isEmpty();
	}

	public Optional<Node> getBracketContent() {
		return bracketContent;
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
		c.printAtom(getType());
		bracketContent.ifPresent(node -> node.prettyPrint(c));
		c.endList();
		return c;
	}

}
