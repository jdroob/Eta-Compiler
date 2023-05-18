package main.java.jdr299zdh5cew256ans96.types;

import java.util.ArrayList;

public class ReturnType extends Type {

	private ArrayList<Type> returnTypes;

	public ReturnType(ArrayList<Type> rt) {
		super("return");
		this.returnTypes = rt;	// initialize returnTypes with another ArrayList of types
	}

	public ArrayList<Type> getReturnTypes() {
		return returnTypes;
	}

	public boolean isEmpty() {
		return returnTypes.size() == 0;
	}

	public boolean equals(ReturnType r){
		ArrayList<Type> r2 = r.getReturnTypes();
		int i = 0;
		for(Type t: returnTypes){
			if(!t.equals(r2.get(i))){
				return false;
			}
			i++;
		}
		return true;
	}

}
