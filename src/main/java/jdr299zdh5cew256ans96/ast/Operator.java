package main.java.jdr299zdh5cew256ans96.ast;

/**
 * Operator class for creating enum operators
 */
public class Operator {

  /**
   * Enums for binary operators
   */
  public enum BinaryOperator {

    PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"), MODULO("%"), LT("<"),
    LEQ("<="), GT(">"), GEQ(">="), AND("&"),
    OR("|"), NOT_EQUAL("!="), EQUALS("=="), HIGH_MULT("*>>");

    /**
     * String value for binary operator
     */
    public final String op;

    /**
     * Constructor for setting binary operator
     * @param op - operator string
     */
    BinaryOperator(String op) {
      this.op = op;
    }

    /**
     *
     * @return string value for binary operator
     */
    public String toString() {
      return op;
    }

  }

  /**
   * Enums for unary operators
   */
  public enum UnaryOperator {

    MINUS("-"), NOT("!");

    /**
     * String value for unary operator
     */
    public final String op;

    /**
     * Constructor for setting unary operator
     * @param op - operator string
     */
    UnaryOperator(String op) {
      this.op = op;
    }

    /**
     *
     * @return string value for binary operator
     */
    public String toString() {
      return op;
    }
  }

}