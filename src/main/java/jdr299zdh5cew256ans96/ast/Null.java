package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRMove;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRStmt;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.types.NullType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Null extends Expression {

	public Null(String pos) {
		super(pos);
	}

	@Override
	public IRExpr translate(IRNodeFactory factory) {
		ArrayList<IRStmt> moveList = new ArrayList<>();
		IRTemp nullPtr = factory.generateFreshTemp();

		IRMove mem0 = factory.IRMove(nullPtr,
				factory.IRMem(factory.IRConst(0)));
		moveList.add(mem0);

		return factory.IRESeq(
				factory.IRSeq(moveList),
				nullPtr
		);
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
		NullType type = new NullType();
		setNodeType(type);
		return type;
	}
}
