package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRData;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.ArrayList;

/**
 * AST Node for global declarations. It can be used anywhere
 * where a definition can be used. A singular declaration
 * is just an identifier and a type, which is wrapped in a
 * parameter class.
 */
public class GlobalDeclaration extends Global {

    /**
     * identifier and colon in the global declaration
     */
    private Parameter parameter;

    /**
     * Constructor for creating a global declaration with an id and a type
     * 
     * @param parameter - id and type in declaration
     * @param pos       - position of declaration in program file
     */
    public GlobalDeclaration(Parameter parameter, String pos) {
        super(pos);
        this.parameter = parameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public String getName() {
        return parameter.getName();
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
        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;
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
        TypeNode paramType = parameter.getType();
        Type type = paramType.typeCheck(c);
        c.put(parameter.getId(), type);
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
        c.startUnifiedList();
        parameter.prettyPrint(c);
        c.endList();
        return c;
    }

    public boolean isMethod() {
        return false;
    }

    public ArrayList<IRData> translate(IRNodeFactory factory) {
        ArrayList<IRData> dataVals = new ArrayList<>();
        dataVals.add(new IRData("_" + parameter.getName(), new long[0]));
        return dataVals;
    }

}