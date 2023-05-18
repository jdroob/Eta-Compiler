package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.types.RecordType;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

public class Record extends Definition {

	private Identifier id;
	private ArrayList<Field> fieldList;

	public Record(String id, String pos) {
		super(pos);
		this.id = new Identifier(id);
		this.fieldList = new ArrayList<>();
	}

	public Record(String id, ArrayList<Field> fieldList, String pos) {
		super(pos);
		this.id = new Identifier(id);
		this.fieldList = fieldList;
	}

	/**
	 * Method for adding this definition to context before type checking it.
	 * This is done to allow for programs to use this definition before
	 * it is declared
	 *
	 * @param c - context symbol table where this definition will be added
	 * @throws SemanticError when there is an error putting definition into the
	 *                       context
	 */
	@Override
	public void add(Context c) throws SemanticError {
		c.put(id, getRecordType(c));
	}

	@Override
	public boolean isMethod() {
		return false;
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
		ArrayList<String> fields = new ArrayList<>();
		id.typeCheck(c);
		for (Field field : fieldList) {
			for (Identifier i : field.getIds()) {
				if (fields.contains(i.getName())) {
					throw new SemanticError(field.getPos() + " error: cannot have duplicate field names in record");
				} else {
					fields.add(i.getName());
				}
			}

			field.typeCheck(c);
		}

		RecordType recordType = getRecordType(c);
		setNodeType(recordType);
		return recordType;
	}

	private RecordType getRecordType(Context c) throws SemanticError {
		ArrayList<Type> types = new ArrayList<>();
		ArrayList<Identifier> fields = new ArrayList<>();
		for (Field f : fieldList) {
			for (Identifier i : f.getIds()) {
				fields.add(i);
				if (f.getType() instanceof RecordTypeNode r) {
					if (r.getId().equals(id.getName())) {
						types.add(new RecordType(new Identifier(r.getId())));
					} else {
						types.add(f.getType().typeCheck(c));
					}
				} else {
					types.add(f.getType().typeCheck(c));
				}
			}
		}

		return new RecordType(id,fields,types);
	}

}
