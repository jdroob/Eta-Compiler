package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.types.IntType;
import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.Type;

import java.util.Optional;

public class RecordTypeNode extends TypeNode {

	private String id;
	private String pos;
	public RecordTypeNode(String id, String pos) {
		super(id, pos);
		this.id = id;
		this.pos = pos;
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
		Optional<Type> recordTypeOpt = c.get(new Identifier(id));
		if (recordTypeOpt.isEmpty()) {
			throw new SemanticError(pos + " error: " +
					"invalid " +
					"record field type");
		} else {
			if (recordTypeOpt.get() instanceof RecordType) {
				setNodeType(recordTypeOpt.get());
				return recordTypeOpt.get();
			} else {
				throw new SemanticError(pos + " error: " +
						"invalid " +
						"record field type");
			}
		}

	}

	public String getId() {
		return id;
	}
}
