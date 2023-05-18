package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRCompUnit;
import main.java.jdr299zdh5cew256ans96.ir.IRData;
import main.java.jdr299zdh5cew256ans96.ir.IRFuncDecl;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * AST Node for a program. The bottom up parser ultimately constructs this AST
 * Node to denote a successful program
 */
public class Program extends Node {

	/**
	 * Attributes that define a program including a list of use statements, a
	 * list of definitions, and boolean variables that specify details of
	 * program
	 */
	private ArrayList<Use> uses;
	private ArrayList<Method> methods;
	private ArrayList<Global> declarations;
	private ArrayList<Record> records;
	private ArrayList<Definition> defs;
	private String fileName;
	private boolean hasUse;
	private boolean hasDef;
	private boolean hasRec;
	private boolean isEti;
	private boolean isRi;
	private boolean error;
	private String errorMessage;

	private static HashMap<String, IRData> strLiterals = new HashMap<>();

	/**
	 * Constructor for creating a program using a list of uses and definitions
	 * 
	 * @param uses  - list of uses
	 * @param defs  - list of definitions
	 * @param isEti - whether program is eti or eta file
	 * @param pos   - start position of program
	 */

	public Program(ArrayList<Use> uses, ArrayList<Definition> defs,
			boolean isEti, String pos) {
		super(pos);

		this.defs = defs;
		methods = new ArrayList<>();
		declarations = new ArrayList<>();

		hasUse = uses.size() != 0;
		hasDef = defs.size() != 0;
		this.uses = uses;
		/**
		 * Separate methods and global declarations
		 * Both are initially returned as definitions (in defs) after parsing
		 * For irGen, we would like to have two separate lists: methods and declarations
		 * since method.translate() is significantly different from
		 * declaration.translate()
		 */
		for (Definition d : defs) {
			if (d.isMethod()) {
				methods.add((Method) d);
			} else if (!(d instanceof Record)) {
				declarations.add((Global) d);
			}
		}
		this.isEti = isEti;
	}

	/**
	 * Constructor for creating an empty program
	 * 
	 * @param pos - start position of program
	 */
	public Program(String pos) {
		super(pos);
		uses = new ArrayList<>();
		methods = new ArrayList<>();
		declarations = new ArrayList<>();
		hasUse = false;
		hasDef = false;
		isEti = false;
	}

	/**
	 * Constructor for creating an error program
	 * 
	 * @param error        - whether program has an error
	 * @param errorMessage - error message for program
	 */
	public Program(boolean error, String errorMessage) {
		super("0:0");
		this.error = error;
		this.errorMessage = errorMessage;
	}

	public static void addStringLiteral(String pos, IRData strLit) {
		strLiterals.put(pos, strLit);
	}

	public static IRData getStringLiteral(String pos) {
		return strLiterals.get(pos);
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
		for (Use u : uses) {
			u.typeCheck(c);
		}

		for (Definition d : defs) {
			d.add(c);
		}

		// assert there is at least one method in the context
		c.hasFunction();

		for (Definition d : defs) {
			d.typeCheck(c);
		}

		UnitType unitType = new UnitType();
		setNodeType(unitType);
		return unitType;
	}

	public IRCompUnit translate(IRNodeFactory factory) {
		HashMap<String, IRFuncDecl> irFuncs = new HashMap<>();
		for (Method m : methods) {
			irFuncs.put(m.getAbiName(), m.translate(factory));
		}

		if (declarations.isEmpty() && strLiterals.isEmpty()) {
			return factory.IRCompUnit(fileName, irFuncs);
		} else {
			HashMap<String, IRData> irData = new HashMap<>();
			int i = 0; // allows us to generate unique key for irData
			for (Global d : declarations) {
				ArrayList<IRData> irDataNodes = d.translate(factory);
				for (IRData data : irDataNodes) {
					irData.put(i + "", data);
					i++;
				}
			}

			for (String pos : strLiterals.keySet()) {
				irData.put(i + "", strLiterals.get(pos));
				i++;
			}

			return new IRCompUnit(fileName, irFuncs,
					new ArrayList<>(), irData, null);
		}

	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 *
	 * @return list of definitions that are in the program
	 */
	public ArrayList<Definition> getDefs() {
		return defs;
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
		if (error) {
			c.printAtom(getPos() + " error: ");
			c.printAtom(errorMessage);
		} else {
			c.startUnifiedList();

			if (!isEti) {
				if (!hasUse) {
					c.startList();
					c.endList();
				} else {
					c.startList();
					for (Use u : uses) {
						u.prettyPrint(c);
					}
					c.endList();
				}
			}

			if (!hasDef) {
				c.startList();
				c.endList();
			} else {
				c.startList();
				for (Definition n : defs) {
					n.prettyPrint(c);
				}
				c.endList();
			}

			c.endList();
		}

		return c;
	}
}