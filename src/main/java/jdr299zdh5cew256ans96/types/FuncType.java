package main.java.jdr299zdh5cew256ans96.types;

import main.java.jdr299zdh5cew256ans96.ast.Context;
import main.java.jdr299zdh5cew256ans96.ast.Identifier;
import main.java.jdr299zdh5cew256ans96.ast.Parameter;

import java.util.ArrayList;
import java.util.Optional;

public class FuncType extends Type {

	private ArrayList<Parameter> arguments;
	private ArrayList<Type> returnTypes;
	private Identifier name;

	public FuncType(Identifier name, ArrayList<Parameter> arguments, ArrayList<Type> returnTypes) {
		super("function");
		this.arguments = arguments;
		this.returnTypes = returnTypes;
		this.name = name;

	}

	public ArrayList<Parameter> getArguments() {
		return arguments;
	}

	public ArrayList<Type> getReturnTypes() {
		return returnTypes;
	}

	public Identifier getIdentifier() { return name; }

	public Optional<Type> typeCheck(Context c) {
		return Optional.empty();
	}

	public String getABIName() {
		String abiMethod = "_I"+name.getName()+"_";
		if (returnTypes.isEmpty()) {
			abiMethod += "p";
		} else if (returnTypes.size() == 1) {
			abiMethod += returnTypes.get(0).getABIName();
		} else {
			abiMethod += "t"+returnTypes.size();
			for (Type type : returnTypes) {
				abiMethod += type.getABIName();
			}
		}

		for (Parameter parameter : arguments) {
			abiMethod += parameter.getType().getABIName();
		}
		return abiMethod;
	}
}
