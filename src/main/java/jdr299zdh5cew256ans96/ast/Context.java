package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.types.Type;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Optional;

/**
 * Symbol table that represents all the definitions (method and variables) in
 * each scope of the program.
 */
public class Context {

	/**
	 * The symbol table is represented as a stack of hash tables,
	 * where each stack is a scope in the program. The hash table in each scope
	 * maps identifier names to their types
	 */
	private Deque<HashMap<String, Type>> context;

	/**
	 * Constructor for creating a symbol table. Initially, we push a hashtable
	 * onto the stack that represents the global scope
	 */
	public Context() {
		HashMap<String,Type> globalScope = new HashMap<>();
		context = new ArrayDeque<>();
		context.push(globalScope);
	}

	/**
	 * Function for creating a new scope in a program, which is represented
	 * by pushing a new hashtable onto the stack
	 */
	public void push() {
		HashMap<String,Type> scope = new HashMap<>();
		context.push(scope);
	}

	public boolean isGlobal(String id)  {
		HashMap<String, Type> globalScope = context.peekLast();
		HashMap<String,Type> curScope = context.peekFirst();
		return globalScope.containsKey(id) && !curScope.containsKey(id);
	}

	/**
	 * Function for leaving the current scope of a program, which is
	 * represented by popping the top hashtable off of the stack
	 */
	public void pop() {
		context.pop();
	}

	/**
	 * Function for retrieving the type of an identifier from the symbol table
	 * @param id - identifier to be looked up in the symbol table
	 * @return the type of the id
	 */
	public Optional<Type> get(Identifier id) {
		for (HashMap<String, Type> scope : context) {
			Type result = scope.get(id.getName());
			if (result != null) {
				return Optional.of(result);
			}
		}
		return Optional.empty();
	}

	/**
	 * Function to check the symbol table for a function
	 * @throws SemanticError if there isn't at least one function in the
	 * symbol table
	 */
	public void hasFunction() throws SemanticError {
		if (context.isEmpty()) {
			throw new SemanticError("0:0 error: eta programs must have " +
					"at least one function");
		} else {
			HashMap<String,Type> globalScope = context.peek();
			for (String id : globalScope.keySet()) {
				if (globalScope.get(id).getType().equals("function")) {
					return;
				}
			}
		}

		throw new SemanticError("0:0 error: eta programs must have " +
				"at least one function");

	}

	/**
	 * Function to put an identifier into the symbol table along with its
	 * assigned type
	 * @param id - identifier to put into the context
	 * @param type - type of the identifier stored in the context
	 * @throws SemanticError if duplicate identifier is in context or an
	 * invalid identifier name tries to get entered
	 */
	public void put(Identifier id, Type type) throws SemanticError {
		if (!context.isEmpty()) {
			HashMap<String,Type> curScope = context.peek();
			if (curScope.containsKey(id.getName())) {
				throw new SemanticError(id.getPos()+" error: cannot " +
						"declare duplicate id "+id.getName());
			}
			if (id.getName().equals("_")) {
				throw new SemanticError(id.getPos()+" error: cannot " +
						"assign a type to _");
			}
			curScope.put(id.getName(), type);
		}
	}


}
