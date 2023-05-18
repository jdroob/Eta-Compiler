package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import main.java.jdr299zdh5cew256ans96.types.Type;

/**
 * Parameter class that wraps together an identifier and a type for declaring
 * variables with types
 */
public class Parameter extends Expression {

    /**
     * Identifier and type, which can be null
     */
    private TypeNode type;
    private Identifier id;

    /**
     * Constructor for creating new parameter object
     * 
     * @param id   - identifier name
     * @param type - variable type
     * @param pos  - position of parameter in program file
     */
    public Parameter(String id, TypeNode type, String pos) {
        super(pos);
        this.type = type;
        this.id = new Identifier(id, pos);
    }

    /**
     *
     * @return type of parameter
     */
    public TypeNode getType() {
        return type;
    }

    /**
     *
     * @return identifier name of parameter
     */
    public Identifier getId() {
        return id;
    }

    public String getName() {
        return id.getName();
    }

    public boolean hasType() {
        return true;
    }

    @Override
    public IRExpr translate(IRNodeFactory factory) {
        return factory.IRTemp(id.getName());
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
        c.startList();
        c.printAtom(id.getName());
        type.prettyPrint(c);
        c.endList();
        return c;
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
        Type nodeType = type.typeCheck(c);
        setNodeType(nodeType);
        return nodeType;
    }

}