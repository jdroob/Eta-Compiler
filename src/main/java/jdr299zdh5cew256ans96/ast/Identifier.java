package main.java.jdr299zdh5cew256ans96.ast;

import main.java.jdr299zdh5cew256ans96.ir.IRExpr;
import main.java.jdr299zdh5cew256ans96.ir.IRNodeFactory;
import main.java.jdr299zdh5cew256ans96.ir.IRTemp;
import main.java.jdr299zdh5cew256ans96.types.Type;
import main.java.jdr299zdh5cew256ans96.types.UnitType;
import main.java.jdr299zdh5cew256ans96.util.edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;

import java.util.Optional;

/**
 * AST Node for identifiers. They can be used anywhere expressions can be.
 */
public class Identifier extends Expression {

  /**
   * Identifier name
   */
  private String id;
  private boolean global;

  /**
   * Constructor for creating a new identifier
   * @param id - name of identifier
   * @param pos - position of identifier in program file
   */
  public Identifier(String id, String pos) {
    super(pos);
    this.id = id;
  }

  /**
   * Constructor for creating a new identifier without an associated position
   * @param id - name of identifier
   */
  public Identifier(String id) {
    super("");
    this.id = id;
  }

  /**
   *
   * @return name of identifier
   */
  public String getName() {
    return id;
  }

  /**
   * Function to type check an AST node in the tree.
   *
   * @param c - Context that represents the symbol table of storing variables
   * @return the type associated with the AST node after type checking
   * @throws SemanticError if the AST node does not type check
   */
  @Override
  public Type typeCheck(Context c) throws SemanticError{
    // throw error here if not found in context
    if (id.equals("_")) {
      UnitType unitType = new UnitType();
      setNodeType(unitType);
      return unitType;
    } else {
      global = isGlobal(c);
      Optional<Type> type = c.get(this);
      if (type.isEmpty()) {
        throw new SemanticError(getPos()+" error: " +
                "Name "+id+" cannot be resolved");
      } else {
        Type setNodeType = type.get();
        setNodeType(setNodeType);
        return setNodeType;
      }
    }
  }

  public boolean isGlobal(Context c) {
    return c.isGlobal(id);
  }

  public boolean global() { return global; }

  public IRExpr translate(IRNodeFactory factory) {
    /**
     * just pass name of identifier to IRTemp constructor and return it
     */
    if (global) {
      return factory.IRMem(factory.IRName("_"+id));
    } else {
      IRTemp temp = factory.IRTemp(id);
      if (id.equals("_")) {
        temp.setValid(false);
      }
      return temp;
    }
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
    c.printAtom(id);
    return c;
  }

}