package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.Optional;

public class Field extends Expression {

	private TypeNode type;
	private ArrayList<Identifier> ids;

	public Field(String id, TypeNode type, String pos) {
		super(pos);
		this.type = type;
		ArrayList<Identifier> identifiers = new ArrayList<>();
		identifiers.add(new Identifier(id));
		this.ids = identifiers;
	}

	public Field(Parameter p) {
		super(p.getPos());
		ArrayList<Identifier> identifiers = new ArrayList<>();
		identifiers.add(p.getId());
		this.ids = identifiers;
		this.type = p.getType();
	}

	public Field(ArrayList<Identifier> ids, TypeNode type) {
		super(ids.get(0).getPos());
		this.ids = ids;
		this.type = type;
	}

	public ArrayList<Identifier> getIds() {
		return ids;
	}

	public TypeNode getType() {
		return type;
	}

	@Override
	public IRExpr translate(IRNodeFactory factory) {
		return null;
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
		return null;
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
		Type nodeType = type.typeCheck(c);
		setNodeType(nodeType);
		return nodeType;

	}
}
