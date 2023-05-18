package main.java.jdr299zdh5cew256ans96.types;

import main.java.jdr299zdh5cew256ans96.ast.Field;
import main.java.jdr299zdh5cew256ans96.ast.Identifier;
import main.java.jdr299zdh5cew256ans96.ast.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordType extends Type {
	private HashMap<String,Integer> memOffsets;
	private Identifier name;
	private HashMap<String,Type> fields;
	private ArrayList<Type> types;

	public RecordType(Identifier name, ArrayList<Identifier> fields,
	                  ArrayList<Type> types) {
		super(name.getName());
		HashMap<String,Type> map = new HashMap<>();
		for (int i=0;i<fields.size();i++) {
			map.put(fields.get(i).getName(),types.get(i));
		}

		HashMap<String,Integer> offsetMap = new HashMap<>();
		int offset = 8;
		for (Identifier id: fields) {
			offsetMap.put(id.getName(),offset);
			offset += 8;
		}

		this.memOffsets = offsetMap;
		this.fields = map;
		this.types = types;
		this.name = name;
	}

	public RecordType(Identifier name) {
		super(name.getName());
		this.name = name;
		this.fields = new HashMap<>();
		this.types = new ArrayList<>();
		this.memOffsets = new HashMap<>();

	}

	public int getNumFields() {
		return fields.size();
	}

	public int getOffset(Identifier id) {
		return memOffsets.get(id.getName());
	}

	public Type getArgType(Identifier field) throws SemanticError {
		Type argType = fields.get(field.getName());
		if (argType == null) {
			throw new SemanticError(field.getPos() + " error: field name "+field.getName()+
					" does not exist for "+name.getName()+" record");
		}
		return fields.get(field.getName());
	}

	public Type getArgType(int index) {
		return types.get(index);
	}


}
