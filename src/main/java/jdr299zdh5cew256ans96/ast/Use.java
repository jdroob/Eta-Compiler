package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.HelperCli;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

/**
 * AST Node for use statements. All use statements must be declared at the
 * top of the program file before any and all method / global
 * variable declarations
 */
public class Use extends Node {

    /**
     * name of interface file to be imported
     */
    private Identifier id;
    private static String libPath;
    private boolean isRi;

    /**
     * Constructor for creating a use object
     * @param id - interface file name
     * @param pos - position of use statement in program file
     */
    public Use(String id, String pos) {
        super(pos);
        this.id = new Identifier(id, pos);
        this.isRi = false;
    }

    public Use(String id, boolean isRi, String pos) {
        super(pos);
        this.id = new Identifier(id, pos);
        this.isRi = isRi;
    }

    public static void setLibPath(String libDir) {
        libPath = libDir;
    }

    public void setRi(boolean ri) {
        isRi = ri;
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

        try {
            HelperCli.addInterfaceMethods(c, id.getName(), libPath);
//            System.out.println("ID: "+id.getName()+", "+libPath);
        } catch (SemanticError s) {
            throw new SemanticError(id.getPos()+" error: " +
                    "Name "+id.getName()+" cannot be resolved");
        }


        UnitType unitType = new UnitType();
        setNodeType(unitType);
        return unitType;
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
        c.printAtom("use");
        c.printAtom(id.getName());
        c.endList();
        return c;
    }
}