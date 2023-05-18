package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRBinOp;
import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

public class FieldAccess extends Expression {

	private Identifier id;
	private Identifier field;

	public FieldAccess(Identifier id, Identifier field) {
		super(id.getPos());
		this.id = id;
		this.field = field;
	}

	@Override
	public IRExpr translate(IRNodeFactory factory) {
		RecordType recordType = (RecordType) id.getStoredType();
		int offset = recordType.getOffset(field);
		IRExpr recordTemp = id.translate(factory);
		return factory.IRMem(
				factory.IRBinOp(
						IRBinOp.OpType.ADD,
						recordTemp,
						factory.IRConst(offset)
				)
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
		Type type = id.typeCheck(c);
		if (type instanceof RecordType r) {
			Type fieldAccessType = r.getArgType(field);
			setNodeType(fieldAccessType);
			return fieldAccessType;
		} else {
			throw new SemanticError(getPos() +
					" error: cannot access fields to non record type");
		}
	}
}
